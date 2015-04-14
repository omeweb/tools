package tools.code;

public class RunTimer {
	/**
	 * 运行
	 * 
	 * @param name
	 * @param times
	 * @param job
	 * @return
	 */
	public long run(String name, int times, Runnable job) {
		if (times <= 0) // fix 2011-11-02 by 63
			return 0;

		// long start = System.currentTimeMillis();
		long startNano = System.nanoTime();

		for (int i = 0; i < times; i++)
			job.run();

		// long rtn = System.currentTimeMillis() - start;
		Double rtn = (System.nanoTime() - startNano) * 1e-6;

		System.out.println(name + " for " + times + " times");
		System.out.println("all time:	" + rtn);
		System.out.println("per time:	" + rtn / times + "\n");// 2014-12-23 10:36:26 by 六三

		return rtn.longValue();
	}
}
