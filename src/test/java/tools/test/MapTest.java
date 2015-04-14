package tools.test;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import tools.code.RunTimer;

public class MapTest {
	public static void main(String[] args) {
		final Map<String, String> map = tools.MapUtil.create();
		map.put("a", "a1");
		map.put("b", "b1");

		int count = 1000000;
		final String key = "a";
		
		// 测试
		new RunTimer().run("containsKeyTest", count, new Runnable() {
			@Override
			public void run() {
				if (map.containsKey(key))// 有没有这个if的判断，耗时都不变：24ms
					map.remove(key);
			}
		});
	}

	@Test
	public void test() {
		int count = 1000000;
		// 测试
		new RunTimer().run("entrySetTest", count, new Runnable() {
			@Override
			public void run() {
				Map<String, String> map = getMap();

				String key = null;
				for (Entry<String, String> item : map.entrySet()) {
					key = item.getKey();
					if ("a".equals(key))
						map.remove(key);
				}
			}
		});

		// 测试
		new RunTimer().run("iteratorTest", count, new Runnable() {
			@Override
			public void run() {
				Map<String, String> map = getMap();
				Iterator<String> iterator = map.keySet().iterator();

				String key = null;
				while (iterator.hasNext()) {
					key = iterator.next();
					if ("a".equals(key))
						iterator.remove();
				}
			}
		});

		// 测试
		new RunTimer().run("forTest", count, new Runnable() {
			@Override
			public void run() {
				Map<String, String> map = getMap();
				Set<String> set = map.keySet();

				for (String item : set) {
					if ("a".equals(item))
						map.remove(item);
				}
			}
		});

		//
		Map<String, String> map = getMap();
		Set<String> set = map.keySet();

		for (String item : set) {
			if ("a".equals(item))
				map.remove(item);
		}
		System.out.println(map);
	}

	private Map<String, String> getMap() {
		Map<String, String> map = tools.MapUtil.create();
		map.put("a", "a1");
		map.put("b", "b1");

		return map;
	}
}
