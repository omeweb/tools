package tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;

/**
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2015年4月27日
 */
public class BeanUtil {
	private static final Map<String, BeanCopier> map = new ConcurrentHashMap<String, BeanCopier>();

	/**
	 * 2个bean直接相同的属性名之间做copy
	 * 
	 * @param srcObj
	 * @param destObj
	 */
	public static void copy(Object srcObj, Object destObj) {
		String key = getKey(srcObj.getClass(), destObj.getClass());
		BeanCopier copier = null;
		
		if (!map.containsKey(key)) {
			synchronized (map) {
				if (!map.containsKey(key)) {
					copier = BeanCopier.create(srcObj.getClass(), destObj.getClass(), false);
					map.put(key, copier);
				}
			}
		}

		if (copier == null) {
			copier = map.get(key);
		}

		copier.copy(srcObj, destObj, null);
	}

	/**
	 * 把map里的kv赋值到同属性名的bean上
	 * 
	 * @param destObj
	 * @param m
	 */
	public static void fill(Object destObj, Map<String, Object> m) {
		if (destObj == null || m == null || m.size() == 0)
			return;

		BeanMap bm = BeanMap.create(destObj);
		bm.putAll(m);

		// java.util.Set<String> keys = m.keySet();
		// for (String item : keys) {
		// bm.put(item, m.get(item));// 类型不匹配时会抛出 java.lang.ClassCastException:
		// }
	}

	private static String getKey(Class<?> srcClazz, Class<?> destClazz) {
		return srcClazz.getName() + destClazz.getName();
	}
}
