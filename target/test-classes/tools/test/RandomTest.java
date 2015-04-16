package tools.test;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import tools.code.RunTimer;

public class RandomTest {

	volatile int n = 0;

	@Test
	public void r() {
		System.out.println("jvm 实现版本：\t" + System.getProperty("java.vm.version"));
		System.out.println("jvm 规范版本：\t" + System.getProperty("java.vm.specification.version"));

		@SuppressWarnings("unused")
		final AtomicLong atomic = new AtomicLong();

		new RunTimer().run("random", 100000 * 100, new Runnable() {
			@Override
			public void run() {
				// atomic.getAndIncrement();// 3
				// atomic.get();// 2
				// boolean x = (10 < 10);// 2
				// Thread.currentThread().getId();// 2
				// tools.StringUtil.RANDOM.nextInt();// 15
				// System.currentTimeMillis();// 2

				/**
				 * 3
				 */
				// long v = atomic.get();
				// long tid = Thread.currentThread().getId();
				// if(v == 0){
				// atomic.compareAndSet(0, tid);
				// }else{
				// boolean y = tid == v;
				// }

				// int x = 1322265431 % 4;// 2

				// int x= 1322265431 & 3;//2

				/**
				 * 
				 */
				// long t = atomic.incrementAndGet() & 7; // 4,100 214
				// boolean f = t < 1;

				/**
				 * 
				 */
				// int t = (n++) & 7; // 4 ,100 170 volatile
				// boolean f = t < 1;
			}
		});
	}
}
