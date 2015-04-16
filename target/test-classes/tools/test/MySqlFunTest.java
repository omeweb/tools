package tools.test;

import org.junit.Assert;

import org.junit.Test;

import tools.MySqlFunction;

public class MySqlFunTest {
	@Test
	public void substring() {
		Assert.assertEquals("ratica", MySqlFunction.substring("Quadratically", 5, 6));

		Assert.assertEquals("aki", MySqlFunction.substring("Sakila", -5, 3));
	}

	@Test
	public void find_in_set() {
		String v = "a\nb,c";
		Assert.assertEquals(1, MySqlFunction.find_in_set("a", v));
		Assert.assertEquals(3, MySqlFunction.find_in_set("c", v));
	}

	@Test
	public void mod() {
		Assert.assertEquals(4, MySqlFunction.mod(234, 10));
		Assert.assertEquals(2, MySqlFunction.mod(29, 9));
	}

	@Test
	public void between() {
		Assert.assertEquals(true, MySqlFunction.between(15, 234, 10));
		Assert.assertEquals(true, MySqlFunction.between(30, 29, 59));
	}

	@Test
	public void floor() {
		Assert.assertEquals(1.0, MySqlFunction.floor(1.23), 0);
		Assert.assertEquals(-2.0, MySqlFunction.floor(-1.23), 0);
	}

	@Test
	public void like() {
		Assert.assertEquals(1, MySqlFunction.like("abc", "a%"));
		Assert.assertEquals(1, MySqlFunction.like("abc", "a_c"));
	}

	@Test
	public void strcmp() {
		Assert.assertEquals(-1, MySqlFunction.strcmp("text", "text2"));
		Assert.assertEquals(1, MySqlFunction.strcmp("text2", "text"));
		Assert.assertEquals(0, MySqlFunction.strcmp("text", "text"));
	}

	@Test
	public void truncate() {
		Assert.assertEquals(1.23, MySqlFunction.truncate(1.235, 2), 0);
	}

	@Test
	public void isTime() {
		Assert.assertEquals(true, MySqlFunction.isTime("10:12:13"));
		Assert.assertEquals(true, MySqlFunction.isTime("10:12:13.456"));
		Assert.assertEquals(false, MySqlFunction.isTime("10:12:13a"));
		Assert.assertEquals(false, MySqlFunction.isTime("100:12:13"));
	}

	@Test
	public void hour() {
		Assert.assertEquals(10, MySqlFunction.hour("10:12:13"));
		Assert.assertEquals(10, MySqlFunction.hour("10:12:13.456"));
		Assert.assertEquals(10, MySqlFunction.hour("2012-06-28 10:12:13.456"));
		Assert.assertEquals(0, MySqlFunction.hour("10:12:13a"));
		Assert.assertEquals(0, MySqlFunction.hour("100:12:13"));
	}

	@Test
	public void diff() {
		Assert.assertEquals(-2, MySqlFunction.second_diff("2012-12-03 10:12:13", "2012-12-03 10:12:15"));
		Assert.assertEquals(2, MySqlFunction.abs_second_diff("2012-12-03 10:12:13", "2012-12-03 10:12:15"));
		Assert.assertEquals(-1, MySqlFunction.datediff("2012-12-02 10:12:13", "2012-12-03 10:12:15"));
	}

	@Test
	public void microsecond() {
		Assert.assertEquals(0, MySqlFunction.microsecond("10:12:13"));
		Assert.assertEquals(455, MySqlFunction.microsecond("10:12:13.456"));
		// Assert.assertEquals(455,
		// MySqlFunction.microsecond("2012-06-28 10:12:13.456"));
		Assert.assertEquals(0, MySqlFunction.microsecond("10:12:13a"));
		Assert.assertEquals(0, MySqlFunction.microsecond("100:12:13"));
	}
}
