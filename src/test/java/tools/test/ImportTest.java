package tools.test;

import tools.test.imports.*;// 导入的是这个包下的全部的子类

/**
 * 如果没有用到某个类，这里是不会加载的
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2015年8月3日
 */
public class ImportTest {
	public static void main(String[] args) {
		A1 a = new A1();// A1类会被加载
		System.out.println(a);

		// 这个分支没有走到，那A2类也不会被加载的 2015-8-3 12:07:51 by liusan.dyf
		if (System.currentTimeMillis() < 0)
			System.out.println(new A2());
	}
}
