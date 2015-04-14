package tools.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 如果某个缓存项不被访问，则可能永远都不会被清除，直到被再次调用；否则需要借助第三方程序 2012-06-05 <br />
 * 默认用WeakHashMap实现 2012-09-26 <br />
 * 注意使用ConcurrentHashMap
 * 
 * @author liusan.dyf
 */
public class HashMapCacheProvider implements CacheProvider {

	private static final int FOREVER = -1;

	private Map<String, CacheItem> container = null;
	private Lock locker = new ReentrantLock();

	public HashMapCacheProvider(Map<String, CacheItem> container) {
		this.container = container;
	}

	public HashMapCacheProvider() {
		this(new WeakHashMap<String, CacheItem>());
	}

	/**
	 * 2012-09-26 by liusan.dyf
	 * 
	 * @return
	 */
	public List<String> getAllKeys() {
		return new ArrayList<String>(container.keySet());
	}

	/**
	 * 单位为秒，-1表示永不过期 2012-09-26
	 */
	public void set(String key, Object value, int ttl) {
		locker.lock();
		try {
			long t = 0;
			if (ttl == FOREVER)
				t = FOREVER;
			else
				t = ttl * 1000 + currentTimeMillis();

			// 溢出保护 2012-09-26 by liusan.dyf
			if (t < 0 && t != FOREVER)
				t = Long.MAX_VALUE;

			CacheItem entry = new CacheItem(value, t);
			getContainer().put(key, entry);
		} finally {
			locker.unlock();
		}
	}

	public Map<String, Object> getAll() {
		Map<String, Object> rtn = new HashMap<String, Object>(getContainer().size());

		// 遍历ConcurrentHashMap，线程安全，如果是普通的hashmap，则线程不安全 2013-11-04 by liusan.dyf
		Set<Entry<String, CacheItem>> set = getContainer().entrySet();

		for (Entry<String, CacheItem> item : set) {
			CacheItem entry = item.getValue();
			if (!isLive(entry.getCutoffTime()))// 过期的给删除
				remove(item.getKey());
			else
				rtn.put(item.getKey(), entry.getValue());// 未过期的返回
		}

		return rtn;
	}

	public Object remove(String key) {
		locker.lock();
		try {
			CacheItem entry = getContainer().remove(key);

			if (entry != null)
				return entry.getValue();

			return null;
		} finally {
			locker.unlock();
		}
	}

	public int size() {
		locker.lock();
		try {
			return getContainer().size();
		} finally {
			locker.unlock();
		}
	}

	public Object get(String key) {
		CacheItem item = null;
		locker.lock();
		try {
			item = getContainer().get(key);
		} finally {
			locker.unlock();
		}

		if (item == null)// 不包含该key
			return null;
		else {
			if (!isLive(item.getCutoffTime())) {
				// 已经过期了，要清除掉
				remove(key);
			} else
				return item.getValue();
		}

		return null;
	}

	private long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public Map<String, CacheItem> getContainer() {
		return container;
	}

	public void setContainer(Map<String, CacheItem> container) {
		this.container = container;
	}

	private boolean isLive(long t) {
		if (t == FOREVER)
			return true;
		else
			return t >= currentTimeMillis();
	}

	public static void main(String[] args) {
		System.out.println(Integer.MAX_VALUE);
	}
}
