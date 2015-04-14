package tools.test.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import tools.redis.Executable;
import tools.redis.RedisQueue;

/**
 * 依赖commons.pool
 * 
 * @author liusan.dyf
 */
public class QueueTest {
	private static JedisPool pool;

	static {
		pool = createPool();
	}

	static JedisPool createPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(1);
		config.setMaxIdle(20);
		config.setMaxWait(1000);
		config.setTestOnBorrow(true);

		return new JedisPool(config, "127.0.0.1");
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// // 连接断了还可以继续放池里用
		// Jedis client = pool.getResource();
		// client.disconnect();
		// pool.returnResource(client);

		// 队列
		final RedisQueue q = new RedisQueue(pool, "q");
		// q.add("1");
		// q.add("2");
		// q.remove("2");
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");

		q.addAll(list);
		System.out.println("size is " + q.size());
		System.out.println("first is " + q.element());

		// 开个线程，来停止拉取
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				q.stopBlockingPoll();
			}
		});
		th.setDaemon(true);
		// th.start();

		// // 放入队列速度测试
		// long start = System.currentTimeMillis();
		// for (int i = 0; i < 100000; i++) {
		// q.add("http://...");
		// }
		// System.out.println(System.currentTimeMillis() - start);
		//
		// return;

		// 拉取
		q.setCallback(new Executable() {
			int i = 0;

			@Override
			public void execute(Object value) {
				System.out.println(value);
				i++;
				if ((i % 1000) == 0)
					System.out.println(i);
			}
		});

		q.setErrorCallback(new Executable() {
			@Override
			public void execute(Object value) {
				// 转换参数
				Object[] arr = (Object[]) value;

				// 第一个参数
				RedisQueue r = (RedisQueue) arr[0];
				r.getPool().destroy();

				// 第二个参数是异常信息
				// System.out.println(arr[1].getClass().toString());

				// 服务器突然中断了，down了 2012-02-07
				System.out.println("redis server is down,because of " + arr[1].getClass().getName());

				// 等会儿继续
				try {
					Thread.sleep(1000 * 5);
				} catch (InterruptedException e) {

				}

				// 继续连接
				r.setPool(createPool());
				q.startBlockingPoll();
			}
		});

		q.startBlockingPoll();
	}
}
