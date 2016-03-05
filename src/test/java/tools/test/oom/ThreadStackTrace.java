package tools.test.oom;

import java.util.Map;
import java.util.Map.Entry;

public class ThreadStackTrace {
	public static void main(String[] args) {
		// 新起一个线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "Test-thread").start();

		//
		Map<Thread, StackTraceElement[]> stacktraces = Thread.getAllStackTraces();
		for (Entry<Thread, StackTraceElement[]> stacktrace : stacktraces.entrySet()) {
			Thread thread = stacktrace.getKey();

			// Filter current thread
			if (Thread.currentThread().equals(thread)) {
				continue;
			}

			System.out.println("线程：" + thread.getName());
			StackTraceElement[] elems = stacktrace.getValue();

			for (StackTraceElement elem : elems) {
				System.out.println("\t" + elem);
			}

			System.out.println("------------------------------");
		}
	}
}
