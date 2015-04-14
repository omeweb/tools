package tools.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import tools.Action;

/**
 * 使用ExecutorService.newFixedThreadPool多线程执行任务
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-12-20
 */
public class Parallel {
	/**
	 * 调用带有ExecutorService版本的loop 2014-12-02 by 六三
	 * 
	 * @param count
	 * @param action
	 * @param threadCount
	 * @return
	 */
	public static int loop(int count, final Action<Integer> action, int threadCount) {
		int r = 0;

		// 创建线程池
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);

		try {
			r = loop(executor, count, action);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();// 记得关闭
		}

		return r;
	}

	/**
	 * 从0循环到count-1。增加线程池参数，且不关闭线程池 2014-11-05 by liusan.dyf
	 * 
	 * @param executor 线程池
	 * @param count 要循环多少次
	 * @param action 循环干什么事情，传入循环的index，0到count-1
	 * @return 成功执行的次数
	 */
	public static int loop(ExecutorService executor, int count, final Action<Integer> action) {
		// 计数，大小为count，执行完一个就减少一个
		final CountDownLatch countdown = new CountDownLatch(count);
		final AtomicInteger success = new AtomicInteger();//

		for (int i = 0; i < count; i++) {
			final int item = i;
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						action.execute(item);// 运行job
						success.getAndIncrement();// 成功执行数加1
					} finally {
						countdown.countDown();// 计数减一
					}
				}
			});
		}

		// 等待结束
		try {
			countdown.await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// executor.shutdown();//不再关闭 2014-12-02 by 六三
		}

		// 返回
		return success.get();
	}

	/**
	 * 直接循环执行Runnable
	 * 
	 * @param jobs
	 * @param i 并发线程数
	 * @return
	 */
	public static int each(List<Runnable> jobs, int i) {
		return each(jobs, null, i);
	}

	/**
	 * 用action多线程去循环执行list里的元素
	 * 
	 * @param list
	 * @param action
	 * @param threadCount
	 * @return
	 */
	public static <T> int each(final List<T> list, final Action<T> action, int threadCount) {
		int r = 0;

		if (threadCount <= 0)
			threadCount = 1;

		// 创建线程池
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);

		try {
			r = each(executor, list, action);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();// 记得关闭
		}

		return r;
	}

	/**
	 * 多线程的处理list 2014-11-05 by liusan.dyf
	 * 
	 * @param executor
	 * @param list 要处理的集合
	 * @param action 做如何处理，如果T=Runnable，则action可以为null
	 * @return
	 */
	public static <T> int each(ExecutorService executor, final List<T> list, final Action<T> action) {
		if (list == null || list.size() == 0)
			return 0;

		// if (action == null)
		// return;

		// 修正线程数 2012-12-24
		int size = list.size();

		int x = loop(executor, size, new Action<Integer>() {
			@Override
			public void execute(Integer t) {
				T item = list.get(t);

				if (item instanceof Runnable) { // 支持Runnable
					Runnable r = (Runnable) item;
					r.run();// TODO 可能有异常 2014-12-19 19:54:16 by 六三
				} else if (action != null)
					action.execute(item);// 运行job

			}
		});

		// 返回
		return x;
	}

	public static void main(String[] args) {
		List<Runnable> jobs = new ArrayList<Runnable>();
		//
		class N {
			int sum = 0;
		}

		final N n = new N();

		for (int i = 0; i <= 100; i++) {
			final int x = i;
			jobs.add(new Runnable() {
				@Override
				public void run() {
					n.sum += x;

					// sleep(50);// 50 * 100 = 5秒

				}
			});
		}

		long startTs = System.currentTimeMillis();
		each(jobs, 10);

		System.out.println(System.currentTimeMillis() - startTs);// 13
		System.out.println(n.sum); // 5050

		/**
		 * 遍历集合
		 */
		final char[] arr = "abc".toCharArray();

		Action<Object> action = new Action<Object>() {
			@Override
			public void execute(Object t) {
				System.out.println(t + " @ " + Thread.currentThread().getId());
				// if (t.equals('a'))
				// throw new RuntimeException(); // 模拟异常

				// sleep(2);
			}
		};

		List<Object> list = new ArrayList<Object>();
		for (char item : arr)
			list.add(item);

		int x = each(list, action, 2);
		System.out.println(x);

		// 独立的ExecutorService测试 2014-12-02 by 六三
		System.out.println("-----------------------------");
		Action<Integer> actionInteger = new Action<Integer>() {
			@Override
			public void execute(Integer t) {
				System.out.println(t + " @ " + Thread.currentThread().getId());
				tools.Global.sleep(100);
			}
		};

		ExecutorService executor = Executors.newFixedThreadPool(5);
		loop(executor, 30, actionInteger);
		loop(executor, 30, actionInteger);
		executor.shutdown();
	}
}
