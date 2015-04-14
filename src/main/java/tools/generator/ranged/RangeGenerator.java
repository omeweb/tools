package tools.generator.ranged;

/**
 * 根据key来生成一个id范围
 * 
 * @author liusan.dyf
 */
public interface RangeGenerator {
	/**
	 * 确保线程安全，且每次的range都不一样 2012-11-20 by liusan.dyf
	 * 
	 * @param key
	 * @return
	 */
	Range next(String key);

	/**
	 * 得到当前的range，2012-01-04
	 * 
	 * @param key
	 * @return
	 */
	Range current(String key);

	/**
	 * 2012-02-12，保存区间，给current使用
	 * 
	 * @param key
	 * @return
	 */
	boolean save(String key, Range g);
}
