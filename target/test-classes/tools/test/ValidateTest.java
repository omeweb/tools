package tools.test;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.Assert;
import org.junit.Test;

import tools.Validate;

public class ValidateTest {
	@Test
	public void isZHCNTest() {
		Assert.assertEquals(true, Validate.isChinese("我"));
		Assert.assertEquals(false, Validate.isChinese("！"));
		Assert.assertEquals(false, Validate.isChinese("a"));
		Assert.assertEquals(false, Validate.isChinese("!"));
	}

	@Test
	public void stackTest() {
		Deque<Integer> stack = new ArrayDeque<Integer>();
		stack.push(1);
		stack.push(2);

		Assert.assertEquals(2, stack.size());

		System.out.println(stack.peek());// 2
		System.out.println(stack.pop());// 2
		System.out.println(stack.pop());// 1

		Assert.assertEquals(0, stack.size());
	}

	@Test
	public void isIntTest() {
		boolean act = Validate.isInt("123");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isInt("a");
		exp = false;
		Assert.assertEquals(exp, act);

		Assert.assertEquals(false, Validate.isInt("123.5"));
		Assert.assertEquals(false, Validate.isInt("123."));
		Assert.assertEquals(false, Validate.isInt(""));
		Assert.assertEquals(false, Validate.isInt("abc"));
		Assert.assertEquals(false, Validate.isInt(null));
		Assert.assertEquals(true, Validate.isInt("01230000000000"));
	}

	@Test
	public void dateTimeBetweenTest() {
		Assert.assertEquals(true, Validate.dateTimeBetween(1355206783 * 1000L, "2012-12-11，2012-12-18"));

		Assert.assertEquals(true, Validate.dateTimeBetween("2012-12-12", "2012-12-11"));
		Assert.assertEquals(true, Validate.dateTimeBetween("2012-12-12", "2012-12-13，2012-12-11"));
		Assert.assertEquals(false, Validate.dateTimeBetween("a", "2012-12-11，2012-12-13"));
		Assert.assertEquals(false, Validate.dateTimeBetween("2012-12-12", "2012-12-14，2012-12-18"));
		Assert.assertEquals(true, Validate.dateTimeBetween(1355206783, "2012-12-11 12:13:14，2012-12-18"));
	}

	@Test
	public void containsTest() {
		Assert.assertEquals(true, Validate.containsChinese("我ai中国"));
		Assert.assertEquals(false, Validate.containsChinese("ai"));
		Assert.assertEquals(false, Validate.containsDigit("ai"));
		Assert.assertEquals(true, Validate.containsDigit("ai555"));

		Assert.assertEquals(true, Validate.containsAlpha("ai555"));
		Assert.assertEquals(false, Validate.containsAlpha("555"));

		short s = 10;
		System.out.println(s & 2);
	}

	@Test
	public void isDoubleTest() {
		boolean act = Validate.isNumeric("-123.45687");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isNumeric("a");
		exp = false;
		Assert.assertEquals(exp, act);
	}

	// @Test
	// public void isIpTest() {
	// boolean act = Validate.isIp("1.2.3.4");
	// boolean exp = true;
	// Assert.assertEquals(exp, act);
	//
	// act = Validate.isIp("a");
	// exp = false;
	// Assert.assertEquals(exp, act);
	// }

	@SuppressWarnings("deprecation")
	@Test
	public void isDigitalTest() {
		boolean act = Validate.isDigital("012345687");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isDigital("");
		exp = false;
		Assert.assertEquals(exp, act);
	}

	@Test
	public void isHasZHCNTest() {
		boolean act = Validate.isChinese("杜有发");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isChinese("abbbdd杜");
		exp = false;
		Assert.assertEquals(exp, act);
	}

	@Test
	public void checkStringLengthTest() {
		boolean act = Validate.checkStringLength("a发", 2, 6);
		boolean exp = true;
		Assert.assertEquals(exp, act);
	}

	@Test
	public void isEmailTest() {
		boolean act = Validate.isEmail("na-me.free@y-eah.net");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isEmail("abbbdd");
		exp = false;
		Assert.assertEquals(exp, act);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void isJustCharsOrNumbersTest() {
		boolean act = Validate.isJustCharsOrNumbers("adf5asdf8asd5");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isEmail("ab杜bbdd");
		exp = false;
		Assert.assertEquals(exp, act);
	}

	@Test
	public void isTimeTest() {
		boolean act = Validate.isTime("12:52:52");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isTime("12:52:521");// 秒位不对
		exp = false;
		Assert.assertEquals(exp, act);

		// 验证失败
		Assert.assertEquals(true, Validate.isTime("12:52:52.456"));
	}

	@Test
	public void isNumericArrayTest() {
		boolean act = Validate.isNumericArray("12:522:52", ":");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isNumericArray("12:52:52a", ":");
		exp = false;
		Assert.assertEquals(exp, act);

		act = Validate.isNumericArray("12,52,52", ",");
		exp = true;
		Assert.assertEquals(exp, act);
	}

	@Test
	public void startsWithAnyTest() {
		Assert.assertEquals(true, Validate.startsWithAny("abc", "ab", "b"));
	}

	@Test
	public void containsAlphaTest() {
		Assert.assertEquals(false, Validate.containsAlpha("中国"));
		Assert.assertEquals(true, Validate.containsAlpha("中国a"));
	}

	@Test
	public void isChineseTest() {
		final String v = "中国人";
		Assert.assertEquals(true, Validate.isChinese(v));
		Assert.assertEquals(true, Validate.isChinese(v));
		Assert.assertEquals(true, Validate.isChineseOrAlpha("中国nd"));
		Assert.assertEquals(true, Validate.isChineseOrAlpha("tbnd"));

		new tools.code.RunTimer().run("isChineseTest", 100000, new Runnable() {

			@Override
			public void run() {
				// Validate.isChinese(v);//32
				Validate.isChinese(v);// 35
			}
		});
	}

	@Test
	public void isInStringTest() {
		boolean act = Validate.isInString("12:522:52", "52", ":");
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isInString("12:52:52a", "25", ":");
		exp = false;
		Assert.assertEquals(exp, act);

		new tools.code.RunTimer().run("test", 100000, new Runnable() {

			@Override
			public void run() {
				Validate.isInString("12:52:52a", "25", ":");
			}
		});

		// ==测试,equals
		String name = "杜有发";
		String name2 = new String("杜有发");
		Assert.assertEquals(name, name2);
		Assert.assertEquals(false, name2 == name);// false

		int a = 100;
		int b = new Integer(100);
		Assert.assertEquals(a, b);
		Assert.assertEquals(true, a == b);// true

		Object n1 = null;
		Object n2 = null;
		Assert.assertEquals(n1, n2);
		Assert.assertEquals(true, n1 == n2);// true

		Object n3 = 100;
		Assert.assertEquals(n3, a);
	}

	@Test
	public void isDateTimeTest() {
		boolean act = Validate.isDateTime("2011-10-13 12:52:52", "yyyy-MM-dd HH:mm:ss", false);
		boolean exp = true;
		Assert.assertEquals(exp, act);

		act = Validate.isDateTime("12:52:52a", "yyyy-MM-dd HH:mm:ss", false);
		exp = false;
		Assert.assertEquals(exp, act);

		act = Validate.isDateTime("12:52:52", "HH:mm:ss", false);
		exp = true;
		Assert.assertEquals(exp, act);
	}
}
