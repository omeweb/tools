package tools.generator;

/**
 * Numberic
 * 
 * @author liusan.dyf
 */
public interface IdGenerator {
	long current();

	/**
	 * 会在程序里预先调用，所以提取为接口的一个方法
	 */
	void init();

	long next();

	/**
	 * 返回【起始id:增量:次数】，包含起始id 适合大批量情况下
	 * 
	 * @param count
	 * @return
	 */
	String next(int count);

	/**
	 * 2012-02-12
	 * 
	 * @return
	 */
	String getKey();

	/**
	 * 生成的id从value + increment开始
	 * 
	 * @param value
	 */
	void setInitialValue(long value);

	/**
	 * 得到增长的步长 2012-11-14 by liusan.dyf
	 * 
	 * @return
	 */
	int getIncrement();
}
