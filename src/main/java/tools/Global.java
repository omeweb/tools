package tools;

//import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import sun.misc.Unsafe;
import tools.Context;
import tools.counter.AtomicKeyedCounter;
import tools.counter.MultiKeyedCounter;

/**
 * 使用静态方法，方便使用，spring bean，注意init-method = init
 * 
 * @author liusan.dyf
 */
public class Global extends tools.InitializeOnce {
	/**
	 * 2014-10-22 by liusan.dyf，保存此次发布的版本号
	 */
	public static int V = 0;

	// private static EventContainer eventContainer = null;

	public static final String LOCAL_IP = tools.StringUtil.getLocalIp();// 本机IP

	private static final Log logger = LogFactory.getLog("system");// 日志

	private static Map<String, Object> settings;// 全局设置

	// /**
	// * 哈勃日志，目前供pfp项目使用
	// */
	// private static final Log MONITOR_LOGGER = LogFactory.getLog("monitor");

	/**
	 * 系统计数器 2012-02-16
	 */
	private static MultiKeyedCounter counter = createCounters();

	/**
	 * 2012-02-28 by liusan.dyf
	 */
	private static Context<User> contextStore = new Context<User>();

	public static void destroy() {
		;
	}

	// /**
	// * 2014-12-13 by 六三，操作堆外内存（off-heap memory），详细说明：http://ifeve.com/sun-misc-unsafe/
	// *
	// * @return
	// */
	// public static Unsafe getUnsafe() {
	// Field f;
	// try {
	// f = Unsafe.class.getDeclaredField("theUnsafe");
	// f.setAccessible(true);
	// return (Unsafe) f.get(null);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// // 操作对外内存 http://robaustin.wikidot.com/how-to-write-to-direct-memory-locations-in-java
	//
	// return null;
	// }

	public static Map<String, Object> getSettings() {
		if (settings == null)
			settings = tools.MapUtil.create();// 2014-01-16 by liusan.dyf

		return settings;
	}

	/**
	 * 2014-03-07 by liusan.dyf
	 * 
	 * @param v
	 */
	public static void setSettings(Map<String, Object> v) {
		if (v == null)
			return;

		settings = v;
	}

	/**
	 * 2014-03-07 by liusan.dyf
	 * 
	 * @param v
	 */
	public static void attachSettings(Map<String, Object> v) {
		if (v == null)
			return;

		getSettings().putAll(v);
	}

	/**
	 * 2012-02-16 by liusan.dyf
	 * 
	 * @return
	 */
	public static MultiKeyedCounter getCounter() {
		return counter;
	}

	/**
	 * 2013-03-07 by liusan.dyf 合并计数器
	 * 
	 * @param m
	 */
	public static void mergeCounters(Map<String, Object> m) {
		if (m == null)
			return;

		Set<Entry<String, Object>> set = m.entrySet();

		for (Entry<String, Object> item : set) {
			counter.increment(item.getKey(), tools.Convert.toLong(item.getValue(), 0));
		}

		logger.warn("mergeCounters:" + m.size());
	}

	/**
	 * 2013-08-28 by liusan.dyf
	 * 
	 * @return
	 */
	public Map<String, Long> getCounterMapAndReset() {
		Map<String, Long> m = getCounter().setAll(0);
		return m;
	}

	public static MultiKeyedCounter createCounters() {
		return new AtomicKeyedCounter();
	}

	public static User getCurrentUser() {
		return contextStore.get();
	}

	public static void removeCurrentUser() {
		contextStore.remove();
	}

	/**
	 * 2012-02-29
	 * 
	 * @param u
	 */
	public static void setCurrentUser(User u) {
		contextStore.set(u);
	}

	/**
	 * 动态方法，在spring bean的xml配置里初始化使用
	 */
	@Override
	protected void doInitialize() {
		// logger.warn("开始初始化global");

		// ===初始化工作
		// if (eventContainer != null)
		// eventContainer.onEvent(this, EventArgs.create(this).setType("global.before-init"));
	}

	/**
	 * 2014-04-20 by liusan.dyf
	 * 
	 * @param v
	 */
	public static void println(Object v) {
		System.out.println(v);
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {

		}
	}

	/**
	 * 2014-11-20 by 六三，默认是warn的方式打印出来
	 * 
	 * @param loggerName
	 * @param content
	 */
	public static void log(String loggerName, Object content) {
		org.apache.commons.logging.LogFactory.getLog(loggerName).warn(content);
	}

	// public static EventContainer getEventContainer() {
	// return eventContainer;
	// }
	//
	// public static void setEventContainer(EventContainer v) {
	// eventContainer = v;
	// }
}
