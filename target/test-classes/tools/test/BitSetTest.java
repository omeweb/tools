package tools.test;

import java.util.BitSet;

public class BitSetTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BitSet bm1 = new BitSet(7);
		System.out.println(bm1.isEmpty() + "--" + bm1.size()); // 64

		BitSet bm2 = new BitSet(63);
		System.out.println(bm2.isEmpty() + "--" + bm2.size());// 64

		BitSet bm3 = new BitSet(65);
		System.out.println(bm3.isEmpty() + "--" + bm3.size());// 128

		BitSet bm4 = new BitSet(111);
		System.out.println(bm4.isEmpty() + "--" + bm4.size());// 128

		System.out.println(bm4.get(1024)); // false
		bm4.set(1024, true); // ���Զ����ݣ����ǲ����̰߳�ȫ��
		System.out.println(bm4.isEmpty() + "--" + bm4.size());// �Ѿ����ݣ�1088
	}
}
