package tools.test.session;

import org.junit.Test;

import tools.test.domain.User;

public class SessionTest {

	@Test
	public void test() throws InterruptedException {
		final MySessionService s = new MySessionService();

		// 开n个线程
		Thread[] arr = new Thread[15];
		for (int i = 0; i < arr.length; i++) {
			Thread th = new Thread() {
				@Override
				public void run() {
					long id = Thread.currentThread().getId();
					User entry = new User();
					entry.setUserName(id + "");
					s.set(entry);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {

					}

					System.out.println(id + ":" + s.get().getUserName());
				}
			};
			th.setDaemon(true);
			th.start();

			arr[i] = th;
		}

		// 等待线程结束
		for (int i = 0; i < arr.length; i++) {
			arr[i].join();
		}
	}
}
