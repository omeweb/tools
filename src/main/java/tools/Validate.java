package tools;

import java.util.Date;
import java.util.regex.Pattern;

public class Validate {
	// private final static Pattern INT_PATTERN = Pattern.compile("^[-]?[0-9]+$", 0);// int，如果是正数，前面不用加+
	// private final static Pattern DECIMAL_PATTERN = Pattern.compile("^[-]?[0-9]+[.]?[0-9]+$", 0);

	// /**
	// * 2012-10-22 fixed by liusan.dyf <br />
	// * from http://hi.baidu.com/redraiment/item/64865312e77cf48d89a95640
	// */
	// private final static Pattern DOUBLE_PATTERN = Pattern.compile(
	// "^[-+]?(\\d+(\\.\\d*)?|\\.\\d+)([eE]([-+]?([012]?\\d{1,2}|30[0-7])|-3([01]?[4-9]|[012]?[0-3])))?[dD]?$", 0);

	// private final static Pattern ZHCN_PATTERN = Pattern.compile("[\\u4E00-\\u9FA5]+", 0);
	// 目前在unicode标准中，汉字地charCode范围是[0x4E00,0x9FA5]

	private final static char DOT = '.';// 2014-11-27 by 六三

	private final static Pattern DIGIT_PATTERN = Pattern.compile("\\d{1,}", 0);

	private final static Pattern EMAIL_PATTERN = Pattern
			.compile("^[a-z0-9]([a-z0-9]*[-_.]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2,3})?$");

	private final static Pattern ASCII_ALPHA_NUMBER_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

	// private final static Pattern URL_PATTERN = Pattern.compile("");

	// private final static Pattern TIME_PATTERN = Pattern
	// .compile("^((([0-1]?[0-9])|(2[0-3])):([0-5]?[0-9])(:[0-5]?[0-9])?)$");
	private final static Pattern IP_PATTERN = Pattern
			.compile("^(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.)(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.){2}([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))$");

	// \d => \\d，\. => \\.

	/**
	 * 2015-3-9 16:33:29 by 六三
	 */
	private static final String[] PRIMITIVE_TYPES = { "java.lang.Character", "java.lang.Byte", "java.lang.Short",
			"java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Boolean",
			"java.lang.Void", "java.lang.String",// 2012-10-11
			"java.lang.Number"// 2012-10-11
	};

	/**
	 * and & checked == checked？ 2014-11-07 by 六三
	 * 
	 * @param base
	 * @param checked
	 * @return
	 */
	public static boolean and(long base, long checked) {
		return (base & checked) == checked;
	}

