package tools.counter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 如果计数器过期了，则重新从0开始计数
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-11-29
 */
public class TimedAtomicKeyedCounter extends AtomicKeyedCounter {
	private Map<String, Cutoff> cutoffs = new ConcurrentHashMap<String, Cutoff>();

	private long getTs(int ttl) {
		return System.currentTimeMillis() + ttl * 1000;
	}

	@Override
	protected AtomicLong getCounter(String key) {
		AtomicLong v = super.getCounter(key);

		// 判断时间是否过期
		if (cutoffs.containsKey(key)) {
			Cutoff entry = cutoffs.get(key);
			if (System.currentTimeMillis() >= entry.getCurrent()) {
				// 重新设置过期时间，并清空计数器
				entry.setCurrent(getTs(entry.getTtl()));
				v.set(0);
			}
		}
		return v;
	}

	public void setTtl(String key, int ttl) {
		Cutoff entry = cutoffs.get(key);
		if (entry == null || entry.getTtl() != ttl) {
			if (entry == null)
				entry = new Cutoff();

			entry.setTtl(ttl);
			entry.setCurrent(getTs(ttl));
			cutoffs.put(key, entry);
		} // else
			// System.out.println("设置ttl无效");
	}

	/**
	 * 2012-11-30 by liusan.dyf，设置过期时间
	 * 
	 * @param key
	 * @param value
	 */
	public void setCutoff(String key, long value) {
		int ttl = (int) ((value - System.currentTimeMillis()) / 1000);

		setTtl(key, ttl);
	}

	public static void main(String[] args) {
		final TimedAtomicKeyedCounter c = new TimedAtomicKeyedCounter();
		final String key = "myc";

		c.setTtl(key, 1);
		c.setTtl(key, 2);

		for (int i = 0; i < 10; i++) {
			System.out.println("got:" + c.increment(key));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		// 多线程测试ranged
		int count = 20;
		Thread[] array = new Thread[count];
		for (int i = 0; i < count; i++) {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int x = 0; x < 10; x++) {
						System.out.println(c.increment(key));

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
					}
				}
			});

			th.setDaemon(true);
			th.setName("thread_" + i);

			th.start();

			array[i] = th;
		}

		// 等待线程关闭
		for (int i = 0; i < count; i++) {
			try {
				array[i].join();
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
	}

	static class Cutoff {
		private long current;
		private int ttl;

		public long getCurrent() {
			return current;
		}

		public void setCurrent(long current) {
			this.current = current;
		}

		public int getTtl() {
			return ttl;
		}

		public void setTtl(int ttl) {
			this.ttl = ttl;
		}
	}
}
