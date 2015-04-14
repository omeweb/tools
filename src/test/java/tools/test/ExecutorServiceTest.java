package tools.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * // from http://my.oschina.net/bairrfhoinn/blog/177639
 * 
 * @author <a href="mailto:omeweb@taobao.com">omeweb</a>
 * @version 1.0
 * @since 2014年12月26日
 */
public class ExecutorServiceTest {
	public static void main(String[] args) throws Throwable {
		Future<?> f = async();
		System.out.println("begin");
		f.get();// 这里只是确保执行完毕
		System.out.println("done");
	}

	public static Future<?> async() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Future<?> f = executorService.submit(new Runnable() {
			public void run() {
				tools.Global.sleep(10000);
			}
		});

		executorService.shutdown();

		return f;

	}
}
