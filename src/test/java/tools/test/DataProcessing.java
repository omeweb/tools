package tools.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import tools.counter.AtomicKeyedCounter;

class KeyedIntegerList {
	private String key;
	private List<Integer> list;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return key + ":" + list;
	}
}

public class DataProcessing {
	private static Map<String, Integer> globalMap = tools.MapUtil.create();

	private static AtomicKeyedCounter counter = new AtomicKeyedCounter();

	static KeyedIntegerList parse(String text) {
		// "146298263843460","50012935:50012139:50012106:50011457:50023210"
		text = tools.StringUtil.replaceAll(text, "\"", "");// 替换到引号

		String key = null;
		List<Integer> list = new ArrayList<Integer>();
		int i = 0;
		StringTokenizer st = new StringTokenizer(text, ",:", false);
		while (st.hasMoreTokens()) {
			// System.out.println("Remaining Tokens : " + st.countTokens());
			// System.out.print(st.nextToken());

			if (i == 0)
				key = st.nextToken();
			else {
				list.add(Integer.parseInt(st.nextToken()));
			}

			i++;
		}

		KeyedIntegerList entry = new KeyedIntegerList();
		entry.setKey(key);

		// 对list进行排序
		Collections.sort(list);

		entry.setList(list);
		return entry;
	}

