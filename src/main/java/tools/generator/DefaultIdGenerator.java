package tools.generator;

import java.net.MalformedURLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tools.Validate;

/**
 * 要单例运行此类
 * 
 * @author liusan.dyf
 */
@Deprecated
public class DefaultIdGenerator implements IdGenerator {
	// private Object obj = new Object();

	// 初始化一个 ReadWriteLock
	/**
	 * Lock有比synchronized更精确的线程语义和更好的性能，当许多线程都在争用同一个锁时，使用 ReentrantLock 的总体开支通常要比 synchronized 少得多。
	 * synchronized会自动释放锁，而Lock一定要求程序员手工释放，并且必须在finally从句中释放。
	 */
	private Lock locker = new ReentrantLock();

	/**
	 * 默认id的存储，是向前存储的；如果向后存储，若buffer还没有消费完，此时系统重启，则下次可能重复 2011-12-28
	 */
	private boolean storeForward = true;

	private String key;
	private long nowMaxId;

	// private long currentId;// 当前的id 2011-12-28
	private boolean isInitialized = false;
	private int increment = 1;// 2011-02-23 增加 by 杜有发

	/**
	 * 2010-08-11增加
	 */
	private int bufferSize = 20;

	/**
	 * 方案3加入，这里不再创建缓冲区，而是直接加1返回id；如果_left=0，再重新申请一批
	 */
	private int left = 0;

	/**
	 * 数据存储 2010-12-23
	 */
	private DataStore dataStore;

	public final DataStore getDataStore() {
		return dataStore;
	}

	public final void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public DefaultIdGenerator() {
	}

	// /**
	// * 使用程序当前目录作为数据存储目录
	// *
	// * @param key
	// */
	// public IdGenerator(String key) {
	// this(key, DEFAULT_BUFFER_SIZE);
	// }
	//
	// public IdGenerator(String key, int bufferSize) {
	// this(key, bufferSize, 1);
	// }

	public DefaultIdGenerator(String key, int bufferSize, int increment) {
		this.key = key;

		if (bufferSize > 0)
			this.bufferSize = bufferSize;

		if (increment != 0)
			this.increment = increment;// 2011-02-23
	}

	/**
	 * 请在Init()方法之前调用,设置初始值,慎用，产生的id从value+1开始
	 * 
	 * @param value
	 */
	public final void setInitialValue(long value) {
		checkProperties();
		locker.lock();
		try {
			// _isInitialized = false;

			// 2010-10-22 增加 by 杜有发
			nowMaxId = value;
			left = 0;

			dataStore.set(key, value);// 这里要存储，防止没有调用量时数据丢失

			// currentId = nowMaxId;// 2011-12-28
		} finally {
			locker.unlock();
		}
	}

	/**
	 * 检查key,bufferSize属性 2010-08-05增加
	 */
	void checkProperties() {
		if (Validate.isEmpty(key))
			throw new IllegalArgumentException("key属性不能为空");

		// if (!IO.IsValidFileName(key))
		// throw new Exception("key不合法，不能包含特殊字符");

		if (bufferSize < 1)
			throw new IllegalArgumentException("bufferSize值(" + bufferSize + ")不合法");

		// 判断增量信息
		if (increment == 0)
			throw new IllegalArgumentException("increment不能等于0");

		if (dataStore == null)
			throw new IllegalArgumentException("请设置DataStore属性");
	}

	/**
	 * 初始化，可以单独调用
	 */
	public final void init() {
		if (isInitialized)// 如果初始化了，则返回
			return;

		checkProperties();

		locker.lock();
		try {
			if (isInitialized)// double check
				return;

			nowMaxId = dataStore.get(key);

			isInitialized = true;

			left = 0;// 2010-08-04增加，剩余的编号

			// 设置currentId 2011-12-28
			// currentId = nowMaxId;// currentId总是等于nowMaxId - left * increment;
		} finally {
			locker.unlock();
		}
	}

	public final String getKey() {
		return key;
	}

	public final void setKey(String value) {
		if (!Validate.isEmpty(key)) {// 原来key属性有值
			if (!this.key.equals(value)) {// key属性发生了变化，要重新初始化
				key = value;
				isInitialized = false;// 标志位
			}
		} else
			key = value;
	}

