package tools.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 2012-03-06
 * 
 * @author liusan.dyf
 */
public class RedisMap implements Map<String, String> {

	private JedisPool pool;
	private String name;

	public RedisMap(String name, JedisPool pool) {
		this.name = name;
		this.pool = pool;
	}

	public List<String> popAll() {
		// 为什么不能getAll()然后再clear，因为在并发情况下，getAll操作完成后，队列里还有可能已经插入了新的元素

		// 1，得到此刻所有的key
		Set<String> keys = this.keySet();

		// 2，根据这些key拉取所有的值
		List<String> list = getAll(keys);

		// 3，删除这些key
		removeAll(keys);

		// 4，返回结果
		return list;
	}

	public List<String> getAll(Collection<String> keys) {
		Jedis client = pool.getResource();
		try {
			String[] arr = (String[]) keys.toArray(new String[0]);
			return client.hmget(name, arr);
		} finally {
			pool.returnResource(client);
		}
	}

	public int removeAll(Collection<String> keys) {
		Jedis client = pool.getResource();
		try {
			// 暂时还不支持hmdel命令
			// String[] arr = (String[]) keys.toArray(new String[0]);
			// return client.hmdel(name, arr);

			int i = 0;
			for (String item : keys)
				i += client.hdel(name, item);

			return i;
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public int size() {
		Jedis client = pool.getResource();
		try {
			long i = client.hlen(name);
			return (int) i;
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		Jedis client = pool.getResource();
		try {
			return client.hexists(name, key.toString());
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		throw new NotImplementedException();
	}

	@Override
	public String get(Object key) {
		Jedis client = pool.getResource();
		try {
			return client.hget(name, key.toString());
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public String put(String key, String value) {
		Jedis client = pool.getResource();
		try {
			client.hset(name, key, value);
			return value;
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public String remove(Object key) {
		Jedis client = pool.getResource();
		try {
			String rtn = get(key);// 返回被删除的元素
			client.hdel(name, key.toString());
			return rtn;
		} finally {
			pool.returnResource(client);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		Jedis client = pool.getResource();
		try {
			// for (String item : m.keySet()) {
			// client.hset(name, item, m.get(item));
			// }

			client.hmset(name, (Map<String, String>) m);
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public void clear() {
		Jedis client = pool.getResource();
		try {
			client.del(name);
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public Set<String> keySet() {
		Jedis client = pool.getResource();
		try {
			return client.hkeys(name);
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public Collection<String> values() {
		Jedis client = pool.getResource();
		try {
			return client.hvals(name);
		} finally {
			pool.returnResource(client);
		}
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		Jedis client = pool.getResource();
		try {
			return client.hgetAll(name).entrySet();
		} finally {
			pool.returnResource(client);
		}
	}

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