	static void sortList(List<String> list) {
		// 对list进行排序
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				boolean f = Integer.parseInt(o1) > Integer.parseInt(o2);
				return f ? 1 : 0;
			}
		});
	}

	static List<int[]> readingData_big_list() throws Exception {
		List<int[]> bigList = new ArrayList<int[]>();

		File file = new File("D:/Documents/My WangWang/lg_cats.csv");
		LineIterator it = FileUtils.lineIterator(file, "gbk");
		try {
			int i = 0;

			while (it.hasNext()) {
				if (i < 1) {
					i++;
					it.nextLine();
					continue;// 第一行为列名
				}

				// 读取一行
				String line = it.nextLine();
				// System.out.println(parse(line));

				// 转换数据
				KeyedIntegerList entry = parse(line);
				List<Integer> list = entry.getList();

				// 判断hash是否重复了
				boolean contains = false;
				String key = tools.Convert.join(list);
				if (!globalMap.containsKey(key))
					globalMap.put(key, 1);
				else {
					contains = true;

					int v = globalMap.get(key);
					globalMap.put(key, v + 1);
				}

				if (list.size() > 1 && !contains) {
					//
					// if ((key % 1000) == 0)
					// System.out.println("已经处理了" + i + "，产生数据" + key);

					bigList.add(toIntArray(list));
				}

				i++;
			}
		} finally {
			LineIterator.closeQuietly(it);
		}

		return bigList;
	}

	static void testSort() {
		List<String> list = new ArrayList<String>();
		list.add("100");
		list.add("99");
		list.add("101");
		sortList(list);

		String s = list.toString();
		s = tools.StringUtil.replaceAllArray(s, new String[] { "[", "]", " " }, new String[] { "", "", "" });
		System.out.println(list.toString());
		System.out.println(s);

		List<Integer> intList = new ArrayList<Integer>();
		intList.add(3);
		intList.add(1);
		intList.add(2);

		Collections.sort(intList);
		System.out.println(intList);
	}

	static void multiThreadInter(final List<int[]> bigList, int start, int end, int threadCount) {
		final CountDownLatch threadSignal = new CountDownLatch(threadCount);// 初始化countDown
		final AtomicInteger index = new AtomicInteger(start - 1);
		final int endIndex = end;

		for (int i = 0; i < threadCount; i++) {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					int x;
					while ((x = index.addAndGet(1)) < endIndex) {
						inter(bigList, x);
					}

					threadSignal.countDown();// 线程结束时计数器减1
				}
			});

			th.setDaemon(true);
			th.start();
		}

		try {
			threadSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static void export() throws Exception {

		Map<String, String> map = tools.MapUtil.create();

		// 去掉数值比较小的
		Map<String, String> mapCopy = new HashMap<String, String>(map);
		for (String key : mapCopy.keySet()) {
			int v = Integer.parseInt(mapCopy.get(key));
			if (v < 100)
				map.remove(key);
		}

		FileUtils.write(new File("d:/json_.txt"), tools.Json.toPrettyJson(map), "gbk");

		// System.out.println(tools.Json.toPrettyJson(map));
	}

	static int[] toIntArray(List<Integer> set) {
		int[] rtn = new int[set.size()];

		int i = 0;
		for (Integer item : set) {
			rtn[i] = item;
			i++;
		}

		return rtn;
	}

	static void inter(List<int[]> bigList, int start) {
		// 求交集
		int count = bigList.size();

		int[] source = bigList.get(start);

		if ((start % 1000) == 0) {
			System.out.println("开始比对:" + start + "," + System.currentTimeMillis());
		}

		for (int j = start + 1; j < count; j++) {
			// // -------------方案一
			// List<Integer> list = intersection(source, bigList.get(j), true);
			//
			// if (list.size() >= 2) {
			// String s = list.toString();
			// s = tools.StringUtil.replaceAllArray(s, new String[] { "[",
			// "]", " " }, new String[] { "", "", "" });
			//
			// clientX.hincrBy("result_5", s, 1);
			// }

			// -------------方案二
			int[] de = bigList.get(j);
			int[] resultList = intersectSortedArrays(source, de);
			if (resultList.length >= 2) {
				String resultString = arrayToString(resultList);

				// 重要，全局map里记录了重复出现的次数
				String key = arrayToString(de);

				int delta = 1;
				if (globalMap.containsKey(key)) {
					delta = globalMap.get(key);
				}

				addCounter(resultString, delta);
			}
		}
	}

	static void addCounter(String key, int v) {
		counter.increment(key, v);
	}

	static String arrayToString(int[] arr) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int item : arr) {
			if (first) {
				first = false;
			} else
				sb.append(",");
			sb.append(item);
		}
		return sb.toString();
	}

	// static String replace(String v) {
	// return tools.StringUtil.replaceAllArray(v,
	// new String[] { "[", "]", " " }, new String[] { "", "", "" });
	// }

	/**
	 * 稍快
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int[] intersectSortedArrays(int[] a, int[] b) {
		int[] c = new int[Math.min(a.length, b.length)];
		int ai = 0, bi = 0, ci = 0;
		while (ai < a.length && bi < b.length) {
			if (a[ai] < b[bi]) {
				ai++;
			} else if (a[ai] > b[bi]) {
				bi++;
			} else {
				if (ci == 0 || a[ai] != c[ci - 1]) {
					c[ci++] = a[ai];
				}
				ai++;
				bi++;
			}
		}
		return Arrays.copyOfRange(c, 0, ci);
	}

	/**
	 * 假设a、b里的元素都不重复
	 * 
	 * @param a
	 * @param b
	 * @param allSorted
	 * @return
	 */
	public static List<Integer> intersection(int[] a, int[] b, boolean allSorted) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (!allSorted) {
			Arrays.sort(a);
			Arrays.sort(b);
		}
		int i = 0;
		int j = 0;
		while (i < a.length && j < b.length) {
			if (a[i] < b[j])
				++i;
			else if (a[i] > b[j])
				++j;
			else {
				// if (!result.contains(a[i]))
				result.add(a[i]);
				++i;
				++j;
			}
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		testSort();
	}

	public static void main_(String[] args) throws Exception {
		long startTs = System.currentTimeMillis();

		// testSort();

		// System.out.println(arrayToString(new int[] { 10, 11, 12 }));

		List<int[]> bigList = readingData_big_list();// 6s
		// System.out.println(bigList.size());

		System.out.println(System.currentTimeMillis() - startTs);
		startTs = System.currentTimeMillis();

		// for (int i = 4000; i < 5000; i++)
		// inter(bigList, i);// result3要从4000开始

		multiThreadInter(bigList, 0, bigList.size(), 1);// 70000

		// int[] xxx = bigList.get(0);
		// for (int item : xxx)
		// System.out.println(item);

		// export();

		System.out.println(System.currentTimeMillis() - startTs);

		// 计数器持久化
		Map<String, Long> counters = counter.getAll();

		FileUtils.write(new File("d:/counter.txt"), tools.Json.toPrettyJson(counters), "gbk");

		// multiThreadInter(5, 2);
	}
}
