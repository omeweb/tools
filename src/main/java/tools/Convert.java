package tools;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Convert {
	private static final byte BITS = 8; // number of bits in byte variable
	private static final int INDEX_NOT_FOUND = -1;

	/**
	 * v2-v1之间的距离；场景：unit的单位为秒，如果unit=60，则表示结果为分钟单位 2015-2-9 21:56:42 by 六三
	 * 
	 * @param v1
	 * @param v2
	 * @param unit
	 * @return
	 */
	public static long diff(long v1, long v2, int unit) {
		long v = v2 - v1;
		// if (v < 0)
		// v = 0 - v; // 绝对值

		if (unit <= 0)
			unit = 1;

		return v / unit;
	}

	/**
	 * 把别的编码转换为charset 2014-11-20 by 六三
	 * 
	 * @param v
	 * @param charset
	 * @return
	 */
	public static String convertCharset(String v, String charset) {
		if (v == null)
			return null;

		try {
			return java.net.URLDecoder.decode(java.net.URLEncoder.encode(v, charset), charset);
		} catch (UnsupportedEncodingException e) {
			return v;
		}
	}

	/**
	 * 2014-11-18 by 六三
	 * 
	 * @param coll
	 * @return
	 */
	public static String[] toArray(Collection<String> coll) {
		if (coll == null)
			return null;

		String[] arr = coll.toArray(new String[0]);
		return arr;
	}

	/**
	 * 得到数组里，给定个元素的下一个元素，如果找不到则返回第一个元素 2014-05-17 by liusan.dyf
	 * 
	 * @param source
	 * @param ele
	 * @return
	 */
	public static String getElementAfter(String[] source, String ele) {
		int i = indexOf(source, ele);
		return getAt(source, ++i);
	}

	public static int indexOf(Object[] array, Object objectToFind) {
		return indexOf(array, objectToFind, 0);
	}

	/**
	 * org.apache.commons.lang.ArrayUtils.indexOf
	 * 
	 * @param array
	 * @param objectToFind
	 * @param startIndex
	 * @return
	 */
	public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}
		if (startIndex < 0) {
			startIndex = 0;
		}
		if (objectToFind == null) {
			for (int i = startIndex; i < array.length; i++) {
				if (array[i] == null) {
					return i;
				}
			}
		} else if (array.getClass().getComponentType().isInstance(objectToFind)) {
			for (int i = startIndex; i < array.length; i++) {
				if (objectToFind.equals(array[i])) {
					return i;
				}
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * 2014-05-17 by liusan.dyf，从数组里取元素，如果i大于数组长度，则和数组长度取模后再取
	 * 
	 * @param source
	 * @param i
	 * @return
	 */
	public static String getAt(String[] source, int i) {
		if (source == null || source.length == 0)
			return null;

		int len = source.length;
		if (i < len && i >= 0)
			return source[i];
		else {
			int n = i % len;

			return source[n];
		}
	}

	/**
	 * 2014-11-13 by 六三，从数组里取元素，如果i大于数组长度，返回null
	 * 
	 * @param source
	 * @param i
	 * @return
	 */
	public static String getAtExactly(String[] source, int i) {
		if (source == null || source.length == 0)
			return null;

		int len = source.length;
		if (i < len && i >= 0)
			return source[i];

		return null;
	}

	/**
	 * list里去重 2013-06-14 by liusan.dyf<br />
	 * http://stackoverflow.com/questions/12585288/a-fast-way-to-find-unique-values-in-the-list
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> unique(List<T> list) {
		// 当list元素较少时，set的方式最快；较大时，set比arraylist、linkedlist的contains方法稍稍慢

		Set<T> set = new HashSet<T>(list.size(), 1F);
		for (T item : list) {
			set.add(item);
		}
		return new ArrayList<T>(set);
	}

	/**
	 * 2013-06-26 by liusan.dyf
	 * 
	 * @param coll
	 * @return
	 */
	public static int size(Collection<?> coll) {
		if (coll == null)
			return 0;
		return coll.size();
	}

	/**
	 * 把a按照b左对齐，比如10按3左对齐，即9。 2012-08-23
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static long leftAligned(long a, long b) {
		long i = a;
		long m = i % b; // 对齐
		// System.out.println(m);
		// System.out.println(i - m);
		return i - m;
	}

	public static long rightAligned(long a, long b) {
		return leftAligned(a, b) + b;
	}

	/**
	 * 利用反射，得到对象所有声明的字段，转换为map返回。key=fieldName，value=fieldValue.toString 2012-12-25<br />
	 * 如果fieldValue还是个对象，则不再递归获取，直接toString返回
	 * 
	 * @param v
	 * @return
	 */
	public static Map<String, String> getObjectFieldMap(Object v) {
		if (v == null)
			return null;

		Class<?> clazz = v.getClass();

		// 数组，不予处理
		if (clazz.isArray()) {
			return null;
		}

		// 得到所有的fields
		Field[] fields = clazz.getDeclaredFields();
		Map<String, String> map = MapUtil.create(fields.length);

		AccessibleObject.setAccessible(fields, true);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();

			try {
				// Warning: Field.get(Object) creates wrappers objects for primitive types.
				Object fieldValue = field.get(v);

				if (fieldValue != null)
					map.put(fieldName, fieldValue.toString());

			} catch (IllegalAccessException ex) {
				// this can't happen. Would get a Security exception instead
				// throw a runtime exception in case the impossible happens.
				// throw new InternalError("Unexpected IllegalAccessException: " + ex.getMessage());
			}
		}

		return map;
	}

	/**
	 * 注意bigArray的长度，一定要够 2012-07-19
	 * 
	 * @param smallArrays
	 * @param bigArray
	 */
	public static void copySmallArraysToBigArray(final byte[][] smallArrays, final byte[] bigArray) {

		// http://stackoverflow.com/questions/4827622/copy-several-byte-arrays-to-one-big-byte-array

		int currentOffset = 0;
		for (final byte[] currentArray : smallArrays) {
			System.arraycopy(currentArray, 0, bigArray, currentOffset, currentArray.length);
			currentOffset += currentArray.length;
		}
	}

	/**
	 * 2015-1-4 15:58:45 by 拉赫
	 * 
	 * @param value
	 * @param radix
	 * @param def
	 * @return
	 */
	public static long toLongWithRadix(String value, int radix, long def) {
		if (value == null || value.length() == 0)
			return def;

		try {
			long result = Long.parseLong(value, radix);
			return result;
		} catch (Exception e) {
			return def;
		}
	}

	public static long toLong(byte[] bytes) {
		long result = 0;
		int len = 8;
		for (int i = 0; i < bytes.length; i++) {
			result += ((bytes[i] & 0xFF) << ((len - 1 - i) * BITS));
		}
		return result;
	}

	public static int toInt(byte[] bytes) {
		int result = 0;
		int len = 4;
		for (int i = 0; i < bytes.length; i++) {
			result += ((bytes[i] & 0xFF) << ((len - 1 - i) * BITS));
		}
		return result;
	}

	public static byte[] intToBytes(int var) {
		int len = 4;
		byte[] result = new byte[len];

		for (int i = 0; i < len; i++) {
			result[i] = (byte) (var >> ((len - 1 - i) * BITS));
		}
		return result;
	}

	public static byte[] longToBytes(long var) {
		int len = 8;
		byte[] result = new byte[len];

		for (int i = 0; i < len; i++) {
			result[i] = (byte) (var >> ((len - 1 - i) * BITS));
		}
		return result;
	}

	static String toHexCode(int v) {
		String r = null;
		if (v < 10)
			r = String.valueOf(v);
		else {
			int i = v + 55;// 一律为大写
			r = String.valueOf((char) i);
		}
		return r;
	}

	/**
	 * 2013-04-16 by liusan.dyf
	 * 
	 * @param value
	 * @param def
	 * @return
	 */
	public static String toString(Object value, String def) {
		if (value == null)
			return def;
		return toString(value);
	}

	/**
	 * 转换为字符串；其中特殊点为：true会变成1，false为0，主要为模型 2013-04-28 by liusan.dyf
	 * 
	 * @param value
	 * @return
	 */
	public static String toStringEx(Object value) {
		if (value instanceof Boolean) {
			boolean f = (Boolean) value;
			return f ? "1" : "0";
		}

		// String s = toString(value);
		// if ("true".equals(s))
		// return "1";
		// else if ("false".equals(s))
		// return "0";

		return toString(value);
	}

	public static String toString(Object value) {
		if (value == null)
			return null;

		if (value instanceof String)
			return (String) value;
		else if (value instanceof Date) {
			return tools.DateTime.format((Date) value, null);// hh是12小时的，HH是24小时的
		} else if (value instanceof Throwable) {
			return getErrorMessage((Throwable) value);// 2013-11-04 by liusan.dyf
		} else
			return value.toString();
	}

	public static String getErrorMessage(Throwable e) {
		String message = null;

		// from org.apache.commons.logging.impl.SimpleLog
		java.io.StringWriter sw = new java.io.StringWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(sw);
		e.printStackTrace(pw);
		pw.close();

		message = sw.toString(); // stack trace as a string
		// StringWriter内部是个StringBuffer，可以不用close

		return message;
	}

	public static String toHexString(int num, int t) {
		if (t == 10) // 2011-05-06 增加 by 杜有发
			return String.valueOf(t);

		// if (t < 2 || t > 36)
		// throw new Exception("进制不合本程序要求，请输入2-36之间的任意整数(包含2和36)！");

		StringBuilder r = new StringBuilder();

		if (num == 0)
			return "0";

		while (num > 0) {
			r.insert(0, toHexCode(num % t));
			num = num / t;
		}

		return r.toString();
	}

	public static long computeHash(byte[] data) {
		int p = 16777619;
		long hash = 2166136261L;
		for (byte b : data)
			hash = (hash ^ b) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;

		return hash;
	}

	public static long computeHash(String str) {
		try {
			return computeHash(str.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static String join(Object coll, String delimiter) {
		if (coll == null)// 2011-11-10
			return "";

		StringBuilder sb = new StringBuilder();
		boolean first = true;

		if (coll instanceof Iterable<?>) {
			Iterable<?> it = (Iterable<?>) coll;
			Iterator<?> iter = it.iterator();

			// 循环
			while (iter.hasNext()) {
				Object o = iter.next();

				if (!first)
					sb.append(delimiter);
				else
					first = false;

				sb.append(o == null ? null : o.toString());
			}
		} else if (coll instanceof Object[]) {// 数组
			first = true;
			for (Object item : (Object[]) coll) {
				if (!first)
					sb.append(delimiter);
				else
					first = false;

				sb.append(item == null ? null : item.toString());
			}
		}

		return sb.toString();
	}

	public static String join(Iterable<?> it) {
		return join(it, ",");
	}

	public static boolean toBoolean(Object o) {
		if (o == null)
			return false;
		if (o instanceof Integer) {
			Integer i = (Integer) o;
			return i >= 1 ? true : false;
		}

		String str = o.toString();

		return str.equalsIgnoreCase("true") ? true : false;
	}

	public static long ipToLong(String ipAddress) {
		// 如果参数不是合法的IP，这会出现异常
		String[] arr = StringUtil.split(ipAddress, '.');// .是正则里的特殊字符
		if (arr.length != 4) // 2014-06-16 by liusan.dyf
			return 0;

		long v = Long.parseLong(arr[0]) << 24;
		v += Long.parseLong(arr[1]) << 16;
		v += Long.parseLong(arr[2]) << 8;
		v += Long.parseLong(arr[3]);

		// 要用Long.parseLong，用int的话，精度丢失，结果就不正确了 2013-05-22 by liusan.dyf
		return v;
	}

	public static String longToIp(long ipAddress) {
		long uint1 = (ipAddress & 0xFF000000) >> 24;
		long uint2 = (ipAddress & 0x00FF0000) >> 16;
		long uint3 = (ipAddress & 0x0000FF00) >> 8;
		long uint4 = ipAddress & 0x000000FF;

		// return String.format("{0}.{1}.{2}.{3}", uint1, uint2, uint3, uint4);
		// bug fixed 2014-09-01 by liusan.dyf from 若远
		return String.format("%d.%d.%d.%d", uint1, uint2, uint3, uint4);
	}

	public static int toInt(Object value, int def) {
		if (value == null)
			return def;

		if (value instanceof Character) {// 不能直接从 Character => int，要用char来中转
			char ch = (Character) value;
			return (int) ch;
		}

		// int...
		if (value instanceof Number) {// 2014-12-15 六三 from 若远 反馈
			Number n = (Number) value;
			return n.intValue();
		}

		// if (value instanceof Byte || value instanceof Short || value instanceof Integer) {
		// return (Integer) value;
		// }
		//
		// if (value instanceof Float || value instanceof Double) {
		//
		// // 不能直接从 Double => int，要用double来中转
		// double d = (Double.valueOf(value.toString()));
		// return (int) d;
		// }

		if (Validate.isInt(value)) {
			return Integer.valueOf(value.toString());
		}

		return def;
	}

	public static float toFloat(Object value, float def) {
		if (value == null)
			return def;

		if (value instanceof Float) {
			return (Float) value;
		}

		if (value instanceof Number) { // 2014-12-15 六三
			Number n = (Number) value;
			return n.floatValue();
		}

		String v = value.toString();
		if (v.length() > 0) {
			if (v.toCharArray()[0] == '.') {
				v = "0" + v;// 解决转换【.2】的问题 2011-10-31
			}

			if (Validate.isNumeric(v)) {
				return Float.valueOf(v);
			}
		}

		return def;
	}

	public static double toDouble(Object value, double def) {
		if (value == null)
			return def;

		if (value instanceof Character) {
			char ch = (Character) value;
			return (double) ch;
		}

		// Float不能强制转换为Double fixed 2012-09-05 by liusan.dyf
		if (value instanceof Float)
			return ((Float) value).doubleValue();

		if (value instanceof Double)
			return (Double) value;

		// 2012-09-14 by liusan.dyf
		if (value instanceof BigDecimal)
			return ((BigDecimal) value).doubleValue();

		if (value instanceof Long) // 如果是long，直接返回就OK
			return (Long) value;

		if (value instanceof Date)// 解决时间问题
			return ((Date) value).getTime() / 1000;

		String v = value.toString();
		// if (v.length() > 0) {
		// if (v.toCharArray()[0] == '.') {
		// v = "0" + v;// 解决转换【.2】的问题 2011-10-31
		// }
		//
		// if (Validate.isDouble(v)) {
		// return Double.valueOf(v);
		// }
		// }

		if (Validate.isNumeric(v)) {
			// fixed java.lang.NumberFormatException: For input string: "1111L" 2013-05-22 by liusan.dyf
			int lastIndex = v.length() - 1;
			char c = v.charAt(lastIndex);
			if (c == 'L' || c == 'l')
				v = v.substring(0, lastIndex);

			return Double.valueOf(v);
		}

		return def;
	}

	/**
	 * 如果是date，则返回时间戳（秒）
	 * 
	 * @param value
	 * @param def
	 * @return
	 */
	public static long toLong(Object value, long def) {
		if (value == null)
			return def;

		// eg 900000000000816642L
		if (value instanceof Number) // 如果value是很大的long值，先转换为double再转回long，会丢失精度 2013-11-20 by liusan.dyf
			return ((Number) value).longValue();

		String str = value.toString();
		int pos = -1;
		if (Validate.isNumericEx(str)) {// 不包含数字后缀的判断，比如0.25f
			return Long.valueOf(str);// str里不支持小数点，但可以是【0250】
		} else if ((pos = str.indexOf('.')) > -1) {
			// 包含小数点，截断小数点
			str = str.substring(0, pos); // System.out.println(str);
			if (Validate.isNumericEx(str))
				return Long.valueOf(str);
		}

		// 可能字符串里有小数点，或者【10.4d】，用double的方式来判断 2014-11-27 by 六三
		// TODO 如果是比较大的带有小数点的，就会丢失精度：【9223370000000104524.888】
		Double d = toDouble(value, def);
		return d.longValue();
	}

	/**
	 * 得到传入的timestamp所在的date的ts，不包含【时分秒】，如果传入0，则认为是当天 2012-12-10 by liusan.dyf
	 * 
	 * @param ts
	 * @return
	 */
	public static long getThatDayTimestamp(long ts) {
		if (ts <= 0)
			ts = System.currentTimeMillis() / 1000;
		/*
		 * 1316880000/3600/24 = 15241.666666667
		 */
		// 所有当天（不含时间）的timestamp，除以3600*24，结果都有.666666667的小数
		long day = 3600 * 24;

		double d = ts * 1.0 / day;// 当天多了多少了
		double i = ts / day + 0.666666667;// 整天

		// System.out.println("d=" + d + ",i=" + i);
		// 当传入的ts是整天的情况下
		// d=15702.666666666666
		// i=15702.666666667

		if (d > i || (i - d) < 0.000000001)// 或者的分支判断，是解决传入的ts是整天的时候，当差距如此之小，当作无
			return (long) (i * day);// 当天
		else
			return (long) ((i - 1) * day);// 前一天
	}

	public static long toUnixTime() {
		return System.currentTimeMillis() / 1000L;
	}

	public static long toUnixTime(Date dateTime) {
		if (dateTime == null)// 2012-12-26 by liusan.dyf
			return toUnixTime();

		return dateTime.getTime() / 1000L;
	}

	/**
	 * 可能返回null
	 * 
	 * @param value
	 * @return
	 */
	public static Date toDateTime(Object value) {
		// 2011-10-13 12:52:52和2011-10-13都可以应付 2011-10-24验证，不是这样的

		// // 新版本
		// if (value == null)
		// return null;
		// if (value.toString().indexOf((char) ':') > -1)
		// return toDateTime(value, DATE_PATTERN);
		// else
		// return toDateTime(value, SHORT_DATE_PATTERN);

		/* 版本2 */
		// Date rtn = null;
		//
		// String[] formatters = new String[] { DATE_FORMAT_MICROSECOND, DATE_FORMAT, DATE_FORMAT_SHORT };
		// for (String item : formatters) {
		// rtn = toDateTime(value, item);
		// if (rtn != null)
		// return rtn;
		// }
		//
		// return rtn;

		/* 版本3 */
		return MySqlFunction.toDate(value);
	}

	public static Date toDate(long timestamp) {
		return new Date(timestamp * 1000);// 这里的参数表示是毫秒
	}

	public static void main(String[] args) {
		Object a = 10;
		System.out.println(a instanceof Integer);// true
		System.out.println(a instanceof Long);// false
		// System.out.println(Integer.valueOf("10.2"));//exception

		float b = 10.2f;
		System.out.println((int) (b / 1));// 10

		// Object c = 10.2f;
		// System.out.println((Integer) c);
		// exception:java.lang.Float cannot be cast to java.lang.Integer

		char ch = 'a';
		System.out.println((int) ch);
		// System.out.println((Integer)ch);//not complire

		System.out.println(Double.valueOf(".2"));

		// String e = "";
		// System.out.println(e.toCharArray()[0]); // java.lang.ArrayIndexOutOfBoundsException: 0

		// 2012-10-22 by liusan.dyf
		System.out.println(toDouble("123123.cwb", 0));
		System.out.println(toDouble("123123.0", 0));

		// 2013-01-09 by liusan.dyf
		System.out.println(getObjectFieldMap(new Date()));

		//
		System.out.println(longToIp(1973617276));

		// 2014-11-18 by 六三
		List<String> list = new java.util.ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		System.out.println(toArray(list)[0]);

		// 2014-11-27 by 六三
		System.out.println(Convert.toLong("9223370000000104524", 0));
		System.out.println(Convert.toLong(10.8, 0));
		System.out.println(Convert.toLong("10.8d", -1));
		System.out.println(Convert.toLong("9223370000000104524.888", -1));
		System.out.println(Convert.toLong(".5", -1));// 0
		System.out.println(Convert.toLong(9223370000000104524L, 0));
	}
}
