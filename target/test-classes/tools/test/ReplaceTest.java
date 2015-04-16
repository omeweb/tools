package tools.test;

import tools.code.RunTimer;

public class ReplaceTest {
	public static void main(String[] args) {
		new RunTimer().run("replace", 100000, new Runnable() {
			@Override
			public void run() {
				tools.StringUtil.replace("abcd", "a", "b");// 42ms
				// replaceV2("abcd", "a", "b");// 44ms
				// tools.StringUtil.replaceAll("abcd", "a", "b");// 45ms
				// StringUtil.replaceAllArrayV1("abcd", new String[] { "a" }, new String[] { "b" });// 48ms/31ms
			}
		});

		System.out.println(tools.StringUtil.replace("abcda", "a", "b"));
		System.out.println(tools.StringUtil.replace("bbb", "b", "ab"));
	}

	public static String replaceV2(String strSource, String strFrom, String strTo) {
		if (strFrom == null || strFrom.equals(""))
			return strSource;

		String strDest = "";
		int intFromLen = strFrom.length();
		int intPos;
		while ((intPos = strSource.indexOf(strFrom)) != -1) {
			strDest = strDest + strSource.substring(0, intPos);
			strDest = strDest + strTo;
			strSource = strSource.substring(intPos + intFromLen);
		}
		strDest = strDest + strSource;
		return strDest;
	}
}
