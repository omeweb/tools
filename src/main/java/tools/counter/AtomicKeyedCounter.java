package tools.counter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicKeyedCounter implements MultiKeyedCounter {
	private Map<String, AtomicLong> counters = new ConcurrentHashMap<String, AtomicLong>();

	protected AtomicLong getCounter(String key) {
		AtomicLong v = counters.get(key);
		if (v == null) {
			/*
			 * 如果你只调用get（），或只调用put（）时，ConcurrentHashMap是线程安全的。但是，在你调用完get后，调用put之前，如果有另外一个线程调用了map.put(name, x)，
			 * 你再去执行map.put(name,x)，就很可能把前面的操作结果覆盖掉了。所以，即使在线程安全的情况下，你还是有可能违反原子操作的规则。
			 */
			synchronized (counters) {
				if (!counters.containsKey(key)) {
					v = new AtomicLong();
					counters.put(key, v);
				} else
					// 2012-02-27
					v = counters.get(key);
			}
		}

		return v;
	}

	@Override
	public long get(String key) {
		return getCounter(key).get();
	}

	@Override
	public long set(String key, long v) {
		return getCounter(key).getAndSet(v);
	}

	@Override
	public long increment(String key) {
		return getCounter(key).addAndGet(1);
	}

	@Override
	public long decrement(String key) {
		return getCounter(key).decrementAndGet();
	}

	@Override
	public long increment(String key, long v) {
		return getCounter(key).addAndGet(v);
	}

	@Override
	public long decrement(String key, long v) {
		return getCounter(key).addAndGet(0 - v);
	}

	@Override
	public Map<String, Long> getAll() {
		Map<String, Long> rtn = new HashMap<String, Long>(counters.size());

		/**
		 * 有可能在此时，counters里又增加了新的KV，这样就会有java.util.ConcurrentModificationException 2013-07-17 by liusan.dyf
		 */
		// Set<Entry<String, AtomicLong>> set = counters.entrySet();
		// for (Entry<String, AtomicLong> item : set) {
		// rtn.put(item.getKey(), item.getValue().get());
		// }

		Iterator<String> iterator = counters.keySet().iterator();

		String key = null;
		AtomicLong value = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = counters.get(key);

			if (value != null)
				rtn.put(key, value.get());
		}

		return rtn;
	}

	/**
	 * 该方法目前只支持v=0的情况
	 */
	@Override
	public Map<String, Long> setAll(long v) {
		// Map<String, Long> rtn = new HashMap<String, Long>(counters.size());
		//
		// Set<Entry<String, AtomicLong>> set = counters.entrySet();
		// for (Entry<String, AtomicLong> item : set) {
		// rtn.put(item.getKey(), item.getValue().getAndSet(v));
		// }
		//
		// return rtn;

		// 2012-05-31 by liusan.dyf
		// 防止某个时间段，提交的计数太多，内存hash表被撑爆，而且一直得不到清理，这里直接清理掉

		Map<String, Long> rtn = getAll();

		synchronized (counters) {
			counters.clear();
		}

		return rtn;
	}

	public static void main(String[] args) {
		final MultiKeyedCounter c = new AtomicKeyedCounter();
		String key = "myc";
		long i = 0;
		i = c.increment(key, 102);
		i = c.decrement(key);// c.decrement(key, 10);
		System.out.println(c.get(key));

		c.setAll(9);
		System.out.println(c.increment(key));

		AtomicLong l = new AtomicLong(Long.MAX_VALUE);
		i = l.incrementAndGet();
		System.out.println(i);

		// 性能测试
		tools.code.RunTimer run = new tools.code.RunTimer();
		run.run("x", 10000000, new Runnable() {
			@Override
			public void run() {
				c.increment("key");// 511
			}
		});
	}
}
