package tools;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * http://dev.mysql.com/doc/refman/5.5/en/functions.html
 * 
 * @author liusan.dyf
 */
public class MySqlFunction {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_MICROSECOND = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";
	public static final Locale LOCALE = Locale.getDefault();

	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	// ----------------------日期函数开始

	/**
	 * 虽然中途 sleep 3 秒,但 now() 函数两次的时间值是相同的 sysdate() 函数两次得到的时间值相差 3 秒。MySQL Manual 中是这样描述 sysdate() 的Return the time at
	 * which the function executes<br />
	 * 返回值带时间
	 * 
	 * @return
	 */
	public static String now() {
		// sysdate() | sleep(3) | sysdate()
		// 2008-08-08 22:28:41 | 0 | 2008-08-08 22:28:44
		return toString(new Date());
	}

	public static String current_timestamp() {
		return now();
	}

	public static String localtime() {
		return now();
	}

	public static String localtimestamp() {
		return now();
	}

	/**
	 * mysql里返回的是日期+时间
	 * 
	 * @return
	 */
	public static String sysdate() {
		return now();
	}

	public static String curdate() {
		return dateToString(new Date(), DATE_FORMAT_SHORT);// toString(getDate(new Date()));
	}

	public static Date getDate(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		// cal.set(Calendar.HOUR, 1);//无效，显示13点，要用HOUR_OF_DAY
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * 2013-06-20 00:00:00 只有日期
	 * 
	 * @return
	 */
	public static String current_date() {
		return curdate();
	}

	public static String date(Object value) {
		Date d = toDate(value);
		if (d == null)
			return null;

		return dateToString(d, DATE_FORMAT_SHORT);// toString(getDate(d));
	}

	/**
	 * CURTIME() = 11:45:16
	 * 
	 * @return
	 */
	public static String curtime() {
		return time(null);
	}

	// public static String date_format(Object value, String format) {
	// //
	// http://dev.mysql.com/doc/refman/5.5/en/date-and-time-functions.html#function_date-format
	// return null;
	// }

	/**
	 * TIME('2012-06-28 11:46:00') = 11:46:00
	 * 
	 * @param value
	 * @return
	 */
	public static String time(Object value) {
		Date d = toDate(value);
		if (d == null)
			return null;

		return dateToString(d, "HH:mm:ss");
	}

	public static int year(Object value) {
		Date d = toDate(value);
		if (d == null)
			return 0;

		return getDateField(d, Calendar.YEAR);
	}

	public static int quarter(Object value) {
		Date d = toDate(value);
		if (d == null)
			return 0;

		return (getDateField(d, Calendar.MONTH) / 3) + 1;
	}

	public static int month(Object value) {
		Date d = toDate(value);
		if (d == null)
			return 0;

		return getDateField(d, Calendar.MONTH);
	}

	/**
	 * select week(@dt,3);不支持！ mySQL week() 函数可以有两个参数具体可看手册。 weekofyear() 和 week() 一样都是计算“某天”是位于一年中的第几周。
	 * weekofyear(@dt) 等价于 week(@dt,3)
	 * 
	 * @param value
	 * @return
	 */
	public static int week(Object value) {
		Date d = toDate(value);
		if (d == null)
			return 0;

		return getDateField(d, Calendar.WEEK_OF_YEAR);
	}

	public static int weekofyear(Object value) {
		return week(value);
	}

	/**
	 * select yearweek('2008-08-08'); -- 200831
	 * 
	 * @param value
	 * @return
	 */
	public static String yearweek(Object value) {
		return dateToString(toDate(value), "yyyyMM");
		// return year(value) + "" + week(value);
	}

	public static int day(Object value) {
		Date d = toDate(value);
		if (d == null)
			return 0;

		return getDateField(d, Calendar.DAY_OF_MONTH);
	}

	public static int dayofweek(Object value) {
		Date d = toDate(value);
		if (d == null)
			return 0;

		return getDateField(d, Calendar.DAY_OF_WEEK);
	}

	/**
	 * DAYOFYEAR('2010-10-1') = 274
	 * 
	 * @param value
	 * @return
	 */
	public static int dayofyear(Object value) {
		Date d = toDate(value);
		if (d == null)
			return 0;

		return getDateField(d, Calendar.DAY_OF_YEAR);
	}

	/**
	 * DAYOFMONTH('2010-10-1') = 1
	 * 
	 * @param value
	 * @return
	 */
	public static int dayofmonth(Object value) {
		return day(value);
	}

	/**
	 * HOUR('10:20:12') = 10 <br />
	 * HOUR('2012-06-28 10:20:12') = 10;
	 * 
	 * @param value
	 * @return
	 */
	public static int hour(Object value) {
		int v = getHour(value);
		if (v != -1)
			return v;

		// // 转换为时间，继续判断
		// if (value != null && value.toString().indexOf(' ') > -1) {
		// Date d = toDate(value);
		// if (d == null)
		// return 0;
		//
		// return getDateField(d, Calendar.HOUR_OF_DAY);
		// }

		return 0;
	}

	/**
	 * MINUTE('11:46:00') = 46<br />
	 * MINUTE('2012-06-28 11:46:00') = 16
	 * 
	 * @param value
	 * @return
	 */
	public static int minute(Object value) {
		int v = getTimeField(value, Calendar.MINUTE);
		if (v != -1)
			return v;

		return 0;
	}

	public static int second(Object value) {
		int v = getTimeField(value, Calendar.SECOND);
		if (v != -1)
			return v;

		return 0;
	}

	public static int microsecond(Object value) {
		int v = getTimeField(value, Calendar.MILLISECOND);
		if (v != -1)
			return v;

		return 0;
	}

	public static int getDateField(Date d, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);

		return cal.get(field);
	}

