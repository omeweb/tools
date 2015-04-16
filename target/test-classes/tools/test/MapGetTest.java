package tools.test;

import java.util.Map;

public class MapGetTest {
	public static void main(String[] args) throws Throwable {
		final Map<String, Object> m1 = tools.MapUtil.create();
		final Map<String, Object> m2 = tools.MapUtil.concurrentHashMap();

		final String key = "a";

		m1.put(key, 1);
		m2.put(key, 1);

		int times = 10000000;
		new tools.code.RunTimer().run("perf", times, new Runnable() {
			@Override
			public void run() {
				// m1.get(key);//212
				m2.get(key);// 350
			}
		});
	}
}
