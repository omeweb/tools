package tools.test.counter;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

import tools.Action;
import tools.counter.MultiKeyedCounter;

public class MultiKeyedCounterTest {
	static MultiKeyedCounter c = new tools.counter.AtomicKeyedCounter();
	static String key = "myc";

	@Test
	public void perfTest() {
		final MultiKeyedCounter c = new tools.counter.AtomicKeyedCounter();

		// 性能测试
		tools.code.RunTimer run = new tools.code.RunTimer();
		run.run("x", 10000000, new Runnable() {
			@Override
			public void run() {
				c.increment("key");// 838 - 557
			}
		});

		// 多线程测试
		final java.util.Random r = new java.util.Random();
		tools.concurrent.Parallel.loop(100000, new Action<Integer>() {
			@Override
			public void execute(Integer t) {
				c.increment("key_" + r.nextInt(200));
			}
		}, 10);

		try {
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {

		}

		System.out.println(c.getAll());
	}

	@Test
	public void hexTest() {
		int threadCount = 20;

		final CountDownLatch threadSignal = new CountDownLatch(threadCount * 2);// 初始化countDown

		for (int i = 0; i < threadCount; i++) {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					int x = 10000;
					while (x > 0) {
						c.increment(key);
						x--;
					}

					threadSignal.countDown();// 线程结束时计数器减1
				}
			});

			th.setDaemon(true);
			th.start();
		}

		for (int i = 0; i < threadCount; i++) {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					//System.out.println("running");
					
					int x = 10000;
					while (x > 0) {
						c.decrement(key);
						x--;
					}

					threadSignal.countDown();// 线程结束时计数器减1
				}
			});

			th.setDaemon(true);
			th.start();
		}

		try {
			threadSignal.await();	
			//Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(c.get(key), 0);
		System.out.println(c.get(key));
	}
}
