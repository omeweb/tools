package tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringUtil {
	private final static String DEFAULT_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final static String DEFAULT_CHARSET = "utf-8";
	public final static Random RANDOM = new Random();
	public static final String EMPTY = "";
	public static final String N = "N";
	// private static Set<Character> ZHCN_NUMBERS = null;

	/**
	 * 特殊字符都是一个字符，但是表示的数字可能有两位，所以是【Character,String】 2014-09-22
	 */
	public static Map<Character, String> specialNumberMap = new HashMap<Character, String>();

	/**
	 * mtee172021095028.pre.cm3/172.21.95.28 2013-03-08 by liusan.dyf
	 */
	public static String LOCAL_HOST = null;
	public static String LOCAL_IP = null;
	public static String LOCAL_HOST_NAME = null;

	static {
		// 主机名
		try {
			InetAddress addr = Inet4Address.getLocalHost();
			LOCAL_HOST = addr.toString();
			LOCAL_IP = addr.getHostAddress();
			LOCAL_HOST_NAME = addr.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// // 一些数字的汉字大写 2014-10-12 by liusan.dyf
		// Character[] zhcnNumbers = { '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '零', '壹', '贰', '叁', '肆', '伍',
		// '陆', '柒', '捌', '玖', '拾', '两' };
		// ZHCN_NUMBERS = new HashSet<Character>(zhcnNumbers.length);
		// for (char item : zhcnNumbers) {
		// ZHCN_NUMBERS.add(item);
		// }

		// 一批特殊符号类型的数字 2014-09-22 by liusan.dyf
		Character[] specialNumbers = { '⒛', '⒚', '⒙', '⒘', '⒗', '⒖', '⒕', '⒔', '⒓', '⒒', '⒑', '⒐', '⒏', '⒎', '⒍', '⒌',
				'⒋', '⒊', '⒉', '⒈',
				//
				'⒇', '⒆', '⒅', '⒄', '⒃', '⒂', '⒁', '⒀', '⑿', '⑾', '⑽', '⑼', '⑻', '⑺', '⑹', '⑸', '⑷', '⑶', '⑵', '⑴',
				//
				'⑩', '⑨', '⑧', '⑦', '⑥', '⑤', '④', '③', '②', '①', '〇',
				//
				'一', '二', '三', '四', '五', '六', '七', '八', '九', '十',
				//
				'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾', '两',
				//
				'０', '１', '２', '３', '４', '５', '６', '７', '８', '９',

				// 2014-11-24 by 六三
				'㈠', '㈡', '㈢', '㈣', '㈤', '㈥', '㈦', '㈧', '㈨', '㈩',

				// 2014-11-24 by 六三
				'❶', '❷', '❸', '❹', '❺', '❻', '❼', '❽', '❾', '❿',

				// 2014-11-24 by 六三
				'㊀', '㊁', '㊂', '㊃', '㊄', '㊅', '㊆', '㊇', '㊈', '㊉',

				//
				'⑪', '⑫', '⑬', '⑭', '⑮', '⑯', '⑰', '⑱', '⑲', '⑳',

				//
				'ⅰ', 'ⅱ', 'ⅲ', 'ⅳ', 'ⅴ', 'ⅵ', 'ⅶ', 'ⅷ', 'ⅸ', 'ⅹ',

				//
				'Ⅰ', 'Ⅱ', 'Ⅲ', 'Ⅳ', 'Ⅴ', 'Ⅵ', 'Ⅶ', 'Ⅷ', 'Ⅸ', 'Ⅹ',

				// 大小写的O
				'O', 'o', 'L', 'l', 'Z', 'z', '丨', '|',

				// 繁体 2014-11-27 by 六三
				'貳', '叄', '陸', };

		String[] realNumbers = {
				// ⒛-》⒈
				"20", "19", "18", "17", "16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3",
				"2", "1",

				// ⒇-》⑴
				"20", "19", "18", "17", "16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3",
				"2", "1",

				// ⑩-》〇
				"10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "0",

				// 汉字小写
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",

				// 汉字大写/金额写法
				"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "2",

				//
				"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",

				// ㈠ ~ ㈩ 2014-11-24 by 六三
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",

				// ❶->❿
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",

				// ㊀->㊉
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",

				// ⑪->⑳
				"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",

				// ⅰ=>ⅹ
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",

				// Ⅰ=>Ⅹ
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",

				// 大小写的O
				"0", "0", "1", "1", "2", "2", "1", "1",

				// 繁体 2014-11-27 by 六三
				"2", "3", "6", };

		if (specialNumbers.length != realNumbers.length) {
			throw new IllegalArgumentException("特殊符号类型的数字数组长度不一致，请检查");
		}

		int i = 0;
		int len = specialNumbers.length;
		for (i = 0; i < len; i++) {
			specialNumberMap.put(specialNumbers[i], realNumbers[i]);
		}
	}

	/**
	 * 循环文件里的每一行，返回总行数，2015-1-13 13:00:20 by 六三
	 * 
	 * @param path 完整路径
	 * @param charset gbk、utf-8
	 * @param buffer 1024 * 5 = 5K
	 * @param p 返回false则表示终止循环
	 * @return
	 * @throws Throwable
	 */
	public static long eachLine(String path, String charset, int buffer, Predicate<String> p) throws Throwable {
		// buffer, 5 * 1024 = 5K
		if (path == null || p == null)
			return 0;

		if (charset == null)
			charset = DEFAULT_CHARSET;

		if (buffer <= 0)
			buffer = 1024 * 5; // 默认5K

		long count = 0;

		BufferedReader reader = null;
		try {
			File file = new File(path);
			if (!file.exists())
				return 0; // 文件不存在

			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
			reader = new BufferedReader(new InputStreamReader(fis, charset), buffer);
			// 5 * 1024 * 1024 = 用5M的缓冲读取文本文件
			String line = null;
			boolean flag;
			while ((line = reader.readLine()) != null) {
				try {
					flag = p.execute(line);
					count++;
					if (!flag)
						break;
				} finally {
					// 什么也不做
				}
			}
		} finally {
			if (reader != null)
				reader.close();
		}

		return count;
	}

	/**
	 * 得到一段文本里的数字 2014-06-23 by liusan.dyf
	 * 
	 * @param v
	 * @return
	 */
	public static String getNumbers(String v) {
		if (v == null)
			return null;

		StringBuilder sb = new StringBuilder();
		int len = v.length();

		char ch;
		for (int i = 0; i < len; i++) {
			ch = v.charAt(i);

			if (ch >= 48 && ch <= 57) {
				sb.append(ch);
			} else if (specialNumberMap.containsKey(ch)) {
				sb.append(specialNumberMap.get(ch));// 2014-09-22，针对汉字里的特殊符号数字做替换
			}
		}

		return sb.toString();
	}

	/**
	 * 2014-06-23 by liusan.dyf
	 * 
	 * @param v
	 * @return
	 */
	public static String getLetters(String v) {
		if (v == null)
			return null;

		StringBuilder sb = new StringBuilder();
		int len = v.length();

		char ch;
		for (int i = 0; i < len; i++) {
			ch = v.charAt(i);
			if (Validate.isAsciiAlphaChar(ch)) {
				sb.append(ch);
			}
		}

		return sb.toString();
	}

	/**
	 * 使用正则匹配。?表示一个字符、*表示多个字符 2014-05-29 by liusan.dyf
	 * 
	 * @param input 不能包含\n
	 * @param p 不能含有括号，要转义
	 * @return
	 */
	public static boolean matches(String input, String p) {
		if (input == null || p == null)
			return false;

		p = p.replace('?', '.'); // 把keyWord字符串中的"?"替换成"."，"*"替换成".*"。
		p = p.replace("*", ".*");

		// try {
		Pattern pattern = Pattern.compile(p, 0);
		return pattern.matcher(input).matches();
		// } catch (Exception e) {
		// return false;
		// }
	}

	// private final static Pattern NO_ILLEGAL_CHARACTER_PATTERN = Pattern.compile("[A-Za-z\\d\\u4E00-\\u9FA5]+", 0);

	/**
	 * 看test在source里出现的次数
	 * 
	 * @param source
	 * @param test
	 * @return
	 */
	public static int count(String source, String test) {
		if (source == null)
			return 0;

		int count = 0;
		int start = 0;
		int end;

		int len = test.length();

		// Scan s and count the tokens.
		while ((end = source.indexOf(test, start)) != -1) {
			count++;
			start = end + len;
		}

		return count;
	}

	/**
	 * 过滤特殊字符，只允许数字、字母、中文 2013-01-06 by liusan.dyf
	 * 
	 * @param v
	 * @return
	 */
	public static String removeSpecialCharacters(String v) {
		if (v == null)
			return null;

		char[] chars = v.toCharArray();
		int pos = 0;
		char ch;
		for (int i = 0; i < chars.length; i++) {
			ch = chars[i];

			if (Validate.isAsciiNumericChar(ch)// 数字
					|| Validate.isAsciiAlphaChar(ch)// Character.isLetter(ch)// 字母
					|| Validate.isChineseChar(ch)) {
				chars[pos++] = chars[i];
			}
		}
		return new String(chars, 0, pos);
	}

	/**
	 * 2013-12-02 by liusan.dyf
	 * 
	 * @param v
	 * @param removes
	 * @return
	 */
	public static String removeCharacters(String v, String removes) {
		if (v == null)
			return null;

		// 没有要替换的
		if (removes == null)
			return v;

		// 先生成要删除的字符set
		Set<Character> set = new HashSet<Character>();
		for (int i = 0; i < removes.length(); i++) {
			set.add(removes.charAt(i));
		}

		return removeCharacters(v, set);
	}

	/**
	 * 2015-1-19 15:53:15 by 六三
	 * 
	 * @param v
	 * @param set 要清理的char列表
	 * @return
	 */
	public static String removeCharacters(String v, Set<Character> set) {
		if (v == null)
			return null;

		if (set == null)
			return v;

		// 循环遍历
		char[] chars = v.toCharArray();
		int pos = 0;
		char ch;
		for (int i = 0; i < chars.length; i++) {
			ch = chars[i];

			// 如果不包括
			if (!set.contains(ch)) {
				chars[pos++] = chars[i];
			}
		}

		return new String(chars, 0, pos);
	}

	public static Map<String, Integer> analyzeString(String value) {
		String source = Convert.toString(value);

		// 验证参数
		if (Validate.isNullOrEmpty(source))
			return MapUtil.create(0);

		Map<String, Integer> rtn = MapUtil.create();
		rtn.put("zh", 0);// 中文，中文字母也算
		rtn.put("digit", 0);// 数字
		rtn.put("alpha", 0);// 字母
		rtn.put("other", 0);// 其他字符

		// 循环测试
		int sourceLength = source.length();
		String key = null;
		char ch;
		for (int i = 0; i < sourceLength; i++) {
			ch = source.charAt(i);

			if (Validate.isAsciiNumericChar(ch))
				key = "digit";
			else if (Validate.isChineseChar(ch))
				key = "zh";
			else if (Validate.isAsciiAlphaChar(ch))
				key = "alpha";
			else
				key = "other";

			// 加载
			if (key != null) {
				int count = rtn.get(key);
				count++;
				rtn.put(key, count);
			}
		}

		return rtn;
	}

	/**
	 * 判断test里的字符在source里出现的频率。test串不宜太长，否则返回的map过大。不会返回null <br />
	 * 如果某字符不存在，返回0
	 * 
	 * @param value
	 * @param test
	 * @return
	 */
	public static Map<Character, Integer> getCharFrequence(Object value, String test) {
		String source = Convert.toString(value);

		// 验证参数
		if (Validate.isNullOrEmpty(source) || Validate.isNullOrEmpty(test))
			return MapUtil.create(0);

		int testLength = test.length();

		Map<Character, Integer> rtn = MapUtil.create(testLength);

		// 处理测试串的map
		Map<Character, Byte> testMap = MapUtil.create();

		for (int i = 0; i < testLength; i++) {
			char ch = test.charAt(i);
			testMap.put(ch, (byte) 1);// 要测试的map
			rtn.put(ch, 0);// 返回的map
		}

		// 循环测试
		int sourceLength = source.length();
		for (int i = 0; i < sourceLength; i++) {
			char ch = source.charAt(i);

			if (testMap.containsKey(ch)) {// 要查找的字符
				// if (rtn.containsKey(ch)) {
				int count = rtn.get(ch);
				count++;
				rtn.put(ch, count);
				// } else {
				// rtn.put(ch, 1);
				// }
			}
		}

		return rtn;
	}

	/**
	 * MD5的算法在RFC1321 中定义 在 Java 中，java.security.MessageDigest 中已经定义了 MD5 的计算，所以我们只需要简单地调用即可得到 MD5 的128 位整数。然后将此 128 位计
	 * 16 个字节转换成 16 进制表示即可。 在RFC 1321中，给出了Test suite用来检验你的实现是否正确： <br />
	 * MD5 ("") = d41d8cd98f00b204e9800998ecf8427e <br />
	 * MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661 <br />
	 * MD5 ("abc") =900150983cd24fb0d6963f7d28e17f72 <br />
	 * MD5 ("message digest") =f96b697d7cb7938d525a2f31aaf161d0 <br />
	 * MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b <br />
	 * 
	 * @author haogj
	 *         <p/>
	 *         传入参数：一个字节数组 传出参数：字节数组的 MD5 结果字符串
	 */
	public static String md5(byte[] source) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char chars[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				chars[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
				// >>> 为逻辑右移，将符号位一起右移
				chars[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(chars); // 换后的结果转换为字符串
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * @param testValue abc
	 * @param sourceValue a,c,r
	 * @return true
	 */
	public static boolean endsWithAny(Object testValue, Object sourceValue) {
		if (testValue == null || sourceValue == null)
			return false;

		// 得到字符串
		String test = Convert.toString(testValue), source = Convert.toString(sourceValue);
		if (test == null || source == null)
			return false;

		if (test.length() == 0)
			return false;

		// 执行匹配
		StringTokenizer st = new StringTokenizer(source, ",\n", false);
		while (st.hasMoreTokens()) {
			if (test.endsWith(st.nextToken()))
				return true;
		}

		return false;
	}

	/**
	 * @param testValue abc
	 * @param sourceValue a,e,r
	 * @return true
	 */
	public static boolean startsWithAny(Object testValue, Object sourceValue) {
		if (testValue == null || sourceValue == null)
			return false;

		// 得到字符串
		String test = Convert.toString(testValue), source = Convert.toString(sourceValue);
		if (test == null || source == null)
			return false;

		if (test.length() == 0)
			return false;

		// 执行匹配
		StringTokenizer st = new StringTokenizer(source, ",\n", false);
		String item = null;
		while (st.hasMoreTokens()) {
			item = st.nextToken();
			if (test.startsWith(item)) {
				// if (item.length() > 0)// 2013-04-26，去除空串 empty
				return true;
			}
		}

		return false;
	}

	public static boolean containsAny(Object testValue, Object sourceValue) {
		return containsAny(testValue, sourceValue, null);
	}

	/**
	 * 左边的串，包含了右边的任何一个子串。sourceValue可以用换行或者逗号分隔 2012-12-07 by liusan.dyf<br />
	 * 目前为了提升性能，暂时只支持以\n分隔的sourceValue
	 * 
	 * @param testValue liusan.dyf@taobao.com
	 * @param sourceValue liusan,dyf,taobao
	 * @param ctx 用来保存被命中的词的map
	 * @return true
	 */
	public static boolean containsAny(Object testValue, Object sourceValue, Map<String, Object> ctx) {
		if (testValue == null || sourceValue == null)
			return false;

		// 得到字符串
		String test = Convert.toString(testValue), source = Convert.toString(sourceValue);
		if (test == null || source == null)
			return false;

		if (test.length() == 0)
			return false;

		// test = test.toLowerCase();// 2013-04-23 by liusan.dyf 忽略大小写

		/* 方法1 */

		// StringTokenizer st = new StringTokenizer(source, ",\n", false);
		// while (st.hasMoreTokens()) {
		// if (test.indexOf(st.nextToken()) > -1)
		// return true;
		// }

		/* 方法2 */

		// String[] arr = split(source, "\n");
		// for (String item : arr) {
		// if (test.indexOf(item) > -1)
		// return true;
		// }

		/* 方法3 */

		char delimiter = '\n';
		int i = 0;
		int j = source.indexOf(delimiter);
		String item;

		while (j >= 0) {
			item = source.substring(i, j);// .toLowerCase();// 忽略大小写 2013-04-23

			if (test.indexOf(item) > -1) {// 查找
				// 解决indexOf（空串）总返回0的问题 2013-04-07 by liusan.dyf
				if (item.length() > 0) {// 只有当item的长度不为0的情况下才返回 2013-03-29 by liusan.dyf
					if (ctx != null)
						ctx.put("__hit", item);// 表示命中的词 2014-07-29 by liusan.dyf
					return true;
				}
			}

			i = j + 1;
			j = source.indexOf(delimiter, i);
		}

		// 最后一个
		item = source.substring(i);// .toLowerCase();// 忽略大小写 2013-04-23

		if (test.indexOf(item) > -1) {
			if (item.length() > 0) {// 只有当item的长度不为0的情况下才返回 2013-03-29 by liusan.dyf
				if (ctx != null)
					ctx.put("__hit", item);// 表示命中的词 2014-07-29 by liusan.dyf
				return true;
			}
		}

		/* 其他方法 */

		return false;
	}

	/*************** ngrams 2013-06-04 by liusan.dyf *********************************/
	// more http://stackoverflow.com/questions/3656762/n-gram-generation-from-a-sentence

	/**
	 * 用空格分隔
	 * 
	 * @param text
	 * @param n
	 * @return
	 */
	public static List<String> ngram(String text, int n) {
		String[] parts = split(text, " ");
		int itemCount = parts.length;
		List<String> result = new ArrayList<String>(itemCount - n + 1);

		for (int i = 0; i < itemCount - n + 1; i++) {
			StringBuilder sb = new StringBuilder();
			for (int k = 0; k < n; k++) {
				if (k > 0)
					sb.append(' ');
				sb.append(parts[i + k]);
			}
			result.add(sb.toString());
		}

		return result;
	}

	/**
	 * 无字符分隔 2013-06-13 by liusan.dyf
	 * 
	 * @param text
	 * @param n
	 * @return
	 */
	public static List<String> ngramNoSplitVersion(String text, int n) {
		if (text == null || n <= 0)
			return new ArrayList<String>(0);

		int len = text.length();
		int size = len - n + 1;// list总大小
		if (size <= 0)
			return new ArrayList<String>(0);

		//
		List<String> list = new ArrayList<String>(size);

		for (int i = 0; i < size; i++) {
			list.add(text.substring(i, i + n));
		}

		return list;
	}

	/************** ngrams **********************************/

	public static String[] split(String string, char delimiter) {
		if (string == null || string.length() == 0)// 2014-12-01 by 六三
			return null;

		String[] temp = new String[(string.length() / 2) + 1];
		int count = 0;
		int i = 0;
		int j = string.indexOf(delimiter);

		while (j >= 0) {
			temp[count++] = string.substring(i, j);
			i = j + 1;
			j = string.indexOf(delimiter, i);
		}

		temp[count++] = string.substring(i);
		String[] result = new String[count];
		System.arraycopy(temp, 0, result, 0, count);
		return result;
	}

	/**
	 * sourceValue可以用换行或者逗号分隔，2012-12-07 by liusan.dyf<br />
	 * 为提升大串的匹配效率，规定elementMaxLength<=128，此情况不解决换行和逗号同时出现的场景 2012-12-14
	 * 
	 * @param testValue b
	 * @param sourceValue a,b,c,d
	 * @return true
	 */
	public static boolean inSet(Object testValue, Object sourceValue) {
		if (testValue == null || sourceValue == null)
			return false;

		// 得到字符串
		String test = Convert.toString(testValue), source = Convert.toString(sourceValue);
		if (test == null || source == null)
			return false;

		if (test.length() == 0)
			return false;

		// 执行匹配
		char[] chars = new char[] { '\n', ',' };//

		for (char item : chars) {
			if (source.indexOf(item + test + item) > -1)
				return true;

			if (source.startsWith(test + item) || source.endsWith(item + test))
				return true;

			if (test.equals(source))// 2012-12-13
				return true;

			// 下面的情况不解决【\n、,】同时出现的情况
			if (item == '\n') {// 优先判断换行符，改善匹配不到的性能 2012-12-14
				int len = source.length();
				int elementMaxLength = 128;// 假设每个元素的最大长度为128
				if (len > elementMaxLength) {
					String sub = source.substring(0, elementMaxLength + 1);// 取source的前elementMaxLength，看看有没有分隔符

					if (sub.indexOf(',') == -1)// 判断有没有下一个要查找的分隔符
						return false;
				}

			}
		}

		return false;
	}

	/**
	 * 2012-12-07 by liusan.dyf，sourceValue要以【,\n】分割的字符串。性能略差，请用inSet
	 * 
	 * @param testValue
	 * @param sourceValue
	 * @return
	 */
	public static boolean inSet_(Object testValue, Object sourceValue) {
		if (testValue == null || sourceValue == null)
			return false;

		// 得到字符串
		String test = Convert.toString(testValue), source = Convert.toString(sourceValue);
		if (test == null || source == null)
			return false;

		// 查找
		int len = test.length();
		int sourceLen = source.length();
		int p = source.indexOf(test);
		if (p == -1)// 找不到
			return false;

		if (p == 0) {// 第一个就是，接着判断下一个字符是否是分割符，如果是，返回true
			int nextCharIndex = p + len;
			char ch = source.charAt(nextCharIndex);

			if (ch == ',' || ch == '\n')
				return true;
		}

		// 继续查找
		Character prev, last;
		while ((p = source.indexOf(test, p)) > -1) {
			prev = null;
			last = null;

			// 判断前一个字符是否是分隔符
			if (p > 0)
				prev = source.charAt(p - 1);
			else
				prev = ',';// 最开始，设置为合法

			// 判断后一个字符是否是分隔符
			int nextCharIndex = p + len;

			if (nextCharIndex < sourceLen)
				last = source.charAt(nextCharIndex);
			else
				last = ',';// 最末尾，设置为合法

			// 头尾都为分隔符，则返回true
			if (prev == ',' || prev == '\n') {
				if (last == ',' || last == '\n')
					return true;
			}

		}

		return false;
	}

	/**
	 * 和http://www.cmd5.com/的结果保持一致
	 * 
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		if (str == null)
			return null;
		return md5(str.getBytes(Charset.forName(DEFAULT_CHARSET)));
	}

	/**
	 * salt末尾加的 2012-02-27
	 * 
	 * @param rawContent
	 * @param salt
	 * @return
	 */
	public static String signContent(String rawContent, String salt) {
		String md5 = md5(rawContent + salt);

		return rawContent + md5;
	}

	/**
	 * salt末尾加的 2012-02-27
	 * 
	 * @param signedContent
	 * @param salt
	 * @return
	 */
	public static String getRawContent(String signedContent, String salt) { // rawContent这里作为一个输出参数
		String rtn = null;

		if (signedContent == null)
			return null;

		int baseLen = 32;

		int len = signedContent.length();
		if (len <= baseLen)
			return null;

		String md5 = signedContent.substring(len - baseLen);// 最后32位
		// System.out.println("validateSignedContent.md5：" + md5);
		rtn = signedContent.substring(0, len - baseLen);// 原始内容
		// System.out.println("validateSignedContent.rtn：" + rtn);

		if (md5.equalsIgnoreCase(md5(rtn + salt)))
			return rtn;

		return null;
	}

	public static String getLocalIp() {
		// try {
		// return (Inet4Address.getLocalHost().getHostAddress());
		// // System.out.println(Inet4Address.getByName("www.omeweb.com").getHostAddress());
		// } catch (UnknownHostException e) {
		//
		// }
		// return null;

		return LOCAL_IP;
	}

	/**
	 * 得到【name=杜有发,age=28】里的name的值，如果key有多个，只返回第一个 2012-04-25
	 * 
	 * @param source
	 * @param name
	 * @param separator 分隔符，推荐用逗号，不过可以用更特殊的符号
	 * @return
	 */
	public static String getFeature(String source, String name, String separator) {
		if (isNullOrEmpty(source))
			return EMPTY;

		/**
		 * 目前测试失败
		 */

		// // to do 有性能改善的空间,可以把exp缓存起来
		// Pattern exp = Pattern.compile("(^|" + separator + "|\\s)" + name
		// + "\\s*=\\s*([^" + separator + "]*)(\\s|" + separator + "|$)",
		// Pattern.CASE_INSENSITIVE);
		//
		// // System.out.println(exp);
		//
		// Matcher mt = exp.matcher(source);
		// if (mt.matches())
		// return mt.group(2).toString();
		// return "";

		/**
		 * 更换方案 2012-04-25
		 */
		// 注意这里的delimiters不能是【name=,】，这样无法解析name=name的情况
		StringTokenizer st = new StringTokenizer(source, "=" + separator, false);
		// StringTokenizer st = new StringTokenizer(token, "=,", false);
		while (st.hasMoreTokens()) {
			// System.out.println("Remaining Tokens : " + st.countTokens());
			// System.out.print(st.nextToken());

			if (st.nextToken().equals(name))
				return (st.nextToken());
		}

		return EMPTY;
	}

	/**
	 * 编码HTML(将>,<,",& 转换成&gt;,&lt;,&quot;,&amp;)(高效率，来自FreeMarker模板源码，比replaceAll速度快很多)
	 * 
	 * @param html
	 * @return
	 */
	public static String encodeHTML(String html) {
		if (Validate.isEmpty(html))
			return null;
		int ln = html.length();
		char c;
		StringBuffer b;
		for (int i = 0; i < ln; i++) {
			c = html.charAt(i);
			if (c == '<' || c == '>' || c == '&' || c == '"') {
				b = new StringBuffer(html.substring(0, i));
				switch (c) {
					case '<':
						b.append("&lt;");
						break;
					case '>':
						b.append("&gt;");
						break;
					case '&':
						b.append("&amp;");
						break;
					case '"':
						b.append("&quot;");
						break;
				}
				i++;
				int next = i;
				while (i < ln) {
					c = html.charAt(i);
					if (c == '<' || c == '>' || c == '&' || c == '"') {
						b.append(html.substring(next, i));
						switch (c) {
							case '<':
								b.append("&lt;");
								break;
							case '>':
								b.append("&gt;");
								break;
							case '&':
								b.append("&amp;");
								break;
							case '"':
								b.append("&quot;");
								break;
						}
						next = i + 1;
					}
					i++;
				}
				if (next < ln)
					b.append(html.substring(next));
				html = b.toString();
				break;
			}
		}
		return html;
	}

	/**
	 * 解码HTML(将&gt;,&lt;,&quot;,&amp;转换成>,<,",& )
	 * 
	 * @param html
	 * @return
	 */
	public static String decodeHTML(String html) {
		if (Validate.isEmpty(html))
			return null;
		String[] replaceStr = { "&amp;", "&lt;", "&gt;", "&quot;" };
		String[] newStr = { "&", "<", ">", "\"" };
		return replaceAllArray(html, replaceStr, newStr);
	}

	/**
	 * 2012-06-14 by liusan.dyf
	 * 
	 * @param str
	 * @return
	 */
	public static String removeHTML(String str) {
		if (Validate.isEmpty(str))
			return null;

		return tools.token.SimpleParser.execute(trimEx(str), "<", ">", null);
	}

	/**
	 * 2012-06-12 by liusan.dyf
	 * 
	 * @param sb
	 */
	public static void removeLastChar(StringBuilder sb) {
		if (sb == null || sb.length() == 0)
			return;

		sb.deleteCharAt(sb.length() - 1);
	}

	/**
	 * 2014-11-13 by 六三
	 * 
	 * @param text
	 * @param replaceStrs
	 * @param newStrs
	 * @return
	 */
	public static String replaceAllArray(String text, String[] replaceStrs, String[] newStrs) {
		if (isNullOrEmpty(text) || replaceStrs == null || newStrs == null)
			return text;

		if (replaceStrs.length == 0 || newStrs.length == 0)
			return text;

		if (replaceStrs.length != newStrs.length)
			return text;

		int i = 0;
		for (String item : replaceStrs) {
			text = replace(text, item, newStrs[i]);
			i++;
		}

		return text;
	}

	/**
	 * 有可能出现死循环，但性能不错 2014-11-13 by 六三<br />
	 * 替换指定的字符串数组为一个字符串数组<br />
	 * 速度比String.replaceAll快3倍左右，比apache-common StringUtils.replace快2倍左右
	 * 
	 * @param text
	 * @param replaceStrs
	 * @param newStrs
	 * @return
	 */
	@Deprecated
	public static String replaceAllArrayV1(String text, String[] replaceStrs, String[] newStrs) {
		// replaceStrs和newStrs的长度应该一致
		if (isNullOrEmpty(text))
			return text;

		StringBuilder str = new StringBuilder();
		for (int i = 0; i < replaceStrs.length; i++) {
			String replaceStr = replaceStrs[i];
			int index = text.indexOf(replaceStr);// 是否包含要替换的串
			if (index >= 0) {// 如果包含，则
				String afterStr = null;
				if (index > 0) {
					String beforeStr = text.substring(0, index);// 该串之前的串
					afterStr = text.substring(index + replaceStr.length());// 该串之后的串
					str.append(replaceAllArray(beforeStr, replaceStrs, newStrs));// 之前的串还有可能包含要替换的数组的其他元素
				} else
					afterStr = text.substring(replaceStr.length());// 从第一个字符开始的

				str.append(newStrs[i]);
				str.append(replaceAllArray(afterStr, replaceStrs, newStrs));// 之后的串继续替换
				break;
			}
		}

		if (str.length() == 0)
			return text;

		return str.toString();
	}

	/**
	 * 替换指定的字符串为一个字符串<br />
	 * 
	 * @param text
	 * @param replaceStr
	 * @param newStr
	 * @return
	 */
	public static String replaceAll(String text, String replaceStr, String newStr) {
		return replace(text, replaceStr, newStr);
	}

	public static String replace(String strSource, String strFrom, String strTo) {
		if (strSource == null) {
			return null;
		}

		int i = 0;
		if ((i = strSource.indexOf(strFrom, i)) >= 0) {
			char[] cSrc = strSource.toCharArray();
			char[] cTo = strTo.toCharArray();
			int len = strFrom.length();

			StringBuffer buf = new StringBuffer(cSrc.length);
			buf.append(cSrc, 0, i).append(cTo);
			i += len;
			int j = i;
			while ((i = strSource.indexOf(strFrom, i)) > 0) {
				buf.append(cSrc, j, i - j).append(cTo);
				i += len;
				j = i;
			}
			buf.append(cSrc, j, cSrc.length - j);

			return buf.toString();
		}

		return strSource;
	}

	/**
	 * a=1,b=2，目前不解决【a=1,b=,c=3】，已经能解决 2014-09-03 by liusan.dyf
	 * 
	 * @param source
	 * @param on &/,
	 * @param kvSeparator =
	 * @param charset
	 * @return
	 */
	public static Map<String, String> stringToMap(String source, String on, String kvSeparator, String charset) {
		if (isNullOrEmpty(source))
			return null;

		String delimiters = kvSeparator + on; // [=,]

		if (source.contains(delimiters) || source.endsWith(kvSeparator)) {
			source += kvSeparator;// [a=1,b=] => [a=1,b=,]
			source = replaceAll(source, delimiters, kvSeparator + N + on); // [a=1,b=,] => [a=1,b=N,]
		}
		// 键值对之间的分隔符，为单字符。键值对中间还是约定用等号（=）连接

		// 是否要解码
		boolean emptyCharset = Validate.isNullOrEmpty(charset);

		/**
		 * StringTokenizer方案
		 */
		Map<String, String> map = MapUtil.create();
		StringTokenizer st = new StringTokenizer(source, delimiters, false);

		while (st.hasMoreTokens()) {
			String key = st.nextToken();
			String value = null;

			if (st.hasMoreTokens())// 可能不再包含了
				value = st.nextToken();
			else
				continue;

			if (isNullOrEmpty(key))// 空的key过滤掉
				continue;

			if (value.equals(N))
				continue;// 2014-09-03 by liusan.dyf

			// 解码
			if (!emptyCharset) {
				try {
					key = java.net.URLDecoder.decode(key, charset);
					value = java.net.URLDecoder.decode(value, charset);
					map.put(key, value);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			} else
				map.put(key, value);

			// end
		}

		return map;
	}

	/**
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		return str == null ? null : str.trim();
	}

	/**
	 * trimStart,trimEnd集成到一起 2012-11-13 by liusan.dyf
	 * 
	 * @param str
	 * @param ch
	 * @return
	 */
	public static String trim(String str, char ch) {
		if (str == null)
			return null;

		char[] chars = str.toCharArray();
		int len = chars.length;

		if (len == 0)
			return str;

		// 从开头查找第一个不为ch的index
		int start = 0;
		while (start < len) {
			if (chars[start] != ch)
				break;

			start++;
		}

		// 从结尾倒着找第一个不为ch的index
		int end = len - 1;
		while (end >= 0) {
			if (chars[end] != ch) {
				break;
			}

			end--;
		}

		if (end < start)// "aaaaa"，trim 'a'的情况
			return "";

		return new String(chars, start, end - start + 1);
	}

	/**
	 * Blank NewLine tab都过滤掉 2012-06-14
	 * 
	 * @param v
	 * @return
	 */
	public static String trimEx(String v) {
		if (v == null)
			return null;

		// 循序替换
		int len = v.length();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			char ch = v.charAt(i);
			if (ch != ' ' && ch != '\r' && ch != '\n' && ch != '\t')
				sb.append(ch);
		}

		return sb.toString();
	}

	public static boolean isNullOrEmpty(String value) {
		return Validate.isNullOrEmpty(value);
	}

	public static String urlEncode(String url, String charset) {
		// http://127.0.0.1/?a=b&c=d ->
		// http%3A%2F%2F127.0.0.1%2F%3Fa%3Db%26c%3Dd
		try {
			return java.net.URLEncoder.encode(url, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String urlDecode(String url, String charset) {
		// http://127.0.0.1/?a=b&c=d ->
		// http%3A%2F%2F127.0.0.1%2F%3Fa%3Db%26c%3Dd
		try {
			return java.net.URLDecoder.decode(url, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 2012-02-13 目前没有解决重KEY的问题，比如【a=1&a=2&a=3】
	 * 
	 * @param q
	 * @param charset
	 * @return
	 */
	public static Map<String, String> parseQueryString(final String q, String charset) {
		if (q == null)
			return null;

		return stringToMap(q, "&", "=", charset);
	}

	private static String encodeCharacterANSI(char c) {
		// from
		// http://code.google.com/p/owasp-esapi-java/source/browse/trunk/src/main/java/org/owasp/esapi/codecs/MySQLCodec.java
		if (c == '\'')
			return "\'\'";
		if (c == '\"')
			return "";
		return "" + c;
	}

	@SuppressWarnings("unused")
	private static String encodeCharacterMySQL(char ch) {
		// from
		// http://owasp-esapi-java.googlecode.com/svn/trunk/src/main/java/org/owasp/esapi/codecs/MySQLCodec.java

		// 更多可以参考
		// https://www.owasp.org/index.php/SQL_Injection_Prevention_Cheat_Sheet
		if (ch == 0x00)
			return "\\0";
		if (ch == 0x08)
			return "\\b";
		if (ch == 0x09)
			return "\\t";
		if (ch == 0x0a)
			return "\\n";
		if (ch == 0x0d)
			return "\\r";
		if (ch == 0x1a)
			return "\\Z";// 
		if (ch == 0x22)
			return "\\\"";
		if (ch == 0x25)
			return "\\%";
		if (ch == 0x27)
			return "\\'";
		if (ch == 0x2d)
			return "\\-";
		if (ch == 0x3b)
			return "\\;";
		if (ch == 0x5c)
			return "\\\\";
		if (ch == 0x5f)
			return "\\_";

		return ch + EMPTY;// return "\\" + ch;
	}

	public static String getRndChars(int len, String source) {
		// string[] arr = source.Split(',');
		// string[] arr = source.Split(',');

		if (source == null)
			source = DEFAULT_CHARS;

		StringBuilder sb = new StringBuilder();

		// 采用一个简单的算法以保证生成随机数的不同
		int c = source.length();
		for (int i = 1; i < len + 1; i++) {
			sb.append(source.charAt(RANDOM.nextInt(c - 1)));
		}
		return sb.toString();
	}

	public static String getRndChars(int len) {
		return getRndChars(len, null);
	}

	public static String escapeSQL(String v) {
		// 过滤非法字符
		// if (str == null)
		// return "";
		// else {
		// return replaceAllArray(str, new String[] { "'", "<", ">" },
		// new String[] { "&#39;", "&lt;", "&gt;" });
		// }

		if (v == null)
			return "";

		StringBuilder sb = new StringBuilder();
		int len = v.length();

		for (int i = 0; i < len; i++) {
			char ch = v.charAt(i);
			sb.append(encodeCharacterANSI(ch));
		}

		return sb.toString();
	}

	public static String escapeJSON(String str) {
		if (str == null)
			return "";

		StringBuffer retval = new StringBuffer();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
				case 0:
					continue;
				case '\b':
					retval.append("\\b");
					continue;
				case '\t':
					retval.append("\\t");
					continue;
				case '\n':
					retval.append("\\n");
					continue;
				case '\f':
					retval.append("\\f");
					continue;
				case '\r':
					retval.append("\\r");
					continue;
				case '\"':
					retval.append("\\\"");
					continue;
				case '\'':
					retval.append("\\\'");
					continue;
				case '\\':
					retval.append("\\\\");
					continue;
				default:
					if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
						// String s = "0000" + Integer.toString(ch, 16);
						// retval.append("\\u" + s.substring(s.length() - 4,
						// s.length()));
					} else {
						// retval.append(ch);
					}

					retval.append(ch);// 中文不做编码 2011-10-18 by 63
					continue;
			}
		}
		return retval.toString();
	}

	/**
	 * 按照整个delimiter来分隔字符串
	 * 
	 * @param s
	 * @param delimiter
	 * @return
	 */
	public static String[] split(String s, String delimiter) {
		if (s == null) {
			return null;
		}
		int delimiterLength;
		int stringLength = s.length();
		if (delimiter == null || (delimiterLength = delimiter.length()) == 0) {
			return new String[] { s };
		}

		// a two pass solution is used because a one pass solution would
		// require the possible resizing and copying of memory structures
		// In the worst case it would have to be resized n times with each
		// resize having a O(n) copy leading to an O(n^2) algorithm.

		int count;
		int start;
		int end;

		// Scan s and count the tokens.
		count = 0;
		start = 0;
		while ((end = s.indexOf(delimiter, start)) != -1) {
			count++;
			start = end + delimiterLength;
		}
		count++;

		// allocate an array to return the tokens,
		// we now know how big it should be
		String[] result = new String[count];

		// Scan s again, but this time pick out the tokens
		count = 0;
		start = 0;
		while ((end = s.indexOf(delimiter, start)) != -1) {
			result[count] = (s.substring(start, end));
			count++;
			start = end + delimiterLength;
		}
		end = stringLength;
		result[count] = s.substring(start, end);

		return (result);
	}

	/**
	 * 用StringTokenizer，可以按照delimiters提供的字符依次split 2012-10-11 by liusan.dyf
	 * 
	 * @param source
	 * @param delimiters
	 * @return
	 */
	public static String[] splitEx(String source, String delimiters) {
		List<String> rtn = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(source, delimiters, false);
		// StringTokenizer st = new StringTokenizer(token, "=,", false);
		while (st.hasMoreTokens()) {
			rtn.add(st.nextToken());
		}

		return (String[]) rtn.toArray(new String[rtn.size()]);
	}

	public static String left(String source, String delimiter) {
		return left(source, delimiter, false);
	}

	public static String right(String source, String delimiter) {
		return right(source, delimiter, false);
	}

	/**
	 * 得到某字符左边的字符串 2012-04-19
	 * 
	 * @param source
	 * @param delimiter
	 * @param lastIndexOf
	 * @return
	 */
	public static String left(String source, String delimiter, boolean lastIndexOf) {
		if (source == null)
			return null;

		int p = indexOf(source, delimiter, lastIndexOf);
		if (p == -1)
			return null;
		return source.substring(0, p);
	}

	/**
	 * 得到某字符右边的字符串，如果不存在delimiter，则返回null 2012-04-19
	 * 
	 * @param source
	 * @param delimiter
	 * @param lastIndexOf
	 * @return
	 */
	public static String right(String source, String delimiter, boolean lastIndexOf) {
		if (source == null)
			return null;

		int p = indexOf(source, delimiter, lastIndexOf);
		if (p == -1)
			return null;
		return source.substring(p + delimiter.length());

		// return source.substring(source.indexOf(ch) + 1);
	}

	public static int indexOf(String source, String testString, boolean lastIndexOf) {
		if (source == null || testString == null)
			return 0;
		if (lastIndexOf)
			return source.lastIndexOf(testString);
		return source.indexOf(testString);
	}

	/**
	 * 2012-12-06 by liusan.dyf
	 * 
	 * @param source
	 * @param testValue
	 * @return
	 */
	@Deprecated
	public static boolean contains_(Object source, Object testValue) {
		if (source == null || testValue == null)
			return false;

		// 如果是字符串
		if (source instanceof String) {

		} else
			source = source.toString();// 做转换

		if (testValue instanceof String)
			return ((String) source).indexOf((String) testValue) > -1;
		else if (testValue instanceof Character) {
			return ((String) source).indexOf((Character) testValue) > -1;
		}

		return false;
	}

	public static void main(String[] args) {
		String s = getRndChars(10, null);
		System.out.println(s);

		System.out.println((int) '杜');
		System.out.println((int) '咑');
		System.out.println((int) '咗');

		System.out.println((int) '-');
		System.out.println((int) ';');
		System.out.println(Integer.toHexString(59));// ;

		System.out.println(escapeJSON("杜有发\""));
		System.out.println(escapeSQL("杜有发'"));

		System.out.println(new Character((char) 0x1a));

		// 2012-10-10 by liusan.dyf
		String[] arr = splitEx("a\n;b;;c\n;\n\nd", "\n;");// splitEx("a,b,c,d|e|f|g", ",|");
		System.out.println(Json.toString(arr));

		// 2012-11-13 by liusan.dyf
		System.out.println(trim("aaaaalibab", 'b'));

		// 2013-05-02 by liusan.dyf
		System.out.println(signContent("m_evaluate", "freeproj"));
		System.out.println(getRawContent("m_evaluateec117424b5bcac95f37c27385802d995", "freeproj"));

		// 2013-06-04 by liusan.dyf
		System.out.println(getCharFrequence("hello world", "llox"));// {o=2, l=3, x=0}
		System.out.println(analyzeString("你好世界，hello world"));// {digit=0, zh=4, alpha=10}

		// 2014-09-03 by liusan.dyf
		System.out.println(stringToMap("a=1,b=2,c=3", ",", "=", null));// {b=2, c=3, a=1}
		System.out.println(stringToMap("a=", ",", "=", null));// {}
		System.out.println(stringToMap("a=1,b=,c=3", ",", "=", null));// {c=3, a=1}
	}
}
