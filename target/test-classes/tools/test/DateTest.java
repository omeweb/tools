package tools.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateTest {
	final static String DATE_FORMAT = "yyyy-MM-dd";

	@Test
	public void specialTest() {
		// getSpecialDate
		// System.out.println(tools.DateTime.getSpecialDate(null, -1, -1, 0, 0));// 整点
		// System.out.println(tools.DateTime.getSpecialDate(null, -1, 0, 0, 0));// 0点
		
		System.getProperties();
	}

	@Test
	public void dayOfWeekTest() {
		Assert.assertEquals("2014-01-13",
				tools.DateTime.format(tools.DateTime.getMonday("2014-01-13", DATE_FORMAT), DATE_FORMAT));
		Assert.assertEquals("2014-01-06",
				tools.DateTime.format(tools.DateTime.getMonday("2014-01-07", DATE_FORMAT), DATE_FORMAT));
	}

	@Test
	public void parseTest() {
		final String s = "2013-01-09";

		Date d = tools.DateTime.parse(s, DATE_FORMAT);
		System.out.println(d);

		new tools.code.RunTimer().run("alignTest", 100000, new Runnable() {
			@Override
			public void run() {
				// tools.Convert.toDateTime(s, "yyyy-MM-dd");// 255
				// tools.DateTime.parse(s, "yyyy-MM-dd");// 205
				tools.MySqlFunction.toDate(s);// 842 - 278
			}
		});
	}

	@Test
	public void formatTest() {
		String s = "2013-01-09";
		Date d = tools.DateTime.parse(s, DATE_FORMAT);
		Assert.assertEquals(s, tools.DateTime.format(d, DATE_FORMAT));
		Assert.assertEquals(s, tools.DateTime.format(d, 0, DATE_FORMAT));
		Assert.assertEquals("2013-01-08", tools.DateTime.format(d, -1, DATE_FORMAT));
		Assert.assertEquals("2013-01-10", tools.DateTime.format(d, 1, DATE_FORMAT));
	}
}
