package tools.redis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import tools.StringUtil;

/**
 * 2012-02-21
 * 
 * @author liusan.dyf
 */
public class RedisUtil {

	private static final Log LOGGER = LogFactory.getLog("redis");

	/**
	 * 2012-02-14 by liusan.dyf，用于产生redispool <br />
	 * eg. redis://user:pass@127.0.0.1:6379/?max_active=10&max_idle=20&max_wait= 1000&timeout=5000
	 * 
	 * @param uriString
	 * @return
	 * @throws URISyntaxException
	 */
	public static JedisPool createRedisPool(String uriString) throws URISyntaxException {
		LOGGER.debug("uriString：" + uriString);

		// redis://user:pass@127.0.0.1:6379/?max_active=10&max_idle=20&max_wait=1000&timeout=5000
		URI uri = new URI(uriString);
		String ip = uri.getHost();// ip

		int port = uri.getPort();
		String auth = uri.getUserInfo();// user:pass

		// 解析密码
		String pass = null;
		if (!tools.StringUtil.isNullOrEmpty(auth)) {
			int index = auth.indexOf(':');
			if (index > -1)
				pass = auth.substring(index + 1);

			// 2012-02-15 由于jedis驱动的问题，密码为""也会发送过去，必须为null
			if (tools.StringUtil.isNullOrEmpty(pass))
				pass = null;
		}
		String queryString = uri.getRawQuery();// getRawQuery未解码

		// 选项
		Map<String, String> options = StringUtil.parseQueryString(queryString, "utf-8");
		int max_active = tools.Convert.toInt(options.get("max_active"), 10);
		int max_idle = tools.Convert.toInt(options.get("max_idle"), 20);
		int max_wait = tools.Convert.toInt(options.get("max_wait"), 1000);
		int timeout = tools.Convert.toInt(options.get("timeout"), 2000);

		// 2012-02-14
		LOGGER.debug(String.format("redis-pool-config：ip：%s，max_active：%d，max_idle：%d，max_wait：%d，timeout：%d", ip,
				max_active, max_idle, max_wait, timeout));

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(max_active);
		config.setMaxIdle(max_idle);
		config.setMaxWait(max_wait);
		config.setTestOnBorrow(true);

		return new JedisPool(config, ip, port, timeout, pass);
	}
}
