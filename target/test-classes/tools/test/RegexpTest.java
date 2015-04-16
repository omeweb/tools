package tools.test;

import org.junit.Assert;
import org.junit.Test;

import jregex.Pattern;

public class RegexpTest {
	@Test
	public void t() {
		final Pattern p = new Pattern("(\\d)+");

		final java.util.regex.Pattern jp = java.util.regex.Pattern.compile("(\\d)+", 0);

		Assert.assertEquals(true, p.matcher("123456").matches());

		new tools.code.RunTimer().run("t", 100000, new Runnable() {

			@Override
			public void run() {
				// p.matcher("123456").matches();//90
				jp.matcher("123456").matches();// 50
			}
		});
	}
}
