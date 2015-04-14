package tools.cache;

/**
 * 缓存项 http://whirlycache.googlecode.com/svn/trunk/src/java/com/whirlycott/cache/ 有参考价值
 * 
 * @author liusan.dyf
 */
public class CacheItem {
	private Object value;
	private long cutoffTime;

	public CacheItem(Object value, long ttl) {
		this.value = value;
		this.cutoffTime = ttl;
	}

	public long getCutoffTime() {
		return cutoffTime;
	}

	public void setCutoffTime(long cutoffTime) {
		this.cutoffTime = cutoffTime;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
