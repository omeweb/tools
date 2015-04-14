package tools;

public class CharacterToAlpha {
	private static final char[] ALPHA_TABLE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	/**
	 * 汉字拼音首字母编码表，可以如下方法得到： 字母Z使用了两个标签，这里有２７个值, i, u, v都不做声母, 跟随前面的字母(因为不可以出现，所以可以随便取) private static final char[]
	 * chartable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌',
	 * '塌', '塌', '挖', '昔', '压', '匝', '座' }; private static final int[] table = new int[27]; static { for (int i = 0; i <
	 * 27; ++i) { table[i] = gbValue(chartable[i]); System.out.print(table[i]+" "); } }
	 */

	private static final int[] TABLE = new int[] { 45217, 45253, 45761, 46318, 46826, 47010, 47297, 47614, 47614,
			48119, 49062, 49324, 49896, 50371, 50614, 50622, 50906, 51387, 51446, 52218, 52218, 52218, 52698, 52980,
			53689, 54481, 55289 };

	private static final int CH = '*';
	private static final int SIZE = 26;

	/**
	 * 主函数, 输入字符, 得到他的声母, 英文字母返回对应的大写字母 其他非简体汉字返回 '*'
	 */
	public static char toAlpha(char ch) {
		if (ch >= 'a' && ch <= 'z')// 小写字母
			return (char) (ch - 'a' + 'A');

		if (ch >= 'A' && ch <= 'Z')// 大写字母
			return ch;

		int idx = getIndex(ch);
		if (idx < TABLE[0]) // TABLE是从小到大排列的
			return CH;

		for (int i = 0; i < SIZE; ++i) {
			if (matches(i, idx)) {
				return ALPHA_TABLE[i];
			}
		}
		return CH;
	}

	/**
	 * 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串
	 */
	public static String toAlpha(String v) {
		if (v == null)
			return null;

		int len = v.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char ch = toAlpha(v.charAt(i));

			if (ch != CH)
				sb.append(ch);
		}

		return sb.toString();
	}

	private static boolean matches(int i, int idx) {
		if (idx < TABLE[i])
			return false;

		int j = i + 1;

		// 字母Z使用了两个标签
		while (j < SIZE && (TABLE[j] == TABLE[i]))
			++j;

		if (j == SIZE)
			return idx <= TABLE[j];
		else
			return idx < TABLE[j];
	}

	/**
	 * 取出传入汉字的编码
	 */
	private static int getIndex(char ch) {
		char[] arr = new char[] { ch };
		String str = new String(arr);

		try {
			byte[] bytes = str.getBytes("GB2312");// 一定要是GB2312的编码 2015-3-18 19:14:30 by 六三
			if (bytes.length < 2)// 英文字符
				return 0;

			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			System.out.println(e);
			return CH;
		}
	}

	/**
	 * 测试输出
	 */
	public static void main(String[] args) {
		System.out.println(toAlpha("代刷信誉，好评！如有需要，联系qq"));
	}
}
