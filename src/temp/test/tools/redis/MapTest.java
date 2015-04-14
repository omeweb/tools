package tools.test.redis;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import org.junit.Test;

import redis.clients.jedis.JedisPool;
import tools.redis.RedisMap;
import tools.redis.RedisUtil;

public class MapTest {

	private static JedisPool pool;

	static {
		try {
			pool = createPool();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	static JedisPool createPool() throws URISyntaxException {
		return RedisUtil
				.createRedisPool("redis://10.13.42.36:6379/?max_active=10&max_idle=5&max_wait=1000&timeout=1000&testKey=testValue");
	}

	@Test
	public void putAllTest() {
		RedisMap m = new RedisMap("m", pool);
		m.clear();

		Map<String, String> to = new HashMap<String, String>();
		to.put("a", "1");

		m.putAll(to);

		Assert.assertEquals(m.size(), 1);
	}

	@Test
	public void toArrayTest() {
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");

		String[] arr = (String[]) list.toArray(new String[0]);
		Assert.assertEquals(list.size(), arr.length);
	}
}
