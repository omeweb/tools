package tools.test;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import tools.CategoryService;
import tools.MapUtil;

/**
 * @author 六三
 * @version 1.0
 * @since 2014年12月12日
 */
public class CategoryTest {
	private CategoryService entry = getService();

	private CategoryService getService() {
		// 构造树
		Map<Integer, Integer> map = MapUtil.create();
		map.put(1, 0);
		map.put(2, 1);
		map.put(3, 1);
		map.put(4, 3);
		map.put(5, 3);
		map.put(7, 0);
		map.put(8, 7);
		map.put(9, 8);

		// 错误的干扰数据 2016-7-22 09:45:13 by liusan.dyf
		// map.put(-1, -1);
		// map.put(0, -1);
		map.put(6, 6);

		map.put(1, 5);// 组成环状数据

		// 匹配测试
		CategoryService entry = new CategoryService();
		entry.init(map);

		return entry;
	}

	@Test
	public void isChildOfTest() {
		Assert.assertEquals(true, entry.isChildOf(5, 1));
		Assert.assertEquals(false, entry.isChildOfAny(5, "4,2"));
		Assert.assertEquals(true, entry.isChildOfAny(5, "3"));
		Assert.assertEquals(false, entry.isChildOf(5, 20));
		
		Assert.assertEquals(false, entry.isChildOf(6, 1));// false，节点指向了自己
		Assert.assertEquals(false, entry.isChildOfAny(5, "4,2"));// false，环状数据，已达最大查找次数
		Assert.assertEquals(true, entry.isChildOfAny(9, "7,2"));// true
		Assert.assertEquals(false, entry.isChildOfAny(0, "7,2"));// false
	}
}
