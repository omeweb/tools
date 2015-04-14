package tools.counter;

import java.util.Map;

/**
 * multivalued，除了get方法外，其他返回int的都表示是操作后的结果 2012-02-16
 * 
 * @author liusan.dyf
 */
public interface MultiKeyedCounter {
	/**
	 * 获取当前的值
	 * 
	 * @param key
	 * @return
	 */
	long get(String key);

	/**
	 * 获取当前已经存储的所有的计数器
	 * 
	 * @return
	 */
	Map<String, Long> getAll();

	/**
	 * 设置新值，并返回旧值
	 * 
	 * @param key
	 * @param v
	 * @return
	 */
	long set(String key, long v);

	/**
	 * 设置全部的计数器以新值，并返回旧值
	 * 
	 * @param v
	 * @return
	 */
	Map<String, Long> setAll(long v);

	/**
	 * 计数器加1，返回新值
	 * 
	 * @param key
	 * @return
	 */
	long increment(String key);

	/**
	 * 计数器减1，返回新值
	 * 
	 * @param key
	 * @return
	 */
	long decrement(String key);

	/**
	 * 计数器加v，返回新值
	 * 
	 * @param key
	 * @param v
	 * @return
	 */
	long increment(String key, long v);

	/**
	 * 计数器减v，返回新值
	 * 
	 * @param key
	 * @param v
	 * @return
	 */
	long decrement(String key, long v);
}
