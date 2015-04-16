package tools.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import tools.Convert;

public class ConvertTest {
	@Test
	public void toListTest() {
		String[] arr = tools.StringUtil.split("a,b,c,d", ',');
		System.out.println(tools.Convert.toList(arr));
	}

	@Test
	public void toStringExTest() {
		Assert.assertEquals("0", tools.Convert.toStringEx(false));
		Assert.assertEquals("1", tools.Convert.toStringEx(true));
		Assert.assertEquals("1", tools.Convert.toStringEx(1));
	}

	@Test
	public void uniqueTest() {
		Random r = new Random(System.currentTimeMillis());
		final List<Integer> list = new ArrayList<Integer>();

		// 构造
		for (int i = 0; i < 100; i++) {
			list.add(r.nextInt(10));
		}

		//
		System.out.println(Convert.unique(list));

		// 性能测试
		new tools.code.RunTimer().run("unique", 100000, new Runnable() {
			@Override
			public void run() {
				Convert.unique(list);// 775

				// new java.util.ArrayList<Integer>(new java.util.HashSet<Integer>(list));// 900
			}
		});
	}

	@Test
	public void hexTest() {
		String act = tools.Convert.toHexString(100, 16);// 100的16进制是64
		String exp = "64";
		Assert.assertEquals(exp, act);
	}

	@Test
	public void hexTestBinary() {
		String act = tools.Convert.toHexString(7, 2);
		String exp = "111";
		Assert.assertEquals(exp, act);
	}

	@Test
	public void joinTest() {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1);
		arr.add(2);
		arr.add(3);

		String act = Convert.join(arr, ",");
		String exp = "1,2,3";
		Assert.assertEquals(exp, act);

		// 2012-06-13
		String[] x = new String[] { "a", "b", "c" };
		Assert.assertEquals("1,2,3", tools.Convert.join(arr, ","));
		Assert.assertEquals("a=0,b=0,c", tools.Convert.join(x, "=0,"));
	}

	@Test
	public void toUnixTimeTest() {
		long act = Convert.toUnixTime();
		Assert.assertEquals(act, Convert.toUnixTime(new Date()));
	}

	@Test
	public void alignTest() {
		long act = Convert.leftAligned(Convert.toUnixTime(), 60 * 60 * 24);
		System.out.println(act - 28800 + "," + Convert.toUnixTime());

		new tools.code.RunTimer().run("alignTest", 100000, new Runnable() {

			@Override
			public void run() {
				// long v = Convert.leftAligned(Convert.toUnixTime(), 60 * 60 * 24) - 28800 - 1;// 6

			}
		});
	}

	@Test
	public void toDateTimeTest() {
		System.out.println("........"
				+ Convert.getThatDayTimestamp(Convert.toUnixTime(Convert.toDateTime("2011-09-27 15:00:01"))));

		Date d = Convert.toDateTime("2011-09-24 15:48:01.010");
		System.out.println(Convert.toString(d));

		//
		System.out.println("date:" + (Convert.toUnixTime()));

		// 时间对齐
		System.out.println(Convert.toUnixTime(Convert.toDateTime("2011-09-27")));

		new tools.code.RunTimer().run("toDateTimeTest", 100000, new Runnable() {

			@Override
			public void run() {
				Convert.toDateTime("2011-09-24 15:48:01");
			}
		});
	}

	@Test
	public void toBooleanTest() {
		boolean act = Convert.toBoolean("TrUe");
		boolean exp = true;
		Assert.assertEquals(exp, act);
	}

	// @Test
	public void ipToLongTest() {
		long act = Convert.ipToLong("127.0.0.1");
		long exp = 0L;
		Assert.assertEquals(exp, act);
	}

	@Test
	public void toIntTest() {
		int act = Convert.toInt("45", 0);
		int exp = 45;
		Assert.assertEquals(exp, act);

		act = Convert.toInt("45a", 0);
		exp = 0;
		Assert.assertEquals(exp, act);

		act = Convert.toInt('a', 0);
		exp = (int) 'a';
		Assert.assertEquals(exp, act);

		act = Convert.toInt(100.8d, 0);
		exp = 100;
		Assert.assertEquals(exp, act);

		act = Convert.toInt(100.8f, 0);
		exp = 100;
		Assert.assertEquals(exp, act);

		act = Convert.toInt((short) 1, 0);// 2014-12-15
		exp = 1;
		Assert.assertEquals(exp, act);
	}

	@Test
	public void toFloatTest() {
		float act = Convert.toFloat("45.88888888888888", 0f);
		float exp = 45.88888888888888f;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toFloat("45a", 0);
		exp = 0;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toFloat("45", 0);
		exp = 45;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toFloat(45, 0);
		exp = 45;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toFloat(45d, 0);
		exp = 45;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toFloat(".5", 0);
		exp = .5f;
		Assert.assertEquals(exp, act, 0);
	}

	@Test
	public void toDoubleTest() {
		double act = Convert.toDouble("45.88888888888888", 0f);
		double exp = 45.88888888888888d;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toDouble("45a", 0);
		exp = 0;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toDouble("45d", 0);
		exp = 45;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toDouble(45, 0);
		exp = 45;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toDouble(45d, 0);
		exp = 45;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toDouble('a', 0);
		exp = (int) 'a';
		Assert.assertEquals(exp, act, 0);

		act = Convert.toDouble(100l, 0);
		exp = 100;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toDouble(100d, 0);
		exp = 100;
		Assert.assertEquals(exp, act, 0);

		act = Convert.toDouble(".5", 0);
		exp = .5d;
		Assert.assertEquals(exp, act, 0);

		// 2014-01-07 by liusan.dyf
		Assert.assertEquals(-62.3, Convert.toDouble("-62.3", 0), 0);

		// 2013-05-22 by liusan.dyf
		Assert.assertEquals(111d, Convert.toDouble("111L", 0), 0);
	}

	@Test
	public void toLongLargeTest() {
		// 2013-11-18 by liusan.dyf
		long expLong = 900000000000816642L;

		Assert.assertEquals(expLong, Convert.toLong(expLong, 0));
		// Assert.assertEquals(expLong, Convert.toLong("900000000000816642L", 0));
	}

	@Test
	public void toLongTest() {
		long act = Convert.toLong("45.88888888888888", 0l);
		long exp = 45l;
		Assert.assertEquals(exp, act);

		act = Convert.toLong("45a", 0);
		exp = 0;
		Assert.assertEquals(exp, act);

		act = Convert.toLong("45", 0);
		exp = 45;
		Assert.assertEquals(exp, act);

		act = Convert.toLong(45, 0);
		exp = 45;
		Assert.assertEquals(exp, act);

		act = Convert.toLong(45d, 0);
		exp = 45;
		Assert.assertEquals(exp, act);

		act = Convert.toLong('a', 0);
		exp = (int) 'a';
		Assert.assertEquals(exp, act);

		Assert.assertEquals(45d, Convert.toLong(45d, 0), 0);

		new tools.code.RunTimer().run("toLong", 100000, new Runnable() {
			@Override
			public void run() {
				// Convert.toDouble("abbbbbbbbbbbasdddddddddddd.asdf.0", 0);// 14
				Convert.toLong("abbbbbbbbbbbasdddddddddddd.asdf.0", 0);// 21
				// Convert.toDouble("11L", 0);// 27
			}
		});
	}
}
