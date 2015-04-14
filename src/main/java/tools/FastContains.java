package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 字符串精确匹配，要求字符串列表固定。本类使用一维int数组来存储各string的hash值，每个string的hashcode都放入数组的固定的index上。 <br />
 * 不解决hash碰撞问题。目前没有办法保证hash值均匀的落在某数组内，该类暂不能使用。
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-11-26
 */
@Deprecated
public class FastContains {
	private int[] table;

	private int size = 0;
	private int n = 0;

	public FastContains(List<String> list) {
		size = list.size();
		table = new int[size * 2];
		for (String item : list) {
			int hc = hashCode(item);
			int idx = getIndex(hc, true);

			// System.out.println("hash(" + item + ")=" + hc + ",idx=" + idx);

			if (table[idx] != 0)
				n++;

			table[idx] = hc;
		}
	}

	private int hashCode(String v) {
		return v.hashCode();
	}

	private int getIndex(int hc, boolean update) {
		int m = hc % size;
		return m + size;// hashcode有可能负数，这里用size*2来避免index冲突

		// return new Random(hc).nextInt(size);

		// return (int) (Math.abs(hc) * 1.0 / Integer.MAX_VALUE * size);

		// return hc & size;
	}

	public boolean contains(String v) {
		int hc = hashCode(v);
		int idx = getIndex(hc, false);

		return table[idx] == hc;
	}

	public int getN() {
		return n;
	}

	public static void main(String[] args) {

		// 测试
		ArrayList<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");

		FastContains of = new FastContains(list);
		System.out.println(of.contains("b"));

		//
		Random r = new Random(1);
		for (int i = 0; i < 0; i++)
			System.out.println(r.nextInt(20));

		//
		int i = (int) (1100 * 1.0 / 2000 * 20);
		System.out.println(i);
	}
}
