package tools.token;

import java.util.Map;

/**
 * 2012-05-29
 * 
 * @author liusan.dyf
 */
public class SimpleParser {
	public static String execute(String content, String openToken, String closeToken, Map<String, Object> vars) {
		TokenHandler handler = new MapHandler(vars);
		GenericTokenParser p = new GenericTokenParser(openToken, closeToken, handler);
		return p.parse(content);
	}

	/**
	 * like c# string.format("{0},{1}...",1,2,3); 2012-09-28 by liusan.dyf
	 * 
	 * @param source
	 * @param args
	 * @return
	 */
	public static String format(String source, Object... args) {
		TokenHandler handler = new ArrayHandler(args);
		GenericTokenParser p = new GenericTokenParser("{", "}", handler);
		return p.parse(source);
	}

	public static void main(String[] args) {
		String text = "<div class=\"viewer\">${value}</div>";
		Map<String, Object> vars = tools.MapUtil.create();
		vars.put("value", "xxx");

		String output = execute(text, "${", "}", vars);
		System.out.println(output);

		// format 2012-09-28
		String v = format("{0} {1} {2} {3}", "hello", "world", "!");
		System.out.println(v);
	}
}
