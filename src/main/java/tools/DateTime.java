package tools;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 使用org.joda.time来处理时间
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-1-9
 */
public class DateTime {
	private static Map<String, DateTimeFormatter> formatters = new java.util.concurrent.ConcurrentHashMap<String, DateTimeFormatter>();

	private static final String DEFAULT_FORMATTER = "YYYY-MM-dd HH:mm:ss";

	/**
	 * 返回2个时间之差，单位为秒；如果入参不合法，返回-1。2014-05-12 by liusan.dyf
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static long diff(Date d1, Date d2) {
		if (d1 == null || d2 == null)
			return -1;

		return (d2.getTime() - d1.getTime()) / 1000;
	}

	/**
	 * 2013-12-30 by liusan.dyf
	 * 
	 * @param plusDays
	 * @return
	 */
	public static Date getDate(int plusDays) {
		return plusDays(plusDays);
	}

	public static Date plusDays(int plusDays) {
		return new org.joda.time.DateTime().plusDays(plusDays).toDate();
	}

	/**
	 * 可以追加秒数 2014-05-06 by liusan.dyf
	 * 
	 * @param seconds
	 * @return
	 */
	public static Date plusSeconds(int seconds) {
		return new org.joda.time.DateTime().plusSeconds(seconds).toDate();
	}

	public static Date plusMinutes(int v) {
		return new org.joda.time.DateTime().plusMinutes(v).toDate();
	}

	public static Date plusHours(int v) {
		return new org.joda.time.DateTime().plusHours(v).toDate();
	}

	/**
	 * 得到给定日期的周一 2014-01-13 by liusan.dyf
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonday(Date date) {
		if (date == null)
			date = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}

		return cal.getTime();
	}

	public static Date getMonday(String dateString, String p) {
		Date date = parse(dateString, p);
		return getMonday(date);
	}

	// public static Date getSpecialDate(Date date, int month, int hour, int minute, int second) {
	// if (date == null)
	// date = new Date();
	//
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(date);
	//
	// if (month > 0)// 月不能为0或者负数
	// cal.set(Calendar.MONTH, month);
	//
	// if (hour > -1)
	// cal.set(Calendar.HOUR_OF_DAY, hour);
	//
	// if (minute > -1)
	// cal.set(Calendar.MINUTE, minute);
	//
	// if (second > -1)
	// cal.set(Calendar.SECOND, second);
	//
	// return cal.getTime();
	// }

	public static String formatNow(String pattern) {
		return format(null, 0, pattern);
	}

	/**
	 * @param d 为null表示现在
	 * @param pattern
	 * @return
	 */
	public static String format(Date d, String pattern) {
		return format(d, 0, pattern);
	}

	/**
	 * 2015-3-4 18:32:12 by 六三
	 * 
	 * @param ts 毫秒级
	 * @param pattern
	 * @return
	 */
	public static String format(long ts, String pattern) {
		Date d = new Date(ts);
		return format(d, pattern);
	}

	/**
	 * 2013-12-11 by liusan.dyf
	 * 
	 * @param d
	 * @param plusDays
	 * @param pattern
	 * @return
	 */
	public static String format(Date d, int plusDays, String pattern) {
		if (d == null)
			d = new Date();
		// System.out.println(DateTimeZone.getAvailableIDs());

		if (pattern == null)
			pattern = DEFAULT_FORMATTER;

		org.joda.time.DateTime dt = new org.joda.time.DateTime(d.getTime()).plusDays(plusDays);

		// return dt.toString(pattern);
		return getFormatter(pattern).print(dt);
	}

	public static DateTimeFormatter getFormatter(String pattern) {
		DateTimeFormatter rtn = formatters.get(pattern);
		if (rtn != null)
			return rtn;

		// 做缓存
		if (!formatters.containsKey(pattern)) {
			synchronized (formatters) {
				if (!formatters.containsKey(pattern)) {// double check
					rtn = DateTimeFormat.forPattern(pattern);
					formatters.put(pattern, rtn);
				} else
					rtn = formatters.get(pattern);// 2014-03-21 by liusan.dyf 防止上面的if没有走到而返回空指针
			}
		}

		return rtn;
	}

	/**
	 * http://stackoverflow.com/questions/8854780/parse-date-string-to-some-java-object<br />
	 * http://johannburkard.de/blog/programming/java/date-time-parsing-formatting-joda-time.html
	 * 
	 * @param value Date or String
	 * @param pattern
	 * @return
	 */
	public static Date parse(Object value, String pattern) {
		if (value == null || pattern == null || pattern.length() <= 0) {
			return null;
		}

		if (value instanceof Date) // 2011-10-31
			return (Date) value;

		try {
			org.joda.time.DateTime time = getFormatter(pattern).parseDateTime(value.toString());
			return time.toDate();
		} catch (Exception ex) {
			// java.lang.IllegalArgumentException: Invalid format:...
		}

		return null;
	}

	/**
	 * 得到下月第一天，其中时分秒全部清零 2014-07-29 by liusan.dyf
	 * 
	 * @param d
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date d) {
		if (d == null)
			d = new Date();

		// Calendar cal = Calendar.getInstance();
		// cal.setTime(d);
		// cal.set(Calendar.DATE, 1);// 设为当前月的1号
		// cal.set(Calendar.HOUR_OF_DAY, 0);
		// cal.set(Calendar.MINUTE, 0);
		// cal.set(Calendar.SECOND, 0);
		// cal.set(Calendar.MILLISECOND, 0);
		//
		// cal.add(Calendar.MONTH, diff);// +一个月，变为下月的1号
		// // lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天
		//
		// return cal.getTime();

		return new org.joda.time.DateTime(d).withDayOfMonth(1).withTime(0, 0, 0, 0).toDate();
	}

	public static Date getFirstDayOfMonth(int year, int month) {
		return new org.joda.time.DateTime(year, month, 1, 0, 0).toDate();
	}

	public static Date getLastDayOfMonth(Date d) {
		if (d == null)
			d = new Date();

		org.joda.time.DateTime dt = new org.joda.time.DateTime(d).withDayOfMonth(1).withTime(0, 0, 0, 0);

		return dt.plusMonths(1).plusDays(-1).toDate();
	}

	public static void main(String[] args) {
		org.joda.time.DateTime time = new org.joda.time.DateTime();
		System.out.println(time.getMillis());
		System.out.println(time.getMonthOfYear());
		System.out.println(time.getYear());

		//
		System.out.println(plusSeconds(300));

		//
		System.out.println(diff(new Date(), plusSeconds(200)));

		//
		System.out.println(getFirstDayOfMonth(2014, 10));		
		System.out.println(getFirstDayOfMonth(null));
		System.out.println(getLastDayOfMonth(null));
	}
}
