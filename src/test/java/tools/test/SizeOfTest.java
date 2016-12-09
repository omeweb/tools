package tools.test;

import java.util.concurrent.atomic.AtomicIntegerArray;

import com.carrotsearch.sizeof.RamUsageEstimator;

/**
 * https://github.com/dweiss/java-sizeof
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2014年11月12日
 */
public class SizeOfTest {
	public static void main(String[] args) {
		int[] arr = new int[10000 * 100];

		long v = RamUsageEstimator.sizeOf(arr);
		System.out.println(v);// 4000016
		System.out.println(RamUsageEstimator.humanSizeOf(arr));// 3.8 MB

		// 2015-8-14 10:59:54 by liusan.dyf
		String[] stringArray = new String[] { "abcdefg", "cd" };
		System.out.println(RamUsageEstimator.sizeOf(stringArray));// 144

		// 2016-7-20 16:02:39 by liusan.dyf
		AtomicIntegerArray aia = new AtomicIntegerArray(1024 * 1024);
		System.out.println(RamUsageEstimator.humanSizeOf(aia));// 4 MB

		System.out.println(RamUsageEstimator.humanSizeOf(new int[1024 * 20]));// 80K

	}
}

// from http://mindprod.com/jgloss/sizeof.html

// overhead ~8 bytes/object
// boolean 1
// byte 1
// char 2
// short 2
// int 4
// long 8
// float 4
// double 8
// reference 4/8
// String length * 2 + 4
