package tools.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import tools.FastContains;
import tools.MapUtil;
import tools.code.RunTimer;

@SuppressWarnings("deprecation")
public class IndexOfTest {
	@Test
	public void hashTest() {
		// string----------------------------
		final String v = "1234,5687,12356,禁限售";

		// 测试
		new RunTimer().run("indexOfTest", 1000000, new Runnable() {
			@Override
			public void run() {
				// v.indexOf("5687");// 33
				v.hashCode();// 13
			}
		});

		// set----------------------------
		final Set<String> set = new HashSet<String>();
		set.add("1234");
		set.add("5687");
		set.add("12356");
		set.add("禁限售");

		Assert.assertEquals(true, set.contains("5687"));

		// 测试
		new RunTimer().run("hashTest", 1000000, new Runnable() {
			@SuppressWarnings("unused")
			int i = 0;

			@Override
			public void run() {
				// set.contains("5687");// 26
				// set.size();//14
				i++;// 10
			}
		});

		// list----------------------------
		final List<String> list = new ArrayList<String>();
		list.add("1234");
		list.add("5687");
		list.add("12356");
		list.add("禁限售");

		Assert.assertEquals(true, list.contains("5687"));

		// 测试
		new RunTimer().run("listTest", 1000000, new Runnable() {
			@Override
			public void run() {
				list.contains("5687");// 29
			}
		});
	}

	@Test
	public void test() {
		// ArrayList
		final ArrayList<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");

		for (int i = 0; i < 1000000; i++)
			list.add(i + "");

		// map
		final Map<String, Integer> map = MapUtil.create();
		for (String item : list)
			map.put(item, 1);

		//
		final String key = "a";

		System.out.println("map:" + map.containsKey(key));

		//
		final FastContains of = new FastContains(list);
		System.out.println("indexof:" + of.contains(key));
		System.out.println("n:" + of.getN());

		// 测试
		new RunTimer().run("indexOfTest", 1000000, new Runnable() {
			@Override
			public void run() {
				// of.contains(key);// 11
				// list.contains(key);// 36
				map.containsKey(key);// 22
			}
		});
	}
}
