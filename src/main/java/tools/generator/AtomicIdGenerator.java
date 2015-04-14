package tools.generator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于原子计数的id生成器
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-11-6
 */
public class AtomicIdGenerator implements IdGenerator {

	/**
	 * 默认id的存储，是向前存储的；如果向后存储，若buffer还没有消费完，此时系统重启，则下次可能重复 2011-12-28
	 */
	private boolean storeForward = true;

	private String key;

	/**
	 * 增量
	 */
	private int increment = 1;// 2011-02-23 增加 by 杜有发

	/**
	 * 2010-08-11增加
	 */
	private int bufferSize = 20;

	/**
	 * 数据存储 2010-12-23
	 */
	private DataStore dataStore;

	private AtomicLong atomicId;

	/**
	 * 操作计数
	 */
	private AtomicLong counter = new AtomicLong(0);

	public AtomicIdGenerator(String key, DataStore dataStore) {
		this(key, dataStore, 0, 0);
	}

	public AtomicIdGenerator(String key, DataStore dataStore, int bufferSize, int increment) {
		this.dataStore = dataStore;
		this.key = key;

		if (bufferSize > 0)
			this.bufferSize = bufferSize;

		if (increment != 0)
			this.increment = increment;

		init();
	}

	@Override
	public long current() {
		return atomicId.get();
	}

	@Override
	public void init() {
		// 初始化atomicId
		long currentId = 0;

		if (dataStore != null)
			currentId = dataStore.get(key);

		atomicId = new AtomicLong(currentId);
	}

	@Override
	public long next() {
		long v = atomicId.addAndGet(increment);

		// 计数器
		long count = counter.incrementAndGet();
		// if (counter.compareAndSet(count, count + 1)) {
		if ((count % bufferSize) == 0) {
			save(v);
		}
		// }

		return v;
	}

	private void save(long v) {
		if (dataStore != null)
			dataStore.set(getKey(), storeForward ? (v + (bufferSize - 1) * increment) : v);
	}

	@Override
	public String next(int count) {
		while (true) {
			long startId = next();
			long lastId = startId + increment * count;
			if (atomicId.compareAndSet(startId, lastId)) {

				// if (count >= bufferSize)
				save(lastId);

				return startId + ":" + increment + ":" + count;
			}
		}
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public synchronized void setInitialValue(long value) {
		long v = 0;
		while (true) {
			v = atomicId.get();
			if (atomicId.compareAndSet(v, value))
				return;
		}
	}

	public void setKey(String key) {
		this.key = key;
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public static void main(String[] args) {
		AtomicIdGenerator g = new AtomicIdGenerator("id", new FileDataStore("d:/data\\temp"));

		for (int i = 0; i < 50; i++) {
			long id = g.next();
			System.out.println(id);
		}
	}
}
