package tools.test;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import tools.CategoryService;

/**
 * @author 六三
 * @version 1.0
 * @since 2014年12月12日
 */
public class CategoryTest {
	private CategoryService entry = getService();

	private CategoryService getService() {
		// 构造树
		Map<Integer, Integer> map = tools.MapUtil.create();
		map.put(1, 0);
		map.put(2, 1);
		map.put(3, 1);
		map.put(4, 3);
		map.put(5, 3);

		// 匹配测试
		CategoryService entry = new CategoryService();
		entry.loadFrom(map);

		return entry;
	}

	@Test
	public void isChildOfTest() {
		Assert.assertEquals(true, entry.isChildOf(5, 1));
		Assert.assertEquals(false, entry.isChildOfAny(5, "4,2"));
		Assert.assertEquals(true, entry.isChildOfAny(5, "3"));

		Assert.assertEquals(false, entry.isChildOf(5, 20));
	}
}
