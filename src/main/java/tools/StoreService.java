package tools;

/**
 * 单一存储器，并非KEY-VALUE结构，比如ThreadLocal类似的
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-12-9
 */
public interface StoreService<T> {
	/**
	 * 删除
	 */
	void remove();

	/**
	 * 存储介质的存储string，比如cookie里、threadlocal里等
	 * 
	 * @param value
	 * @param seconds
	 */
	void set(T value, int seconds);

	/**
	 * 从存储介质里获取序列化的string值，比如从cookie里获取
	 * 
	 * @return
	 */
	T get();
}
