package tools.test.oom;

import java.util.*;

public class StringIntern {
	public static long times = 10000000L;

	public static void main(String[] args) {
		testIntern();
	}

	public static void testIntern() {
		System.gc();

		List<String> list = new ArrayList<String>();

		long l = System.currentTimeMillis();

		for (int i = 0; i < times; i++) {
			list.add(("A" + (i % 1000)).intern());
		}

		long ll = System.currentTimeMillis();
		System.out.println("testIntern time :" + (ll - l));

		System.gc();

		System.out.println("testIntern:" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}
}
