package tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.CronExpression;

public class Cron {
	/**
	 * 2013-08-01 得到一个cron表达式接下来的若干个执行时间
	 * 
	 * @param cronExpression
	 * @param startTimestamp
	 * @param n
	 * @return
	 * @throws Throwable
	 */
	public static List<Date> getNextValidTimes(String cronExpression, long startTimestamp, int n) throws Throwable {
		if (n <= 0)
			n = 1;
		List<Date> list = new ArrayList<Date>(n);

		// [秒] [分] [小时] [日] [月] [周] [年]
		// 实例化表达式类，把字符串转成一个对象
		CronExpression cron = new CronExpression(cronExpression);
		// cron.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Beijing"));

		Date start = null;
		if (startTimestamp <= 0)
			start = new Date();
		else
			start = new Date(startTimestamp * 1000);

		for (int i = 0; i < n; i++) {
			Date d = cron.getNextValidTimeAfter(start);
			// System.out.println(tools.Convert.toString(d));

			start = d;

			list.add(d);
		}

		return list;
	}

	public static long getNextOccurrenceTimestampBySingleCron(String cron, long ts) {
		// 如果cron的秒位是*，那么只要当前时间还和cron的分钟一致，则会返回下一秒的时间
		List<java.util.Date> list = null;
		try {
			list = getNextValidTimes(cron, ts, 1);
		} catch (Throwable e) {

		}
		if (list != null && list.size() > 0)
			return list.get(0).getTime() / 1000;

		return 0;
	}

	/**
	 * 支持多个cron表达式取值，取时间最早的那个，即最早发生的 2013-09-12 by liusan.dyf
	 * 
	 * @param crons
	 * @param ts
	 * @return
	 */
	public static long getNextOccurrenceTimestamp(String crons, long ts) {
		long min = Long.MAX_VALUE;

		String[] arr = tools.StringUtil.split(crons, '\n');

		for (String item : arr) {
			if (tools.Validate.isBlank(item))
				continue;

			long v = getNextOccurrenceTimestampBySingleCron(item, ts);
			if (v != 0 && v < min) {
				min = v;
			}
		}

		if (min == Long.MAX_VALUE)
			return 0;

		return min;
	}

	public static void main(String[] args) {
		String cron = "* 12 18 ? * 2-6\n* 40 11 ? * 2-6";
		System.out.println(tools.Convert.toDate(getNextOccurrenceTimestamp(cron, 0)));
	}
}
