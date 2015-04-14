package tools.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

/**
 * 基于quartz 2012-01-31<br />
 * 一个实例只解决一个组的问题，除非多次设置group，不过可能有并发问题
 * 
 * @author liusan.dyf
 */
public class JobScheduler {
	/**
	 * 默认组名
	 */
	private static final String DEFAULT_GROUP_NAME = "group";
	private static final Log LOGGER = LogFactory.getLog(JobScheduler.class);

	private static final AtomicInteger COUNTER = new AtomicInteger(0);

	private Scheduler scheduler = null;

	/**
	 * 不在构造函数里体现group，因为这不是必需的 2012-02-01
	 */
	private String groupName = DEFAULT_GROUP_NAME;

	public JobScheduler(Scheduler scheduler) {
		this.setScheduler(scheduler);
	}

	/**
	 * 开始调度
	 */
	public void start() {
		// Start up the scheduler (nothing can actually run until the
		// scheduler has been started)

		if (getScheduler() == null) {
			throw new IllegalArgumentException("请先设置正确的Scheduler");
		}

		try {
			if (!getScheduler().isStarted())
				getScheduler().start();
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 增加一个单次执行的任务
	 * 
	 * @param startTime
	 * @param runJob
	 * @return
	 */
	public boolean addJob(Date startTime, Class<? extends Job> runJob) {
		return addJob(startTime, runJob, null, null);
	}

	/**
	 * 增加一个单次执行的任务，同时指定job的data
	 * 
	 * @param startTime
	 * @param runJob
	 * @param jobData
	 * @return
	 */
	public boolean addJob(Date startTime, Class<? extends Job> runJob, Map<String, Object> jobData) {
		return addJob(startTime, runJob, jobData, null);
	}

	/**
	 * 增加一个单次执行的任务，要指定jobData和job名称，名称不能重复，参数里名称可以为空
	 * 
	 * @param startTime
	 * @param runJob
	 * @param dataMap
	 * @param name
	 * @return
	 */
	public boolean addJob(Date startTime, Class<? extends Job> runJob, Map<String, Object> dataMap, String name) {
		// 处理job名称
		String innerJobName = getJobName(name);

		// 判断是否重复
		if (checkExists(innerJobName, getGroupName()))
			return false;

		JobDetail job = prepareJobDetail(innerJobName, getGroupName(), runJob, dataMap);

		// 创建trigger
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		trigger.setKey(new TriggerKey(getTriggerName(innerJobName)));
		trigger.setStartTime(startTime);
		trigger.setGroup(getGroupName());

		try {
			getScheduler().scheduleJob(job, trigger);
			return true;
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}// 如果job的名字重复，则引发Quartz.ObjectAlreadyExistsException异常

		return false;
	}

	/**
	 * 周期性任务,名称不能重复，需要指定执行间隔、执行次数，-1表示永远执行
	 * 
	 * @param startTime
	 * @param runJob
	 * @param dataMap
	 * @param theJobName
	 * @param intervalSeconds
	 * @param repeatCount
	 * @return
	 */
	public boolean addJob(Date startTime, Class<? extends Job> runJob, Map<String, Object> dataMap, String theJobName,
			int intervalSeconds, int repeatCount) {
		// 处理job名称
		String innerJobName = getJobName(theJobName);

		// 判断是否重复
		if (checkExists(innerJobName, getGroupName()))
			return false;

		JobDetail job = prepareJobDetail(innerJobName, getGroupName(), runJob, dataMap);

		// 创建trigger
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		trigger.setKey(new TriggerKey(getTriggerName(innerJobName)));
		trigger.setStartTime(startTime);
		// trigger.setRepeatCount(Integer.MAX_VALUE);// 2012-01-31 要设置
		// trigger.setRepeatCount(-1);// 或者设置为-1
		trigger.setRepeatCount(repeatCount);
		trigger.setRepeatInterval(intervalSeconds * 1000L);
		trigger.setGroup(getGroupName());

		try {
			getScheduler().scheduleJob(job, trigger);
			return true;
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}

		return true;
	}

	/**
	 * 周期性任务，要执行执行间隔
	 * 
	 * @param startTime
	 * @param runJob
	 * @param intervalSeconds
	 * @return
	 */
	public boolean addJob(Date startTime, Class<? extends Job> runJob, int intervalSeconds) {
		return addJob(startTime, runJob, null, null, intervalSeconds, -1);
	}

	/**
	 * 周期性任务，要执行执行间隔和执行次数，-1表示永远执行下去
	 * 
	 * @param startTime
	 * @param runJob
	 * @param intervalSeconds
	 * @param repeatCount
	 * @return
	 */
	public boolean addJob(Date startTime, Class<? extends Job> runJob, int intervalSeconds, int repeatCount) {
		return addJob(startTime, runJob, null, null, intervalSeconds, repeatCount);
	}

	public boolean addJob(String cron, Class<? extends Job> runJob, Map<String, Object> dataMap, String name) {
		// 处理job名称
		String innerJobName = getJobName(name);

		// 判断是否重复
		if (checkExists(innerJobName, getGroupName()))
			return false;

		JobDetail job = prepareJobDetail(innerJobName, getGroupName(), runJob, dataMap);

		try {
			// 创建trigger
			CronTriggerImpl trigger = new CronTriggerImpl();
			trigger.setCronExpression(cron);
			trigger.setGroup(getGroupName());
			trigger.setKey(new TriggerKey(getTriggerName(innerJobName)));

			scheduler.scheduleJob(job, trigger);// 如果job的名字重复，则引发Quartz.ObjectAlreadyExistsException异常
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		// 2011-02-21 如果cron表达式错误，或者是无法执行的（一次性的、小于当前时间的），则会报错
		return false;
	}

	/**
	 * 2012-01-31，可以用来替换job的data数据
	 * 
	 * @param jobDetail
	 * @param replace
	 * @return
	 */
	public boolean addJob(JobDetail jobDetail, boolean replace) {
		try {
			getScheduler().addJob(jobDetail, replace);
			return true;
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}

		return false;
	}

	public static boolean isNullOrEmpty(String value) {
		return value == null || value.length() == 0;
	}

	public boolean deleteJob(String name) {
		// from
		// http://stackoverflow.com/questions/1904064/delete-trigger-in-quartz

		try {
			JobKey key = new JobKey(name, getGroupName());// 注意group name

			boolean f = getScheduler().deleteJob(key);// 2012-01-31

			// System.out.println(f);
			//
			// f = getScheduler().checkExists(key);
			// System.out.println(f);
			//
			// f = getScheduler().unscheduleJob(
			// new TriggerKey(getTriggerName(key.getName())));
			// System.out.println(f);

			return f;
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}

		return false;
	}

	/**
	 * 得到job的数据，只读的，无法修改
	 * 
	 * @param name
	 * @return
	 */
	public JobDataMap getJobData(String name) {
		JobDetail j = null;
		try {
			j = getScheduler().getJobDetail(new JobKey(name, getGroupName()));
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
		if (j != null)
			return j.getJobDataMap();

		return null;
	}

	/**
	 * 重新设置job的data
	 * 
	 * @param name
	 * @param data
	 * @return
	 */
	public boolean setJobData(String name, Map<String, Object> data) {
		// from
		// http://stackoverflow.com/questions/2829731/update-an-existing-jobdatamap
		try {
			JobDetail j = getScheduler().getJobDetail(new JobKey(name, getGroupName()));
			if (j != null) {
				// 这里的JobData其实是一个拷贝，在这里修改是无效的
				j.getJobDataMap().putAll(data);

				getScheduler().addJob(j, true);
				// 一定要，否则修改失败，修改完后再获取，依然是原来的值，目前先删除这个job，再创建一个新的
				return true;
			}
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
		return false;
	}

	/**
	 * 获取当前正在执行的jobs，内部调用Scheduler.getCurrentlyExecutingJobs<br />
	 * 一般来说，满载时候其size为线程池的大小 2012-02-17
	 * 
	 * @return
	 */
	public List<JobDetail> getCurrentlyExecutingJobs() {
		List<JobDetail> rtn = null;
		try {
			List<JobExecutionContext> list = getScheduler().getCurrentlyExecutingJobs();

			rtn = new ArrayList<JobDetail>(list.size());

			for (JobExecutionContext item : list) {
				// System.out.println(item.getJobDetail().getKey());
				rtn.add(item.getJobDetail());
			}
		} catch (SchedulerException e) {
			LOGGER.error(e);
			rtn = new ArrayList<JobDetail>(0);
		}

		return rtn;
	}

	public void shutdown(boolean waitForJobsToComplete) {
		if (getScheduler() != null) {
			try {
				if (!getScheduler().isShutdown())
					getScheduler().shutdown(waitForJobsToComplete);
			} catch (SchedulerException e) {
				LOGGER.error(e);
			} finally {
			}
		}
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

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

	/**
	 * 设置调度器
	 * 
	 * @param scheduler
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * 组名
	 * 
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * 2012-02-01
	 * 
	 * @param name
	 * @param group
	 * @param runJob
	 * @param dataMap
	 * @return
	 */
	JobDetail prepareJobDetail(String name, String group, Class<? extends Job> runJob, Map<String, Object> dataMap) {
		JobDetail job = JobBuilder.newJob(runJob).withIdentity(name, group).build();
		if (dataMap != null)
			job.getJobDataMap().putAll(dataMap);

		return job;
	}

	/**
	 * 判断该任务名称是否重复
	 * 
	 * @param name
	 * @param group
	 * @return
	 */
	boolean checkExists(String name, String group) {
		JobKey key = new JobKey(name, group);// 注意group name
		try {
			return getScheduler().checkExists(key);
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}

		return false;
	}

	/**
	 * 仅仅是job的name，并不是jobkey，与group无关
	 * 
	 * @param jobName
	 * @return
	 */
	String getJobName(String jobName) {
		if (isNullOrEmpty(jobName))
			return "job_" + COUNTER.incrementAndGet();
		else
			return jobName;
	}

	String getTriggerName(String name) {
		return "trigger_for_" + name;
	}
}
