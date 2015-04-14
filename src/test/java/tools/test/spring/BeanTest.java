package tools.test.spring;

import tools.spring.AsyncInitBeanFactory;

public class BeanTest {
	static {
		// System.out.println("...");
		String[] locations = { "classpath:applicationContext.xml" };

		AsyncInitBeanFactory.initBeans(locations, 4);
	}

	public static void main(String[] args) {
		Object bean = tools.spring.SpringContext.getBean("slowBean");
		System.out.println(bean == null);

		bean = tools.spring.SpringContext.getBean("slowBean_2");
		System.out.println(bean == null);

		// tools.Global.sleep(1000 * 2);
	}
}