	public static boolean isInt(Object obj) {
		// if (obj != null) {
		// String str = obj.toString();
		// int len = str.length();
		// if (len > 0 && len <= 11 && INT_PATTERN.matcher(str).matches()) {
		// char[] chars = str.toCharArray();
		// char ch = chars[0];
		// if ((len < 10) || (len == 10 && ch == '1') || (len == 11 && ch == '-' && chars[1] == '1')// 负数
		// ) {
		// return true;
		// }
		// }
		// }
		// return false;

		// return isNumeric(obj);

		/**
		 * 2013-08-05修改为以下版本，这样判断123.2会返回false
		 */
		if (obj == null) {
			return false;
		}

		String str = Convert.toString(obj);
		int strLen = str.length();

		if (strLen == 0)// 2013-08-05，过滤空串
			return false;

		for (int i = 0; i < strLen; i++) {
			if (!isAsciiNumericChar(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 来自org.apache.commons.lang.StringUtils 2013-05-31 by liusan.dyf
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	@Deprecated
	public static boolean isIp(String value) {
		if (value != null) {
			return IP_PATTERN.matcher(value).matches();
		}
		return false;
	}

	/**
	 * 用的正则，性能慢。用isNumeric或者isAsciiNumeric代替。2013-07-25<br />
	 * obj的第一个字符可以为0
	 * 
	 * @param obj
	 * @return
	 */
	@Deprecated
	public static boolean isDigital(String obj) {
		if (obj != null) {
			return DIGIT_PATTERN.matcher(obj).matches();
		}
		return false;
	}

	public static boolean isAsciiNumericChar(char ch) {
		return ch >= '0' && ch <= '9';
	}

	/**
	 * 是否包含中文，只要包括即可 2012-11-21
	 * 
	 * @param v
	 * @return
	 */
	public static boolean containsChinese(String v) {
		if (v == null || v.length() == 0) {
			return false;
		}

		int len = v.length();
		for (int i = 0; i < len; i++) {
			if (isChineseChar(v.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * 是否包含数字 2012-11-21 by liusan.dyf
	 * 
	 * @param v
	 * @return
	 */
	public static boolean containsDigit(CharSequence v) {
		if (v == null || isEmpty(v))
			return false;

		int len = v.length();
		for (int i = 0; i < len; i++) {
			if (isAsciiNumericChar(v.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * 是否包括字母 2012-11-21 by liusan.dyf
	 * 
	 * @param v
	 * @return
	 */
	public static boolean containsAlpha(CharSequence v) {
		if (v == null || isEmpty(v))
			return false;

		int len = v.length();
		for (int i = 0; i < len; i++) {
			// 2013-04-02 by liusan.dyf
			// Java天生提供了对unicode的支持，因此在她眼里中文也是“letter”，Character.isLetter('中')==true

			// if (Character.isLetter(v.charAt(i)))
			if (isAsciiAlphaChar(v.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * 来自Apache Commons子项目中的lang库，CharUtils的isAsciiAlpha 2013-04-02 by liusan.dyf
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isAsciiAlphaChar(char ch) {
		return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
	}

	/**
	 * 得到字母和数字、汉字的混合次数 2014-06-28 by liusan.dyf
	 * 
	 * @param v
	 * @return
	 */
	public static int mixedTimes(String v) {
		if (v == null || v.length() == 0)
			return 0;

		int t = 0;
		int len = v.length();

		for (int i = 0; i < len; i++) {
			if (i == len - 1)
				break;

			char ch = v.charAt(i);
			char nextCH = v.charAt(i + 1);

			if (isAsciiAlphaChar(ch) && isAsciiAlphaChar(nextCH)) {
				// i不能加1，有可能i+1的位置和后面的不一样
				// 同是字母
			} else if (isAsciiNumericChar(ch) && isAsciiNumericChar(nextCH)) {
				// 同是数字
			} else if (isChineseChar(ch) && isChineseChar(nextCH)) {
				// 同是汉字 2014-10-21 by liusan.dyf
			} else { // 不一样的时候，索引往后移动一位，因为已经不一样了
				t++;
				i++;
			}
		}

		return t;
	}

	/**
	 * 是否仅仅是字母的组合 2013-07-25 by liusan.dyf
	 * 
	 * @param v
	 * @return
	 */
	public static boolean isJustAsciiAlphas(String v) {
		if (v == null || isEmpty(v))
			return false;

		int len = v.length();
		for (int i = 0; i < len; i++) {
			if (!isAsciiAlphaChar(v.charAt(i)))// 如果不是则退出
				return false;
		}
		return true;
	}

	public static boolean isJustAsciiAlphasOrNumbers(String v) {
		if (v == null || isEmpty(v))
			return false;

		int len = v.length();
		char ch;
		for (int i = 0; i < len; i++) {
			ch = v.charAt(i);
			if (isAsciiAlphaChar(ch) || isAsciiNumericChar(ch))// 字母或者是数字
				continue;
			else
				return false;
		}

		return true;
	}

	@Deprecated
	public static boolean isJustCharsOrNumbers(String str) {
		if (str != null) {
			return ASCII_ALPHA_NUMBER_PATTERN.matcher(str).matches();
		}
		return false;
	}

	// /**
	// * 2012-04-20 by liusan.dyf
	// *
	// * @param value
	// * @return
	// */
	// public static boolean isNumeric(Object value) {
	// // from
	// // http://code.google.com/p/jmxtrans/source/browse/trunk/src/com/googlecode/jmxtrans/util/JmxUtils.java?r=168
	// return (((value instanceof String) && isNumeric((String) value)) || (value instanceof Number)
	// || (value instanceof Integer) || (value instanceof Long) || (value instanceof Double) || (value instanceof
	// Float));
	// }

	/**
	 * 2012-11-19，v只要匹配pList中的任意一个即可
	 * 
	 * @param v
	 * @param pList
	 * @return
	 */
	public static boolean startsWithAny(String v, String... pList) {
		if (v == null || v.length() == 0)
			return false;

		for (String item : pList) {
			if (v.startsWith(item))
				return true;
		}

		return false;
	}

	public static boolean isAsciiAlphaUpperChar(char ch) {
		return ch >= 'A' && ch <= 'Z';
	}

	// public static boolean isDecimal(Object obj) {
	// if (obj != null) {
	// return DECIMAL_PATTERN.matcher(obj.toString()).matches();
	// }
	// return false;
	// }

	public static boolean isChineseOrAlpha(String v) {
		if (v == null || v.length() == 0) {
			return false;
		}

		int len = v.length();
		for (int i = 0; i < len; i++) {
			char ch = v.charAt(i);
			if (!isAsciiAlphaChar(ch) && !isChineseChar(ch)) {
				return false;
			}
		}
		return true;
	}

	// /**
	// * 判断仅仅是否为汉字，其中中文状态下的标点符号，也返回false
	// *
	// * @param str
	// * @return
	// */
	// public static boolean isZHCN(String str) {
	// // 目前是测试全部为中文，场合：中国人姓名判断
	// if (str == null)
	// return false;
	//
	// return ZHCN_PATTERN.matcher(str).matches();
	// }

	/**
	 * 只判断中文，中文标点符号也返回false
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChineseChar(char c) {
		// 一些说明：http://lhp--2006.iteye.com/blog/1300002
		// CJK的意思是“Chinese，Japanese，Korea”的简写 ，实际上就是指中日韩三国的象形文字的Unicode编码
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS// CJK 统一表意符号
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
		// || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION// 判断中文的“号
		// || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION// CJK 符号和标点
		// || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS// 判断中文的，号
		) {
			return true;
		}
		return false;

		// return isZHCN(c + "");
	}

	public static boolean isChinese(String v) {
		if (v == null)
			return false;

		int len = v.length();
		for (int i = 0; i < len; i++) {

			char c = v.charAt(i);

			if (!isChineseChar(c))
				return false;
		}

		return true;
	}

	public static boolean checkStringLength(String str, int min, int max) {
		// 2011-11-05 fixed when str is null
		if (min == 0) {
			return true;
		} else if (str == null)
			return false;

		int l = str.getBytes().length;// 一个汉字3个字节，不知道为什么
		// str.getBytes(Charset.forName("utf-8")).length;//一个汉字3个字节，不知道为什么
		// str.toCharArray().length;//一个汉字一个字节

		// System.out.println(l);
		return (l >= min && l <= max);
	}

	/**
	 * 用非正则的方式判断是否为ip地址 2013-06-04 by liusan.dyf
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isIPAddress(String value) {
		if (isNullOrEmpty(value))
			return false;

		String[] arr = StringUtil.split(value, ".");
		int len = arr.length;

		if (len != 4) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			int v = Convert.toInt(arr[i], -1);
			if ((v < 0) || (v > 255)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmail(String str) {
		if (str != null) {
			return EMAIL_PATTERN.matcher(str).matches();
		}
		return false;
	}

	public static boolean isTime(String str) {
		/*
		 * if (str != null) { return timePattern.matcher(str).matches(); } return false;
		 */

		// 2012-11-19 增加HH:mm:ss.ms的判断 by liusan.dyf
		return isDateTime(str, "HH:mm:ss", false) || isDateTime(str, "HH:mm:ss.ms", false);
	}

	// public static boolean isDate(String str) {
	// return isDateTime(str, "yyyy-MM-dd", false);
	// }
	//
	// public static boolean isDateTime(String str) {
	// return isDateTime(str, "yyyy-MM-dd HH:mm:ss", false);
	// }

	/**
	 * 2012-12-12 by liusan.dyf
	 * 
	 * @param a timestamp、13位的毫秒、date类型、yyyy-MM-dd均可
	 * @param b 【yyyy-MM-dd，yyyy-MM-dd】的格式
	 * @return
	 */
	public static boolean dateTimeBetween(Object a, Object b) {
		if (a == null || b == null)
			return false;

		// 检查右值
		String[] arr = StringUtil.splitEx(b.toString(), "\n,，");
		int len = arr.length;

		if (len == 0)
			return false;

		long v = 0;
		if (a instanceof String && ((String) a).indexOf('-') > -1) {
			// 字符串格式的时间 yyyy-MM-dd
			Date d = Convert.toDateTime(a);
			if (d == null)
				return false;
			// System.out.println(d);//[2012-12-12a]也可以转换为时间
			v = d.getTime() / 1000;
		} else {
			v = Convert.toLong(a, 0);

			if (v == 0)
				return false;

			if (String.valueOf(v).length() == 13)// 毫秒格式的时间
				v = v / 1000;
		}

		// 得到右值的long值，右边的时间格式比较简单，一律是【yyyy-MM-dd】格式
		long vLeft = 0;
		if (len > 0) {// 如果只有一个时间，则默认结束时间就是现在
			Date dLeft = Convert.toDateTime(arr[0]);
			if (dLeft == null)
				return false;

			vLeft = dLeft.getTime() / 1000;

			if (len == 1)
				return vLeft <= v;
		}

		// 右边有两个值
		long vRight = 0;
		Date dRight = Convert.toDateTime(arr[1]);
		if (dRight == null)
			return false;

		vRight = dRight.getTime() / 1000;

		// 左右2个值矫正
		if (vRight < vLeft) {
			long max = vLeft;
			vLeft = vRight;
			vRight = max;
		}

		return vLeft <= v && v <= vRight;
	}

	/**
	 * 2014-11-27 by 六三，ox001、54D、53L都不算数字，不包括小数点
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumericEx(String str) {
		return isNumericEx(str, false);
	}

	/**
	 * ox001、54D、53L都不算数字，但可配置小数点是否算 2014-11-27 by 六三
	 * 
	 * @param str
	 * @param dot
	 * @return
	 */
	public static boolean isNumericEx(String str, boolean dot) {
		if (str == null)
			return false;

		int len = str.length();
		if (len == 0)
			return false;

		int dotCount = 0;// 小数点的个数
		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);
			if (dot && ch == DOT) {
				dotCount++;
				continue;
			}

			if (!isAsciiNumericChar(ch)) {
				return false;
			}
		}

		if (dot) {
			if (dotCount > 1)// 小数点太多了
				return false;

			if (DOT == str.charAt(len - 1))// 最后一个符号是小数点，也是不合法的 2014-11-28 by 六三
				return false;
		}

		return true;
	}

	/**
	 * 来自org.apache.commons.lang.StringUtils 2013-05-21 by liusan.dyf
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str == null || str.length() == 0) {
			return false;
		}

		char[] chars = str.toCharArray();
		int sz = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;
		// deal with any possible sign up front
		int start = (chars[0] == '-') ? 1 : 0;
		if (sz > start + 1) {
			if (chars[start] == '0' && chars[start + 1] == 'x') {
				int i = start + 2;
				if (i == sz) {
					return false; // str == "0x"
				}
				// checking hex (it can't be anything else)
				for (; i < chars.length; i++) {
					if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f')
							&& (chars[i] < 'A' || chars[i] > 'F')) {
						return false;
					}
				}
				return true;
			}
		}
		sz--; // don't want to loop to the last char, check it afterwords
				// for type qualifiers
		int i = start;
		// loop to the next to last char or to the last char if we need another digit to
		// make a valid number (e.g. chars[0..5] = "1234E")
		while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				foundDigit = true;
				allowSigns = false;

			} else if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					// two decimal points or dec in exponent
					return false;
				}
				hasDecPoint = true;
			} else if (chars[i] == 'e' || chars[i] == 'E') {
				// we've already taken care of hex.
				if (hasExp) {
					// two E's
					return false;
				}
				if (!foundDigit) {
					return false;
				}
				hasExp = true;
				allowSigns = true;
			} else if (chars[i] == '+' || chars[i] == '-') {
				if (!allowSigns) {
					return false;
				}
				allowSigns = false;
				foundDigit = false; // we need a digit after the E
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				// no type qualifier, OK
				return true;
			}
			if (chars[i] == 'e' || chars[i] == 'E') {
				// can't have an E at the last byte
				return false;
			}
			if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					// two decimal points or dec in exponent
					return false;
				}
				// single trailing decimal point after non-exponent is ok
				return foundDigit;
			}
			if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
				return foundDigit;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				// not allowing L with an exponent
				return foundDigit && !hasExp;
			}
			// last character is illegal
			return false;
		}
		// allowSigns is true iff the val ends in 'E'
		// found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
		return !allowSigns && foundDigit;
	}

	public static boolean isNumeric(Object obj) {
		if (obj == null)
			return false;
		return isNumeric(obj.toString());

		// if (obj != null) {
		// // 目前不解决 【.5】的问题，返回false
		// return DOUBLE_PATTERN.matcher(obj.toString()).matches();
		// }
		// return false;
	}

	public static boolean isNumericArray(String str, String split) {
		// 注意split里的特殊字符的转义
		if (str == null)
			return false;
		return isNumericArray(str.split(split));
	}

	public static boolean isNumericArray(String[] arr) {
		if (arr == null || arr.length == 0)
			return false;
		for (String item : arr) {
			if (!isNumeric(item))
				return false;
		}
		return true;
	}

	public static boolean isDateTime(String value, String pattern, boolean strict) {// 来自commons-validator
		// // strict表示严格模式，即value的长度要和pattern的长度一致
		// if (value == null || pattern == null || pattern.length() <= 0) {
		// return false;
		// }
		//
		// SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		// formatter.setLenient(false);
		//
		// try {
		// formatter.parse(value);
		// } catch (ParseException e) {
		// return false;
		// }
		//
		// if (strict && (pattern.length() != value.length())) {
		// return false;
		// }
		//
		// return true;

		return tools.DateTime.parse(value, pattern) != null;
	}

	public static boolean isInString(String source, String test, String split) {
		if (test == null || source == null)
			return false;

		String theTest = split + test + split;
		String theSource = split + source + split;

		return theSource.contains(theTest);
	}

	/**
	 * @param v
	 * @return
	 */
	public static boolean isEmpty(CharSequence v) {
		return v == null || v.length() == 0;
	}

	/**
	 * 对object也可以判断 2013-06-04 by liusan.dyf
	 * 
	 * @param v
	 * @return
	 */
	public static boolean isEmpty(Object v) {
		if (v instanceof String)
			return isEmpty((String) v);

		return v != null;
	}

	/**
	 * <p>
	 * Checks if the CharSequence contains only uppercase characters.
	 * </p>
	 * <p>
	 * {@code null} will return {@code false}. An empty String (length()=0) will return {@code false}.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isAllUpperCase(null)   = false
	 * StringUtils.isAllUpperCase("")     = false
	 * StringUtils.isAllUpperCase("  ")   = false
	 * StringUtils.isAllUpperCase("ABC")  = true
	 * StringUtils.isAllUpperCase("aBC") = false
	 * </pre>
	 * 
	 * @param v the CharSequence to check, may be null
	 * @return {@code true} if only contains uppercase characters, and is non-null
	 */
	public static boolean isAllUpperCase(CharSequence v) {
		if (v == null || isEmpty(v)) {
			return false;
		}
		int len = v.length();
		for (int i = 0; i < len; i++) {
			if (isAsciiAlphaUpperChar(v.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNullOrEmpty(String value) {
		return value == null || value.length() == 0;
	}

	/**
	 * 2012-04-24 by liusan.dyf，支持mysql、mssql等数据库类型<br />
	 * 该方法把boolean和bit也当作数字来处理了
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isNumericSqlType(String type) {
		if (isNullOrEmpty(type))
			return false;
		type = type.toLowerCase();// type可能为int(11),decimal(12,5)这样的格式
		if (type.indexOf("int") > -1 // int,tinyint,smallint,mudiumint,integer,bigint
				|| type.indexOf("float") > -1 || type.indexOf("unsigned") > -1// unsigned
				|| type.indexOf("double") > -1// double
				|| type.indexOf("real") > -1// real
				|| type.indexOf("short") > -1// short
				|| type.indexOf("numeric") > -1// numeric
				|| type.indexOf("bit") > -1 // bit
				|| type.indexOf("boolean") > -1 // bit
		)
			return true;
		return false;
	}

	/**
	 * 2012-10-11 by liusan.dyf，如果是基本类型的包装类，这里也算做是基本类型
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isPrimitive(Class<?> clz) {
		if (clz == null)
			return false;

		// boolean, byte, char, short, int, long, float, double 和 void
		String cls = clz.getName();// java.lang.Integer

		for (String item : PRIMITIVE_TYPES) {
			if (item.equals(cls))
				return true;
		}

		return clz.isPrimitive();
	}

	public static void main(String[] args) {
		Object a = 1;
		System.out.println(a.getClass().isPrimitive());// java.lang.Integer

		String b = "b";
		a = b;
		System.out.println(a.getClass().getName());

		//
		System.out.println(isNumericEx("02"));
		System.out.println(isNumericEx("42d"));
		System.out.println(isNumericEx("42.3"));
		System.out.println(Long.valueOf("02"));
		System.out.println(isNumericEx("02.4", true));
		// System.out.println(Long.valueOf("10.2"));
	}
}