	public static String toString(Object value) {
		if (value instanceof Date) {
			return dateToString((Date) value, DATE_FORMAT);
		}

		if (value == null)
			return null;

		return value.toString();
	}

	public static String dateToString(Date d, String pattern) {
		if (d == null)
			return null;

		/**
		 * 旧方案 2013-01-09 by liusan.dyf
		 */
		// SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		// return sdf.format(d);

		/**
		 * new 2013-01-09
		 */
		return tools.DateTime.format(d, pattern);
		// from http://www.rgagnon.com/javadetails/java-0106.html
	}

	public static String dayname(Object value) {
		Date d = toDate(value);
		if (d == null)
			return "";

		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, LOCALE);
	}

	public static String monthname(Object value) {
		Date d = toDate(value);
		if (d == null)
			return "";

		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, LOCALE);
	}

	public static String last_day(Object value) {
		Date d = toDate(value);
		if (d == null)
			return null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDate(d));
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);
		return dateToString(calendar.getTime(), DATE_FORMAT_SHORT);
	}

	/**
	 * 和mysql的date_add稍有不同
	 * 
	 * @param value
	 * @param field
	 * @param x
	 * @return
	 */
	public static String date_add(Object value, int field, int x) {
		Date d = toDate(value);
		if (d == null)
			return null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);

		calendar.add(field, x);
		return dateToString(calendar.getTime(), DATE_FORMAT);
	}

	/**
	 * 返回2个日期相差多少天。DATEDIFF('2007-12-31 23:59:59','2007-12-30')=1;<br />
	 * DATEDIFF('2010-11-30 23:59:59','2010-12-31')=-31
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int datediff(Object v1, Object v2) {
		return second_diff(v1, v2) / 60 / 60 / 24;
	}

	/**
	 * 得到2个日期相差的秒，可能为负数，该函数不属于mysql的函数。2012-12-03 by liusan.dyf
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int second_diff(Object v1, Object v2) {
		if (v1 == null || v2 == null)
			return 0;

		// http://stackoverflow.com/questions/4759248/difference-between-two-dates-in-mysql
		// http://stackoverflow.com/questions/2438828/mysql-datetime-to-seconds
		Date d1 = toDate(v1);
		if (d1 == null)
			return 0;

		Date d2 = toDate(v2);
		if (d2 == null)
			return 0;

		// Get msec from each, and subtract.
		long diff = d1.getTime() - d2.getTime();
		// System.out.println(diff);

		return (int) (diff / 1000);
	}

	/**
	 * 得到2个日期相差的秒，始终为正数，返回差值的绝对值。该函数不属于mysql的函数。2012-12-03 by liusan.dyf
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int abs_second_diff(Object v1, Object v2) {
		int n = second_diff(v1, v2);
		if (n < 0)
			return 0 - n;
		return n;
	}

	/**
	 * SELECT TIME_TO_SEC('22:23:00'); = 80580
	 * 
	 * @param time
	 * @return
	 */
	public static int time_to_second(String time) {
		// 00:39:38
		if (time == null || time.length() == 0)
			return 0;

		// String[] arr = time.split(":");
		// if (arr.length == 3) {
		// return strToInt(arr[0], 0) * 3600 + strToInt(arr[1], 0) * 60
		// + strToInt(arr[2], 0) * 1;
		// }

		StringTokenizer st = new StringTokenizer(time, ":", false);
		int count = 0;
		int[] arr = new int[3];

		while (st.hasMoreTokens()) {
			if (count < 3)
				arr[count++] = toInt(st.nextToken(), 0);
		}

		return arr[0] * 3600 + arr[1] * 60 + arr[2];
	}

	/**
	 * 时间戳
	 */
	public static long unix_timestamp(Object value) {
		Date d = toDate(value);
		if (d == null)
			return 0;

		return d.getTime() / 1000;
	}

	public static String from_unixtime(long v) {
		Date d = new Date();
		d.setTime(v * 1000);

		return dateToString(d, DATE_FORMAT);
	}

	/**
	 * select makedate(2001,31); -- '2001-01-31'<br />
	 * select makedate(2001,32); -- '2001-02-01
	 * 
	 * @param year
	 * @param dayofyear
	 * @return
	 */
	public static String makdedate(int year, int dayofyear) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_YEAR, dayofyear);

		return dateToString(calendar.getTime(), DATE_FORMAT_SHORT);
	}

	public static String maketime(int hour, int minute, int second) {
		return hour + ":" + minute + ":" + second;
	}

	// ----------------------日期函数结束

	// ----------------------string函数开始

	/**
	 * 回值为字符串str 的最左字符的数值。假如str为空字符串则返回值为 0
	 * 
	 * @param value
	 * @return
	 */
	public static int ascii(Object value) {
		if (value == null)
			return 0;

		return value.toString().toCharArray()[0];
	}

	public static String bin(long value) {
		return Long.toBinaryString(value);
	}

	public static int bit_length(Object value) {
		if (value == null)
			return 0;

		try {
			return value.toString().getBytes("gb2312").length;
		} catch (UnsupportedEncodingException e) {
			return 0;
		}
	}

	public static int length(Object value) {
		return bit_length(value);
	}

	public static String char_(int... args) {
		char[] chars = new char[args.length];

		int i = 0;
		for (int item : args) {
			chars[i++] = (char) item;
		}

		return new String(chars);
	}

	public static int character_length(Object value) {
		return char_length(value);
	}

	public static int char_length(Object value) {
		if (value == null)
			return 0;
		return value.toString().length();
	}

	/**
	 * 返回结果为连接参数产生的字符串。如有任何一个参数为NULL，则返回值为 NULL
	 * 
	 * @param args
	 * @return
	 */
	public static String concat(String... args) {
		StringBuilder sb = new StringBuilder();

		for (String item : args) {
			if (item == null)
				return null;
			sb.append(item);
		}
		return sb.toString();
	}

	/**
	 * 代表 CONCAT With Separator 是CONCAT()的特殊形式。 第一个参数是其它参数的分隔符。分隔符的位置放在要连接的两个字符串之间。分隔符可以是一个字符串也可以是其它参数
	 * 
	 * @param separator
	 * @param args
	 * @return
	 */
	public static String concat_ws(String separator, String... args) {
		if (separator == null)
			return null;

		// SELECT CONCAT_WS(',','First name',NULL,'Last Name');-> 'First
		// name,Last Name

		StringBuilder sb = new StringBuilder();

		int i = 0;
		int len = args.length;

		for (String item : args) {
			sb.append(item == null ? "NULL" : item);

			i++;

			if (i < len)
				sb.append(separator);
		}

		return sb.toString();
	}

	/**
	 * 若N = 1则返回值为 str1 若N = 2则返回值为 str2 以此类推。若N 小于1或大于参数的数目则返回值为 NULL
	 * 
	 * @param indexBased1
	 * @param args
	 * @return
	 */
	public static String elt(int indexBased1, String... args) {
		if (indexBased1 == 0)
			indexBased1 = 1;

		if (args.length >= indexBased1)
			return args[indexBased1 - 1];

		return null;
	}

	/**
	 * 返回值为str1, str2, str3,……列表中的str 指数。在找不到str 的情况下返回值为 0 ; <br />
	 * 如果str 为NULL则返回值为0 原因是NULL不能同任何值进行同等比较
	 * 
	 * @param value
	 * @param args
	 * @return
	 */
	public static int fielt(String value, String... args) {
		if (value == null)
			return 0;

		int i = 0;
		for (String item : args) {
			if (value.equals(item))
				return i + 1;

			i++;
		}

		return 0;
	}

	/**
	 * 如果str不在strlist 或strlist 为空字符串则返回值为 0 。如任意一个参数为NULL则返回值为 NULL。 这个函数在第一个参数包含一个逗号(‘,’)时将无法正常运行。 <br />
	 * 2012-12-05，对换行的兼容
	 * 
	 * @param value
	 * @param list
	 * @return
	 */
	public static int find_in_set(String value, String list) {
		if (value == null)
			return 0;

		StringTokenizer st = new StringTokenizer(list, ",\n", false);
		int i = 0;
		while (st.hasMoreTokens()) {
			if (value.equals(st.nextToken()))
				return i + 1;

			i++;
		}

		return 0;
	}

	public static String hex(Object value) {
		if (value == null)
			return null;
		// TODO 支持还比较弱
		// http://dev.mysql.com/doc/refman/5.5/en/string-functions.html#function_hex
		return Long.toHexString(Long.valueOf(value.toString()));
	}

	public static String unhex(Object value) {
		if (value == null)
			return null;
		// TODO
		return null;
	}

	/**
	 * SELECT INSTR('foobarbar', 'bar');-> 4
	 * 
	 * @param str
	 * @param substr
	 * @return
	 */
	public static int instr(Object str, Object substr) {
		if (str == null || substr == null)
			return 0;

		int i = str.toString().indexOf(substr.toString());

		return i + 1;
	}

	public static int locate(Object str, Object substr, int... p) {
		if (p.length == 0)
			return instr(str, substr);

		//
		if (str == null || substr == null)
			return 0;

		int i = str.toString().indexOf(substr.toString(), p[0]);

		return i + 1;
	}

	public static String lcase(Object value) {
		if (value == null)
			return null;
		return value.toString().toLowerCase();
	}

	public static String lower(Object value) {
		return lcase(value);
	}

	public static String upper(Object value) {
		if (value == null)
			return null;
		return value.toString().toUpperCase();
	}

	/**
	 * 返回字符串 str, 其左边由字符串padstr 填补到len 字符长度。假如str 的长度大于len, 则返回值被缩短至 len 字符
	 * 
	 * @param value
	 * @param len
	 * @param padstr
	 * @return
	 */
	public static String lpad(Object value, int len, String padstr) {
		return pad(value, len, padstr, LEFT);
	}

	public static String rpad(Object value, int len, String padstr) {
		return pad(value, len, padstr, RIGHT);
	}

	/**
	 * @param value
	 * @param len
	 * @param padstr
	 * @return
	 */
	public static String pad(Object value, int len, String padstr, int direction) {
		if (value == null)
			return null;

		if (len < 0)
			return null;

		String str = value.toString();
		int sourceLen = str.length();
		int paddingLen = padstr.length();

		if (len > sourceLen) {
			StringBuilder sb = new StringBuilder();
			// for (int i = 0; i < len - slen; i++)
			// sb.append(padstr);

			if (direction == LEFT) {
				// sourceLen
				while (len - sb.length() - sourceLen > paddingLen) {// 最后还要append原始串，所以要减去sourceLen
					sb.append(padstr);
				}

				// 不足一个plen的时候
				if (len - sb.length() - sourceLen > 0)
					sb.append(padstr.substring(0, len - sb.length() - sourceLen));

				sb.append(str);// append原始串
			} else if (direction == RIGHT) {
				sb.append(str);// append原始串

				while (len - sb.length() > paddingLen) {// 当还有padstr的空间时，一直追加
					sb.append(padstr);
				}

				// 不足一个plen的时候
				if (len - sb.length() > 0)
					sb.append(padstr.substring(0, len - sb.length()));
			}

			return sb.toString();
		} else {// len长度过段，小于原串
			if (direction == LEFT)
				return str.substring(0, len);
			else
				return str.substring(sourceLen - len);
		}
	}

	public static String ltrim(Object value) {
		if (value == null)
			return null;

		return value.toString().trim();
	}

	public static String repeat(Object value, int count) {
		if (value == null)
			return null;

		if (count <= 0)
			return null;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++)
			sb.append(value);

		return sb.toString();
	}

	public static String replace(Object value, String from, String to) {
		if (value == null || from == null || to == null)
			return null;

		// TODO 改进性能
		return value.toString().replaceAll(from, to);
	}

	public static String reverse(Object value) {
		if (value == null)
			return null;

		return new StringBuffer(value.toString()).reverse().toString();
	}

	public static String right(Object value, int len) {
		if (value == null)
			return null;

		String str = value.toString();
		int sourceLen = str.length();

		if (sourceLen <= len)// len过大
			return str;

		return str.substring(sourceLen - len, sourceLen);
	}

	public static String space(int n) {
		return repeat(" ", n);
	}

	/**
	 * pos从1开始计算的。带有len参数的格式从字符串str返回一个长度同len字符相同的子字符串起始于位置 pos。也可能对pos使用一个负值。假若这样则子字符串的位置起始于字符串结尾的pos 字符而不是字符串的开头位置。
	 * 
	 * @param value
	 * @param pos
	 * @param len
	 * @return
	 */
	public static String substring(Object value, int pos, int len) {
		if (value == null)
			return null;

		if (len <= 0)
			return null;

		if (pos > 0)
			return value.toString().substring(pos - 1, pos + len - 1);
		else {
			String str = value.toString();
			int sourceLen = str.length();

			pos = sourceLen + pos + 1;// pos为负数
			return value.toString().substring(pos - 1, pos + len - 1);
		}
	}

	public static String substr(Object value, int pos, int len) {
		return substring(value, pos, len);
	}

	public static int like(final Object str, final Object expr) {
		return stringLike(str, expr) ? 1 : 0;
	}

	public static int not_like(final Object str, final Object expr) {
		return stringLike(str, expr) ? 0 : 1;
	}

	public static boolean stringLike(final Object str, final Object expr) {
		// from
		// http://stackoverflow.com/questions/898405/how-to-implement-a-sql-like-like-operator-in-java
		if (str == null || expr == null)
			return false;

		String regex = quoteMeta(expr.toString());
		regex = regex.replace("_", ".").replace("%", ".*?");
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		return p.matcher(str.toString()).matches();
	}

	public static String quoteMeta(String s) {
		if (s == null) {
			throw new IllegalArgumentException("String cannot be null");
		}

		int len = s.length();
		if (len == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder(len * 2);
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if ("[](){}.*+?$^|#\\".indexOf(c) != -1) {
				sb.append("\\");
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static int strcmp(Object value1, Object value2) {
		if (value1 == null || value2 == null)
			return 0;

		return value1.toString().compareTo(value2.toString());
	}

	// ----------------------string函数结束

	// ----------------------math函数开始

	public static long abs(Object value) {
		return Math.abs(toLong(value, 0));
	}

	public static double sign(Object value) {
		return Math.sin(toDouble(value, 0));
	}

	public static long mod(Object value, int base) {
		if (base == 0)
			return 0;

		long v = toLong(value, 0);

		return v - (v / base) * base;
	}

	/**
	 * 返回不大于X的最大整数值。
	 * 
	 * @param value
	 * @return
	 */
	public static double floor(Object value) {
		return Math.floor(toDouble(value, 0));
	}

	/**
	 * 返回不小于X的最小整数值。
	 * 
	 * @param value
	 * @return
	 */
	public static double ceiling(Object value) {
		return Math.ceil(toDouble(value, 0));
	}

	/**
	 * 返回参数X的四舍五入的一个整数
	 * 
	 * @param value
	 * @return
	 */
	public static double round(Object value) {
		return Math.round(toDouble(value, 0));
	}

	/**
	 * 返回值e自然对数的底的X次方。
	 * 
	 * @param value
	 * @return
	 */
	public static double exp(Object value) {
		return Math.exp(toDouble(value, 0));
	}

	/**
	 * 返回X的自然对数。
	 * 
	 * @param value
	 * @return
	 */
	public static double log(Object value) {
		return Math.log(toDouble(value, 0));
	}

	public static double log10(Object value) {
		return Math.log10(toDouble(value, 0));
	}

	public static double pow(Object x, Object y) {
		return Math.pow(toDouble(x, 1), toDouble(y, 0));
	}

	public static double power(Object x, Object y) {
		return pow(x, y);
	}

	public static double sqrt(Object value) {
		double v = toDouble(value, 0);
		if (v <= 0)
			return 0;
		return Math.sqrt(v);
	}

	public static double pi() {
		return Math.PI;
	}

	public static double sin(Object value) {
		return Math.sin(toDouble(value, 0));
	}

	public static double tan(Object value) {
		return Math.tan(toDouble(value, 0));
	}

	public static double acos(Object value) {
		return Math.acos(toDouble(value, 0));
	}

	public static double asin(Object value) {
		return Math.asin(toDouble(value, 0));
	}

	public static double atan(Object value) {
		return Math.atan(toDouble(value, 0));
	}

	public static double cot(Object value) {
		return Math.cosh(toDouble(value, 0));
	}

	public static double rand() {
		return Math.random();
	}

	public static double degrees(Object value) {
		return Math.toDegrees(toDouble(value, 0));
	}

	/**
	 * mysql:TRUNCATE(1.235,2)=1.23，而该函数四舍五入，为1.24
	 * 
	 * @param d
	 * @param decimalPlace
	 * @return
	 */
	public static double truncate_(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * TRUNCATE(1.235,2)=1.23，不四舍五入
	 * 
	 * @param value
	 * @param places
	 * @return
	 */
	public static double truncate(double value, int places) {
		double multiplier = Math.pow(10, places);
		return Math.floor(multiplier * value) / multiplier;
	}

	public static double min(double... numbers) {
		double minValue = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] < minValue) {
				minValue = numbers[i];
			}
		}
		return minValue;
	}

	public static double least(double... numbers) {
		return min(numbers);
	}

	public static double max(double... numbers) {
		double maxValue = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] > maxValue) {
				maxValue = numbers[i];
			}
		}
		return maxValue;

		// Collections.max(numbers);
	}

	public static double greatest(double... numbers) {
		return max(numbers);
	}

	// ----------------------math函数结束

	// // Using Apache Commons Codec:
	// public static String MySQLPassword(String plainText) throws
	// UnsupportedEncodingException {
	// byte[] utf8 = plainText.getBytes("UTF-8");
	// return "*" + DigestUtils.shaHex(DigestUtils.sha(utf8)).toUpperCase();
	// }

	public static int sleep(int seconds) throws InterruptedException {
		Thread.sleep(seconds * 1000);
		return 0;
	}

	public static int toInt(Object value, int def) {
		if (value == null)
			return def;

		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static long toLong(Object value, long def) {
		if (value == null)
			return def;
		try {
			return Long.parseLong(value.toString());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static double toDouble(Object value, double def) {
		if (value == null)
			return def;
		try {
			return Double.parseDouble(value.toString());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * 验证输入是否为一个合法的时间，格式：HH:mm:ss，【10:20:12.123】
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isTime(Object value) {
		return getHour(value) != -1;
	}

	/**
	 * -1表示不是个时间
	 * 
	 * @param value
	 * @return
	 */
	public static int getHour(Object value) {
		return getTimeField(value, Calendar.HOUR_OF_DAY);
	}

	/**
	 * -1表示不是个时间，value可能是个date，也可以是【10:12:13.456】
	 * 
	 * @param value
	 * @return
	 */
	public static int getTimeField(Object value, int field) {
		if (value == null)
			return -1;

		// 日期
		if (value instanceof Date || value.toString().indexOf(' ') > -1) {
			Date d = toDate(value);
			return getDateField(d, field);
		}

		// 时间
		StringTokenizer st = new StringTokenizer(value.toString(), ":", false);

		int len = 3;
		String[] arr = new String[len];

		int i = 0;
		while (st.hasMoreTokens()) {
			if (i < len)
				arr[i] = st.nextToken();
			i++;
		}

		if (i == 3) {
			try {
				int hour = Integer.parseInt(arr[0]);
				int minute = Integer.parseInt(arr[1]);
				double second = Double.parseDouble(arr[2]);// 可能有毫秒

				if (between(hour, 0, 60) && between(minute, 0, 60) && between(second, 0, 60)) {

					if (field == Calendar.HOUR_OF_DAY)
						return hour;
					else if (field == Calendar.MINUTE)
						return minute;
					else if (field == Calendar.SECOND)
						return (int) second;
					else if (field == Calendar.MILLISECOND) {// 10.123，则结果为123
						// System.out.println(second);
						return (int) ((second - round(second)) * 1000);
						// return (int) ((second - round(second)) * 1000);
					}
				}
			} catch (NumberFormatException e) {
				return -1;
			}
		}

		return -1;
	}

	public static boolean between(Object value, Object min, Object max) {
		if (value == null || min == null || max == null)
			return false;

		// 修正min，max
		double rMin = toDouble(min, 0);
		double rMax = toDouble(max, 0);

		if (rMin > rMax) {
			rMin = rMax + (rMax = rMin) * 0;
			// 注意顺序，赋值在后面 2012-08-29
		}
		// System.out.println("min=" + rMin + ",max=" + rMax);

		double v = toDouble(value, 0);
		return v >= rMin && v <= rMax;
	}

	public static Date toDate(Object value) {
		// http://eternal1025.iteye.com/blog/344360值得参考
		if (value == null)// null
			return new Date();

		if (value instanceof String) {
			String str = (String) value;

			String pattern = DATE_FORMAT_SHORT;
			if (str.indexOf('.') > -1)
				pattern = DATE_FORMAT_MICROSECOND;
			else if (str.indexOf(' ') > -1)
				pattern = DATE_FORMAT;

			/**
			 * SimpleDateFormat线程不安全，且效率较低 2013-01-09
			 */
			// // 做转换
			// SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			// formatter.setLenient(true);
			//
			// try {
			// return formatter.parse(str);
			// } catch (ParseException e) {
			//
			// }

			/**
			 * 换方案，用joda.time 2013-01-09 by liusan.dyf
			 */
			Date rtn = tools.DateTime.parse(str, pattern);
			if (rtn != null)
				return rtn;

			// 兼容字符串形式的timestamp 2012-12-04 by liusan.dyf
			String temp = value.toString();
			if (tools.Validate.isNumeric(temp)) {
				long v = tools.Convert.toLong(temp, 0);

				if (v == 0)
					return null;
				else
					value = v;// 重置了value的值，为了下面的匹配 2012-12-04
			}
		}

		if (value instanceof Date)// 直接是日期
			return (Date) value;

		if (value instanceof Long) {
			int len = value.toString().length();

			// 2012-12-03 by liusan.dyf
			if (len == 13)// 毫秒 1354502174201
				return new Date((Long) value);
			else if (len == 10)// 秒 1354502174
				return new Date((Long) value * 1000);
		}

		return null;
	}

	public static void main(String... args) {
		System.out.println(args.length);
		System.out.println(now());
		System.out.println(curdate());
		System.out.println(dayofmonth(curdate()));
		System.out.println(dayofmonth("2008-08-08"));
		System.out.println(dayname(null));
		System.out.println(monthname(null));
		System.out.println(last_day(null));
		System.out.println(date_add(null, Calendar.DATE, 1));
		System.out.println(datediff(null, "2012-06-26"));
		System.out.println(unix_timestamp(null));
		System.out.println(from_unixtime(1340610268));
		System.out.println(makdedate(2001, 32));
		System.out.println(ascii("2"));
		System.out.println(bin(12));
		System.out.println(char_(new int[] { 97, 98 }));
		System.out.println(char_());
		System.out.println(concat_ws(",", "First name", null, "Last Name"));
		System.out.println(elt(3, "First name", null, "Last Name"));

		System.out.println(fielt("x", "First name", null, "x1"));
		System.out.println(find_in_set("x", "a,b,c,x,d,e"));
		System.out.println(time_to_second("01:05:10"));
		System.out.println(hex("255"));
		System.out.println(instr("foobarbar", "bar"));
		System.out.println(locate("foobarbar", "bar"));
		System.out.println(locate("foobarbar", "bar", 4));
		System.out.println(lpad("hi", 39, "ab"));
		System.out.println(lpad("hi", 1, "?"));
		System.out.println(rpad("hi", 10, "?"));
		System.out.println(right("hi", 1));
	}
}
