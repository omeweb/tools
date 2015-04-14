package tools.test.cache;

import org.junit.Assert;

import org.junit.Test;

public class CacheTest {
	@Test
	public void testCache() throws InterruptedException {
		String key = "name";
		Catalog value = new Catalog();
		value.setTitle("xxxx");

		// 创建对象
		tools.cache.HashMapCacheProvider c = new tools.cache.HashMapCacheProvider();
		c.set(key, value, 3);

		// 取值
		Object s1 = c.get(key);
		System.out.println(s1);

		// 等待
		Thread.sleep(1000 * 2);

		// 再取
		Object s2 = c.get(key);
		System.out.println(s2);

		// 验证
		Assert.assertEquals(s1, s2);
	}
}

class Catalog {
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
