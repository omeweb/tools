package tools.cache;

import java.util.Map;

/**
 * 2012-06-05
 * 
 * @author liusan.dyf
 */
public interface CacheProvider {
	/**
	 * 单位为秒
	 * 
	 * @param key
	 * @param value
	 * @param ttl
	 */
	void set(String key, Object value, int ttl);

	/**
	 * 清除key，返回老的结果。不存在或者过期，则返回null
	 * 
	 * @param key
	 * @return
	 */
	Object remove(String key);

	/**
	 * 获取原始缓存的内容
	 * 
	 * @param key
	 * @return
	 */
	Object get(String key);

	int size();

	Map<String, Object> getAll();
}
