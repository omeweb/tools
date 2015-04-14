package tools.redis;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//redis server 关闭了此客户端的连接:server端设置了maxidletime(默认是5分钟），服务端会不断循环检测clinet的最后一次通信时间（lastinteraction），如果大于maxidletime，则关闭连接，并回收相关资源。client在向该连接中写数据后就会由于server端已经关闭而出现 broken pipe的问题。

/**
 * FIFO队列，使用完redis并不关闭，由使用者自行控制；依赖于jedis（commons.pool）<br />
 * 主要使用lpush,rpop/brpop命令来操作
 * 
 * @author liusan.dyf
 */
public class RedisQueue implements Queue<String> {
	/**
	 * 2012-02-08 加入log，参见：http://zhangjunhd.blog.51cto.com/113473/25135
	 */
	private static final Log LOGGER = LogFactory.getLog(RedisQueue.class);
	/**
	 * Pool实例，这个pool很强大，即便pool里的redis被disconnect后，依然可以自动连接
	 */
	private JedisPool pool;

	/**
	 * redis list的名称
	 */
	private String queue;

	/**
	 * 阻塞状态 未设置/已经结束拉取，正在阻塞拉取，正在阻塞拉取、等待停止 2012-02-07
	 */
	private int status = 0;

	/**
	 * 接收到消息时的回调，之所以没有放构造函数里，因为这不是必须的
	 */
	private Executable callback;

	/**
	 * 一般表示服务器错误，object参数是[this,exception] 2012-02-07
	 */
	private Executable errorCallback;

	public RedisQueue(JedisPool pool, String queue) {
		// 参数检查
		if (pool == null)
			throw new IllegalArgumentException("pool不能为null");

		if (queue == null)
			throw new IllegalArgumentException("queue不能为null");

		this.setPool(pool);
		this.queue = queue;

		// // 测试连接 2012-02-07 也可以不测试，如果初始化就失败，会立即发现的
		// Jedis client = pool.getResource();
		// // client.disconnect();
		// this.pool.returnResource(client);
	}

	// getter and setter

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public Executable getCallback() {
		return callback;
	}

	public void setCallback(Executable callback) {
		this.callback = callback;
	}

	public Executable getErrorCallback() {
		return errorCallback;
	}

	/**
	 * 只处理redis服务器相关的异常 2012-02-14
	 * 
	 * @param errorCallback
	 */
	public void setErrorCallback(Executable errorCallback) {
		this.errorCallback = errorCallback;
	}

	@Override
	public String poll() {
		Jedis client = pool.getResource();
		try {
			return client.rpop(queue);
		} finally {
			pool.returnResource(client);
		}
	}

	/**
	 * 0表示无止境的等下去，直到接收到元素
	 * 
	 * @param timeout
	 * @return
	 */
	public String poll(int timeout) {
		Jedis client = pool.getResource();
		try {
			return blockPollInternal(client, timeout, queue);
		} finally {
			pool.returnResource(client);
		}
	}

	private static String blockPollInternal(Jedis client, int timeout, String queue) {
		List<String> rtn = client.brpop(timeout, queue);

		if (rtn != null && rtn.size() == 2) // 这里判断的是2
			return rtn.get(1);// 第一个是队列名称，第二个才是value
		return null;
	}

	/**
	 * 程序会阻止在这里，通过回调来通知，所以请先设置回调
	 */
	public void startBlockingPoll() {
		// 参数检查
		if (callback == null)
			throw new IllegalArgumentException("callback不能为null");

		// 看是否正在拉取
		if (status == Status.BLOCKING_POLL)
			return;

		// 正在被取消，这里取消【正在被取消】
		if (status == Status.WAITING_POLLING_STOP) {
			status = Status.BLOCKING_POLL;
			return;
		}

		// 取一个连接，专门来监听队列，主要是为了在消息频繁时省去从pool里获取redis的消耗 2012-02-07
		Jedis client = null;
		try {
			client = pool.getResource();
		} catch (JedisConnectionException e) {
			// 通知应用程序
			handleError(e);
			return;
		}

		LOGGER.info("RedisQueue[" + this.getQueue() + "]开始拉取...");

		status = Status.BLOCKING_POLL;

		// 拉取
		while (true) {
			try {
				String value = blockPollInternal(client, 0, queue);

				// 回调这里是同步的，处理速度一定要快 2012-02-07
				callback.execute(value);
			} catch (ArrayIndexOutOfBoundsException e) {
				StackTraceElement[] st = e.getStackTrace();
				// 确保这个异常是来自jedis，而不是callback里的

				// redis服务器突然挂了，则at
				// redis.clients.util.RedisOutputStream.write(RedisOutputStream.java:35)
				// 这里报ArrayIndexOutOfBoundsException异常
				if (st != null && st.length > 0) {
					if ("redis.clients.util.RedisOutputStream".equalsIgnoreCase(st[0].getClassName())) {

						// 设置状态为默认
						status = Status.DEFAULT;

						// 通知应用程序
						handleError(e);
						return;
					}
				}
			} catch (JedisConnectionException e) {
				// 设置状态为默认
				status = Status.DEFAULT;

				// 通知应用程序
				handleError(e);
				return;
			} catch (Exception e) {
				LOGGER.error(null, e);
			} finally {
				// 其他地方出异常了继续while 2012-02-06
			}

			if (status == Status.WAITING_POLLING_STOP)
				break;
		}

		// 放入池里
		pool.returnResource(client);

		// 设置状态
		status = Status.DEFAULT;
	}