	public final boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * 2011-12-22 by 63 返回当前id
	 * 
	 * @return
	 */
	public final long current() {
		init();
		// return currentId;
		return nowMaxId - left * increment;
		// locker.lock();
		// try {
		// return nowMaxId - left * increment;
		// } finally {
		// locker.unlock();
		// }
	}

	/**
	 * 得到当前最大的maxId，当storeForward时，这个值和存储的是一致的 2011-12-30
	 * 
	 * @return
	 */
	public final long currentMaxId() {
		init();
		return nowMaxId;
	}

	public final int increment() {
		return this.increment;
	}

	/**
	 * <p>
	 * 得到当前已经存储的最大的id 2011-12-30<br />
	 * storeForward ? nowMaxId : nowMaxId - this.bufferSize * this.increment;
	 * </p>
	 * 
	 * @return
	 */
	public final long currentStoredMaxId() {
		init();
		return storeForward ? nowMaxId : nowMaxId - this.bufferSize * this.increment;
	}

	/**
	 * 2011-12-26 返回【起始id:增量:次数】，包含起始id 适合大批量情况下
	 * 
	 * @param count
	 * @return
	 */
	public final String next(int count) {
		long start = 0;
		locker.lock();
		try {
			if (left == 0) {
				nextBatch(bufferSize);
			}

			left--; // 这一步很重要

			// 第一个值先得到
			start = nowMaxId - left * increment;

			int c = count - 1;

			// 改善性能 2011-12-28
			if (c <= left) { // 如果缓存池里够count-1，nowMaxId这里是不设置的
				left = left - c;
				// currentId = start + c * increment;
			} else {
				// 重新设置起始值
				nowMaxId = start + c * increment;
				left = 0;

				// 设置currentId
				// currentId总是等于nowMaxId - left * increment;
				// currentId = nowMaxId;

				// 申请下一批，并持久化
				nextBatch(bufferSize);
			}

		} finally {
			locker.unlock();
		}

		return start + ":" + increment + ":" + count;
	}

	/*
	 * 性能和bufferSize有关，bufferSize越大性能越好
	 */
	public final long next() {
		locker.lock();
		try// 2011-03-04 虽然_obj不是一个静态的对象，不过如果不加这个，的确会有并发问题存在
		{
			if (left == 0) {
				nextBatch(bufferSize);
			}

			left--;// 剩下的减一

			// return nowMaxId - left;//nowMaxId已经加上了BUFFER_SIZE

			// nowMaxId已经加上了BUFFER_SIZE 2011-02-23 增加 增量
			// long currentId = nowMaxId - left * increment;
			// return currentId;
			return nowMaxId - left * increment;
		} finally {
			locker.unlock();
		}
	}

	/**
	 * 使用该方法，一次批量申请很多id号，私有方法，该方法非线程安全的，来设置nowMaxId的，同时写入到文件 <br />
	 * 同时设置了left的值<br />
	 * 外层使用ReentrantLock，还是没有办法防止文件被别的线程锁住，这里用synchronized <br />
	 * 
	 * @param count
	 */
	private/**/synchronized void nextBatch(int count) {
		init(); // 确保已经初始化

		// lock (_obj)//取消lock，因为在取的时候，已经有一个lock了
		// {

		long nowMaxIdCopy = nowMaxId;

		nowMaxId += count * increment;// 2011-02-23增加增量 //nowMaxId +=
										// count;//重新设置nowMaxId
		// Console.WriteLine(_increment);

		// 写入文件，每次都要写入一次，免得出了意外情况
		if (storeForward)
			dataStore.set(key, nowMaxId);
		else
			dataStore.set(key, nowMaxIdCopy);

		left = bufferSize;

		// Console.WriteLine("写库了");
		// }
	}

	public static void main(String[] args) throws MalformedURLException {
		// URL u = new URL("file:///d:/t.txt");
		// System.out.println(u);// unknown protocol: d

		DefaultIdGenerator g = new DefaultIdGenerator();
		g.setKey("id");
		g.setDataStore(new FileDataStore("d:/data\\temp"));

		for (int i = 0; i < 10; i++) {
			long id = g.next();
			System.out.println(id);
		}
	}

	public boolean isStoreForward() {
		return storeForward;
	}

	public void setStoreForward(boolean storeForward) {
		this.storeForward = storeForward;
	}

	@Override
	public int getIncrement() {
		return increment;
	}
}
