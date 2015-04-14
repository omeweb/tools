package tools.test.quartz;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import tools.quartz.JobScheduler;

public class CronTest {
	@Test
	public void nextTest() throws Throwable {
		System.out.println(tools.Convert.toString(new Date()));
		
		List<Date> list = JobScheduler.getNextValidTimes("0 0/1 * * * ? *", -1, 1);
		for (Date item : list) {
			System.out.println(tools.Convert.toString(item));
		}
	}
}