	/**
	 * 标记为等待停止，并不一定会马上终止
	 */
	public void stopBlockingPoll() {
		status = Status.WAITING_POLLING_STOP;
	}

	// getter and setter

	private void handleError(Exception e) {
		LOGGER.error(null, e);

		if (errorCallback == null)
			return;

		Object[] arr = new Object[2];
		arr[0] = this;
		arr[1] = e;

		errorCallback.execute(arr);
	}

	@Override
	public int size() {
		Jedis client = pool.getResource();
		try {
			return Integer.valueOf(client.llen(queue).toString());
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		throw new NotImplementedException();
	}

	@Override
	public Iterator<String> iterator() {
		throw new NotImplementedException();
	}

	@Override
	public Object[] toArray() {
		Jedis client = pool.getResource();
		try {
			// 按照队列从左到右的顺序组成的list，实际最右边的是最先插入的元素
			List<String> rtn = client.lrange(queue, 0, -1);
			if (rtn != null)
				return rtn.toArray();
			return null;
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new NotImplementedException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// 仅保留此 collection 中那些也包含在指定 collection 的元素（可选操作）。
		// 换句话说，移除此 collection中未包含在指定 collection 中的所有元素。

		if (c == null)
			return false;

		// 清空队列
		clear();

		// 依次插入新的值
		for (Object item : c)
			if (item != null)
				add(item.toString());

		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (o == null)
			return false;

		// count = 0: Remove all elements equal to value.
		Jedis client = pool.getResource();
		try {
			return client.lrem(queue, 0, o.toString()) > 0;
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new NotImplementedException();
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
		if (c == null)
			return false;

		/*
		 * for (String item : c) add(item);
		 */

		// 这里批量插入，只申请一个连接，直到全部插入再释放 2012-02-07
		Jedis client = pool.getResource();
		try {
			for (String item : c)
				client.lpush(queue, item);
		} finally {
			pool.returnResource(client);
		}

		return true;
	}

	@Override
	public boolean add(String value) {
		return addx(value) > 0;
	}

	/**
	 * 添加成功后，返回 the number of elements inside the list after the push operation
	 * 
	 * @param value
	 * @return
	 */
	public long addx(String value) {
		Jedis client = pool.getResource();
		try {
			return client.lpush(queue, value);
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (c == null)
			return false;

		for (Object item : c)
			remove(item);

		return true;
	}

	@Override
	public void clear() {
		Jedis client = pool.getResource();
		try {
			client.del(queue);
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public boolean offer(String e) {
		return add(e);
	}

	@Override
	public String remove() {
		// Retrieves and removes the head of this queue
		return poll();
	}

	@Override
	public String element() {
		// 返回，但不移除，队列的头。
		// LRANGE mylist -1 -1 是队列末尾
		// LRANGE mylist 0 0 是队列的头
		// 参见 http://redis.io/commands/lrange

		Jedis client = pool.getResource();
		try {
			List<String> rtn = client.lrange(queue, -1, -1);// FIFO队列，第一个就是最右边的
			if (rtn != null && rtn.size() > 0)
				return rtn.get(0);
			return null;
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public String peek() {
		return element();
	}

	/**
	 * 表示阻塞状态的 2012-02-07
	 * 
	 * @author liusan.dyf
	 */
	class Status {
		/**
		 * 默认、停止拉取
		 */
		public static final int DEFAULT = 0;

		/**
		 * 正在阻塞拉取
		 */
		public static final int BLOCKING_POLL = 1;

		/**
		 * 等待阻塞拉取停止
		 */
		public static final int WAITING_POLLING_STOP = 2;
	}
}
