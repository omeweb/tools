package tools.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import org.junit.Test;

import tools.code.RunTimer;
import tools.token.GenericTokenParser;
import tools.token.SimpleParser;
import tools.token.TokenHandler;

public class TokenTest {
	@Test
	public void test() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "杜有发");

		TokenHandler th = new TokenHandler() {
			@Override
			public String handle(String content) {
				return (String) map.get(content);
			}
		};

		final String template = "hello ${name}";
		final GenericTokenParser t = new GenericTokenParser("${", "}", th);

		Assert.assertEquals("hello 杜有发", t.parse(template));

		// 39ms
		new RunTimer().run("test", 100000, new Runnable() {
			@Override
			public void run() {
				t.parse(template);
			}
		});
	}

	@Test
	public void formatTest() {
		final String source = "{0} {1} {2} {3}";

		// 200ms
		new RunTimer().run("format", 100000, new Runnable() {
			@Override
			public void run() {
				SimpleParser.format(source, "hello", "world", "!");
			}
		});

		// 257ms
		new RunTimer().run("java", 100000, new Runnable() {
			@Override
			public void run() {
				java.text.MessageFormat.format(source, "hello", "world", "!");
			}
		});
	}
}
