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

	public static final int HOUR_MILLISECONDS = 1000 * 3600;// 毫秒 2016-7-28 15:23:41 by liusan.dyf
	public static final int DAY_MILLISECONDS = HOUR_MILLISECONDS * 24;
	public static final int WEEK_MILLISECONDS = DAY_MILLISECONDS * 7;

	public static final int HOUR_SECONDS = 3600;// 秒 2016-7-28 15:23:41 by liusan.dyf
	public static final int DAY_SECONDS = HOUR_SECONDS * 24;
	public static final int WEEK_SECONDS = DAY_SECONDS * 7;

	// ---------------2015-9-21 11:49:09 by liusan.dyf
	private static volatile long currentTimeMillis = System.currentTimeMillis();// 当前时间

	static {
		// 每隔一定的时间来更新时间戳，在高并发下比较有优势，但是精度有限
		// 更多参见：http://blog.sina.com.cn/s/blog_7ce08df70101ig4r.html
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						// TimeUnit.MILLISECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					currentTimeMillis = System.currentTimeMillis();
				}
			}
		}, "systemTimeMillisUpdater");

		th.setDaemon(true);// 不然不会随主进程一起关闭
		th.start();
	}

	public static long currentTimeMillis() {
		return currentTimeMillis;
	}

	public static long timestamp() {
		return currentTimeMillis / 1000;
	}

	// ---------------end of currentTimeMillis

	/**
	 * 返回2个时间之差，d2-d1，单位为秒；如果入参不合法，返回-1。2014-05-12 by liusan.dyf
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
	 * 计算input和toCompare之间日期部分相差几天；如果是今天则返回0，前一天返回-1，后一天返回1 2016-12-5 20:48:35 by liusan.dyf
	 * 
	 * @param input
	 * @param toCompare
	 * @return
	 */
	public static int diffDays(Date input, Date toCompare) {
		Date d1 = clearTime(input);
		Date d2 = clearTime(toCompare);

		return (int) ((d1.getTime() - d2.getTime()) / DAY_MILLISECONDS);

		// 测试代码：System.out.println(diffDays(getDate(-1), getDate(0))); // -1
	}

	/**
	 * 包括了时分秒部分 2013-12-30 by liusan.dyf
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
	 * 追加秒数 2014-05-06 by liusan.dyf
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

	// ------------------------2016-12-10 15:55:51 by liusan.dyf

	public static Date plusSeconds(Date base, int seconds) {
		return new org.joda.time.DateTime(base).plusSeconds(seconds).toDate();
	}

	public static Date plusMinutes(Date base, int v) {
		return new org.joda.time.DateTime(base).plusMinutes(v).toDate();
	}

	public static Date plusHours(Date base, int v) {
		return new org.joda.time.DateTime(base).plusHours(v).toDate();
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
		if (!formatters.containsKey(pattern)) { // ___#1___
			synchronized (formatters) {
				if (!formatters.containsKey(pattern)) {// double check
					rtn = DateTimeFormat.forPattern(pattern);
					formatters.put(pattern, rtn);
				} else
					rtn = formatters.get(pattern);// 2014-03-21 by liusan.dyf 防止上面的if没有走到而返回空指针
			}
		}

		if (rtn != null) // 如果已经有值，直接返回 2015-12-17 10:17:25 by liusan.dyf
			return rtn;

		return formatters.get(pattern); // 如果___#1___不成立，那rtn为null，这里再次从map里取 2015-12-17 10:18:14 by liusan.dyf
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
		if (Validate.isNullOrEmpty(pattern))
			pattern = DEFAULT_FORMATTER;

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

	/**
	 * 把秒位清零 2016-7-28 19:32:08 by liusan.dyf
	 * 
	 * @param d
	 * @return
	 */
	public static Date clearSeconds(Date d) {
		if (d == null)
			d = new Date();

		return new org.joda.time.DateTime(d).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
	}

	public static Date clearSeconds(long ts) {
		Date d = new Date(ts);
		return clearSeconds(d);
	}

	public static Date clearTime(long ts) {
		Date d = new Date(ts);
		return clearTime(d);
	}

	public static Date clearTime(Date d) {
		return new org.joda.time.DateTime(d).withTime(0, 0, 0, 0).toDate();
	}

	/**
	 * 让d1的时间部分用d2的时间来代替 2016-7-29 10:51:51 by liusan.dyf
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static Date mixinTime(Date d1, Date d2) {
		if (d1 == null)
			return null;

		if (d2 == null)
			return d1;

		org.joda.time.DateTime dt2 = new org.joda.time.DateTime(d2);

		int h = dt2.getHourOfDay();
		int m = dt2.getMinuteOfHour();
		int s = dt2.getSecondOfMinute();
		int ms = dt2.getMillisOfSecond();

		return new org.joda.time.DateTime(d1).withTime(h, m, s, ms).toDate();
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
		System.out.println(time.getMonthOfYear());// 7
		System.out.println(time.getYear());// 2016

		//
		System.out.println(plusSeconds(300));

		String x = format(clearSeconds(null), "yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println("---------clearSeconds:" + x);

		System.out.println("---------mixinTime:" + mixinTime(new Date(), plusSeconds(200)));

		//
		System.out.println(diff(new Date(), plusSeconds(200)));// 200

		//
		System.out.println(getFirstDayOfMonth(2014, 10));// 10-1
		System.out.println(getFirstDayOfMonth(null));
		System.out.println(getLastDayOfMonth(null));

		// 2015-9-21 12:12:27 by liusan.dyf
		System.out.println(currentTimeMillis() + "." + System.currentTimeMillis());
		Global.sleep(50);
		System.out.println(currentTimeMillis() + "." + System.currentTimeMillis());
		Global.sleep(500);
		System.out.println(currentTimeMillis() + "." + System.currentTimeMillis());
		System.out.println(timestamp());

		// Global.sleep(50000);
		System.out.println(diffDays(getDate(-1), getDate(0))); // -1

		long d = tools.DateTime.diff(parse("2016-12-10 19:03:46", null), plusDays(3));
		System.out.println("_________" + d);
	}
}
