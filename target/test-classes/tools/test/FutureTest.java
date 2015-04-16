package tools.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 试验 Java 的 Future 用法
 */
public class FutureTest {

	public static class Task implements Callable<String> {
		@Override
		public String call() throws Exception {
			String tid = String.valueOf(Thread.currentThread().getId());
			System.out.printf("Thread#%s : in call\n", tid);
			return tid;
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		List<Future<String>> results = new ArrayList<Future<String>>();
		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 0; i < 100; i++)
			results.add(es.submit(new Task()));

		for (Future<String> res : results)
			System.out.println(res.get());

		es.shutdown();
	}

}
