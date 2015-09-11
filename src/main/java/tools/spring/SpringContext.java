package tools.spring;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 使用静态方法，方便使用，记得该类要被spring创建一次
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-2-28
 */
public class SpringContext implements ApplicationContextAware {

	/**
	 * 可以存储多个ApplicationContext 2013-02-28 by liusan.dyf
	 */
	private static Map<String, ApplicationContext> all = tools.MapUtil.concurrentHashMap();
	private static Map<String, Object> cachedBeans = tools.MapUtil.concurrentHashMap();// 2015-9-11 10:33:32 by 一篑

	/**
	 * 如果该name没有配置，则返回null；不做containsBean判断则报错：NoSuchBeanDefinitionException
	 * 
	 * @param name
	 * @return
	 */
	public static Object getBean(String name) {
		if (cachedBeans.containsKey(name)) {// ApplicationContext.getBean内部有锁，这里做个缓存 2015-9-11 10:33:32 by 一篑
			return cachedBeans.get(name);
		}

		for (ApplicationContext item : all.values()) {
			// if (item.containsBean(name))
			// return item.getBean(name);

			if (item.containsBean(name)) {
				Object bean = item.getBean(name);
				cachedBeans.put(name, bean);// 放入缓存 2015-9-11 10:31:54 by 一篑
				return bean;
			}
		}

		return null;
	}

	/**
	 * 2012-03-05 by liusan.dyf
	 * 
	 * @param t
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> t, String name) {
		Object obj = getBean(name);
		if (obj == null)
			return null;
		return (T) obj;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		synchronized (all) {
			all.put(applicationContext.getId(), applicationContext);
		}
	}
}
