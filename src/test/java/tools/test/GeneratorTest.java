package tools.test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;

import org.junit.Test;

import tools.generator.AtomicIdGenerator;
import tools.generator.FileDataStore;
import tools.generator.ranged.Range;
import tools.generator.ranged.RangeGenerator;
import tools.generator.ranged.RangedIdGenerator;

public class GeneratorTest {
	// @Test
	public void idTest() throws Throwable {
		final AtomicIdGenerator g = new AtomicIdGenerator("id", new FileDataStore("d:/data\\temp"));

		final Map<Long, Object> map = new java.util.concurrent.ConcurrentHashMap<Long, Object>();

		// 多线程测试ranged
		int count = 20;
		Thread[] array = new Thread[count];
		for (int i = 0; i < count; i++) {
			Thread th = new Thread(new Runnable() {

				@Override
				public void run() {
					for (int j = 0; j < 5; j++) {
						long v = g.next();
						if (!map.containsKey(v)) {
							map.put(v, v);
							System.out.println(Thread.currentThread().getName() + ":got " + v);
						} else
							System.out.println(Thread.currentThread().getName() + "------重复了，v=" + v);
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

		Thread.sleep(1000);
	}

	@Test
	public void rangeTest() throws Exception {
		Assert.assertEquals(1, 1);

		// 原始计数器
		AtomicIdGenerator g = new AtomicIdGenerator("id", null);

		final AtomicLong id = new AtomicLong(0);

		// 区间计数器
		final RangedIdGenerator ranged = new RangedIdGenerator(g, new RangeGenerator() {

			@Override
			public Range next(String key) {

				// System.out.println("获取新的区间");
				long v = id.get();
				long end = v + 1;

				while (id.compareAndSet(v, end))
					return new Range(v, end);

				return null;
			}

			@Override
			public Range current(String key) {
				return null;
			}

			@Override
			public boolean save(String key, Range g) {
				return false;
			}
		});

		final Map<Long, Object> map = new java.util.concurrent.ConcurrentHashMap<Long, Object>();

		// 多线程测试ranged
		int count = 20;
		Thread[] array = new Thread[count];
		for (int i = 0; i < count; i++) {
			Thread th = new Thread(new Runnable() {

				@Override
				public void run() {
					for (int j = 0; j < 5; j++) {

						// 取
						long v = ranged.next();
						if (!map.containsKey(v)) {
							map.put(v, v);
							// System.out.println(Thread.currentThread().getName() + ":got " + v);
						} else
							System.out.println(Thread.currentThread().getName() + "------重复了，v=" + v);

						// sleep
						try {
							Thread.sleep(8);
						} catch (InterruptedException e) {
							e.printStackTrace();
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

		Thread.sleep(1000);
	}
}
