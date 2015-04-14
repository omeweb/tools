package tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MapUtil {
	/**
	 * 2013-11-21 by liusan.dyf
	 * 
	 * @param m
	 * @param handler
	 * @return
	 */
	public static <TKey, TValue> Map<TKey, TValue> where(final Map<TKey, TValue> m, final Predicate<TKey> handler) {
		if (m == null)
			return null;
		if (handler == null)
			return m;

		final Map<TKey, TValue> result = create(m.size());

		// // 遍历查找，支持多线程
		// TKey key = null;
		// Iterator<TKey> iterator = m.keySet().iterator();
		// while (iterator.hasNext()) {
		// key = (TKey) iterator.next();
		//
		// // 判断key是否满足条件
		// if (handler.execute(key)) {
		// result.put(key, m.get(key));
		// }
		// }

		eachKey(m, new Action<TKey>() {
			@Override
			public void execute(TKey t) {
				if (handler.execute(t))
					result.put(t, m.get(t));
			}
		});

		return result;
	}

	public static <TKey, TValue> TValue findOneValue(Map<TKey, TValue> m, Predicate<TValue> handler) {
		if (m == null)
			return null;
		if (handler == null)
			return null;

		// 遍历查找，支持多线程
		TKey key = null;
		TValue value = null;
		Iterator<TKey> iterator = m.keySet().iterator();
		while (iterator.hasNext()) {
			key = (TKey) iterator.next();
			value = m.get(key);

			if (handler.execute(value)) {
				return value;
			}
		}

		return null;
	}

	public static <TKey, TValue> void eachKey(Map<TKey, TValue> m, Action<TKey> handler) {
		if (m == null)
			return;
		if (handler == null)
			return;

		// 遍历查找，支持ConcurrentMap多线程
		TKey key = null;
		Iterator<TKey> iterator = m.keySet().iterator();
		while (iterator.hasNext()) {
			key = (TKey) iterator.next();
			handler.execute(key);
		}
	}

	public static <TKey, TValue> void eachValue(Map<TKey, TValue> m, Action<TValue> handler) {
		if (m == null)
			return;
		if (handler == null)
			return;

		// 遍历查找，支持ConcurrentMap多线程
		TKey key = null;
		TValue value = null;
		Iterator<TKey> iterator = m.keySet().iterator();
		while (iterator.hasNext()) {
			key = (TKey) iterator.next();
			value = m.get(key);

			handler.execute(value);
		}
	}

	/**
	 * 2012-05-09 by liusan.dyf
	 * 
	 * @return
	 */
	public static <K, V> HashMap<K, V> create() {
		// return new HashMap<K, V>();
		return create(8);
	}

	public static <K, V> HashMap<K, V> create(int initialCapacity) {
		return new HashMap<K, V>(initialCapacity);
	}

	public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap() {
		return concurrentHashMap(8);
	}

	/**
	 * 2013-08-07 by liusan.dyf
	 * 
	 * @param initialCapacity
	 * @return
	 */
	public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(int initialCapacity) {
		return new ConcurrentHashMap<K, V>(initialCapacity);
	}

	/**
	 * map反转 2013-04-26 by liusan.dyf
	 * 
	 * @param originalMap
	 * @return
	 */
	public static <K, V> Map<V, K> reverse(Map<K, V> originalMap) {
		if (originalMap == null)
			return null;

		HashMap<V, K> reverseMap = new HashMap<V, K>();
		Set<?> set = originalMap.entrySet();
		Iterator<?> it = set.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
			reverseMap.put(entry.getValue(), entry.getKey());
		}
		return reverseMap;
	}

	/**
	 * 2014-04-30 by liusan.dyf
	 * 
	 * @param map
	 * @param separator KV直接的连接符
	 * @param keyValueSeparator KV对直接的连接符
	 * @return
	 */
	public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator) {
		if (map == null)
			return null;

		if (keyValueSeparator == null)
			keyValueSeparator = "";

		StringBuilder sb = new StringBuilder();

		Set<?> set = map.entrySet();
		Iterator<?> it = set.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
			sb.append(entry.getKey());
			sb.append(separator);
			sb.append(entry.getValue());
			sb.append(keyValueSeparator);
		}

		// 去掉末尾的s2
		int len = sb.length();
		if (len > 0) {
			sb.delete(len - keyValueSeparator.length(), len);
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		Map<String, String> m = create();// createMap自动推导出KV的类型
		m.put("key", null);
		m.put("a", "1");
		m.put("b1", "2");
		m.put("b2", "3");

		// 测试反转
		System.out.println(reverse(m));

		System.out.println(join(m, "=", "&"));

		// 测试where 2013-11-21 by liusan.dyf
		Map<String, String> sub = where(m, new Predicate<String>() {

			@Override
			public boolean execute(String t) {
				return t.startsWith("b");
			}
		});

		System.out.println(sub);
	}
}

// 当使用泛型类时，必须在创建对象的时候指定类型参数的值，而使用泛型方法的时候，通常不必指明参数类型，因为编译器会为我们找出具体的类型。这称为类型参数推断（type
// argument inference）

// http://hi.baidu.com/中国山东人2009/blog/item/65b3e28f230c8c07c8fc7abc.html
