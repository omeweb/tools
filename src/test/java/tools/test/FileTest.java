package tools.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Assert;
import org.junit.Test;

public class FileTest {
	@Test
	public void specialTest() {
		File f = new File("d:/");
		Assert.assertEquals(false, f.isFile());
		Assert.assertEquals(true, new File("d:/h.txt").isFile());

		// 如果File不存在，则isFile也会返回false 2014-06-11 by liusan.dyf

		Object obj = null;
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < 3; i++) {
			obj = new Object();
			list.add(obj);
		}

		for (Object item : list)
			System.out.println(item);
	}

	public static void main___x(String[] args) throws Throwable {
		File f = new File("D:/scriptT");
		FileInputStream inputStream = new FileInputStream(f);
		LineIterator lineIterator = IOUtils.lineIterator(inputStream, "gbk");

		Map<String, Integer> map = tools.MapUtil.create();

		// 按行读取
		String line = null;
		int all = 0;

		while (lineIterator.hasNext()) {
			line = lineIterator.next();
			all++;

			String[] arr = tools.StringUtil.split(line, "wwTest:");
			// System.out.println(arr[arr.length - 1]);
			String name = arr[arr.length - 1];

			if (map.containsKey(name)) {
				int n = map.get(name);
				map.put(name, n + 1);
			} else
				map.put(name, 1);
		}

		inputStream.close();

		//
		Map<String, Integer> mapCached = tools.MapUtil.create();
		int x = 0;

		Set<Entry<String, Integer>> set = map.entrySet();
		for (Entry<String, Integer> item : set) {
			int c = item.getValue();
			if (c > 1) {
				mapCached.put(item.getKey(), c);
				x += c;
			}
		}

		//
		System.out.println(all);
		System.out.println(map.size());
		System.out.println(x);
		System.out.println(mapCached.size());
	}
}
