package tools.spring;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 统计每个bean的创建时间，直接配置即可：
 * 
 * <pre>
 * bean class="tools.SpringBeanPostPrcessor"
 * 
 * BeanPostPrcessorImpl beanPostProcessor = new BeanPostPrcessorImpl();
 * Resource resource = new FileSystemResource("applicationContext.xml");
 * ConfigurableBeanFactory factory = new XmlBeanFactory(resource);
 * factory.addBeanPostProcessor(beanPostProcessor);
 * factory.getBean("logic");
 * </pre>
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2014年9月4日
 */
public class SpringBeanPostPrcessor implements BeanPostProcessor {
	private static final Log logger = LogFactory.getLog("spring");

	/**
	 * 初始状态，value=0-时间戳，为负数；如果初始化OK后，转正，为init耗时情况
	 */
	private Map<String, Long> map = tools.MapUtil.concurrentHashMap();

	private AtomicInteger counter = new AtomicInteger(0);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		counter.incrementAndGet();
		map.put(beanName, 0 - System.currentTimeMillis());
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		counter.incrementAndGet();

		Long x = map.remove(beanName);// 移除
		long v = 0;

		if (x != null) {
			if (x < 0) {
				v = System.currentTimeMillis() - (0 - x);

				logger.warn("bean=" + beanName + ", counter=" + counter.get() + ", cost=" + v);
			}
		} else
			logger.warn("bean=" + beanName + " time cost not found, counter=" + counter.get());

		return bean;
	}
}
