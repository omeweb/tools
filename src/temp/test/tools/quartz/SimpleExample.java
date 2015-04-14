/* 
 * Copyright 2005 - 2009 Terracotta, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */

package tools.test.quartz;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import tools.quartz.JobScheduler;

/**
 * This Example will demonstrate how to start and shutdown the Quartz scheduler and how to schedule a job to run in
 * Quartz.
 * 
 * @author Bill Kratzer
 */
public class SimpleExample {

	public static AtomicInteger atomicInteger = new AtomicInteger(0);

	public void runV2() throws SchedulerException, InterruptedException {

		// 创建工厂
		SchedulerFactory sf = new StdSchedulerFactory();

		Scheduler sched = sf.getScheduler();

		// 创建JobScheduler，设置调度器
		JobScheduler jobScheduler = new JobScheduler(sched);

		// 创建一批job
		for (int i = 0; i < 1000; i++) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("id", i);
			jobScheduler.addJob(new Date(), HelloJob.class, data, null);
		}

		String jobName = "cc";

		// 创建周期性job
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", -1);
		jobScheduler.addJob(new Date(), HelloJob.class, data, jobName, 1, -1);

		// 任务重复了，添加失败
		boolean f = jobScheduler.addJob(new Date(), HelloJob.class, null, jobName, 1, -1);
		System.out.println(f + "");

		// 开始做
		jobScheduler.start();

		// 得到任务数据并修改
		Thread.sleep(200);
		JobDataMap jobData = jobScheduler.getJobData(jobName);
		if (jobData != null) {
			System.out.println(jobData.get("id").toString());
			jobData.put("id", -2);// 修改是无效的
			jobScheduler.setJobData(jobName, jobData);
		}

		// jobScheduler.getScheduler().pauseAll();

		// 删除job
		Thread.sleep(600);
		jobScheduler.deleteJob(jobName);

		// sleep
		Thread.sleep(10L * 1000L);

		// 看看目前还有多少任务还在做 2012-02-17
		System.out.println(jobScheduler.getScheduler().getCurrentlyExecutingJobs().size());

		// 关闭
		jobScheduler.shutdown(true);

		// 看看执行了多少job
		System.out.println(atomicInteger.get() + "");
	}

	public static void main(String[] args) throws Exception {
		SimpleExample example = new SimpleExample();
		example.runV2();
	}
}
