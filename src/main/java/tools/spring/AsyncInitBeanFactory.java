package tools.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * idea和部分代码来自【书全】<br />
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2014年12月9日
 */
public class AsyncInitBeanFactory extends DefaultListableBeanFactory {
	protected static final int DEFAULT_POOL_SIZE = 2;// 默认线程池大小
	private static final Log logger = LogFactory.getLog("spring");// 日志

	private long setupTimeMillis = 0;// 2014-12-09 by 六三

	private ExecutorService executor = null;//
	private List<Future<?>> list = null;

	public AsyncInitBeanFactory() {
		this(null, DEFAULT_POOL_SIZE);
	}

	public AsyncInitBeanFactory(BeanFactory parentBeanFactory, int poolSize) {
		super(parentBeanFactory);

		if (poolSize <= 0)
			poolSize = DEFAULT_POOL_SIZE;

		// System.out.println(this.getClass());

		setupTimeMillis = System.currentTimeMillis();
		executor = Executors.newFixedThreadPool(poolSize);
		list = new ArrayList<Future<?>>();

		// http://gitlab.alibaba-inc.com/jhaolee/async-init-spring/blob/master/src/main/java/com/alibaba/trade/async/spring/beans/factory/AsyncInitBeanFactory.java
	}

	/**
	 * 系统启动入口 2014-12-09 by 六三
	 * 
	 * @param locations
	 * @param n
	 * @return
	 */
	public static ApplicationContext initBeans(String[] locations, final int n) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(locations) {
			private AsyncInitBeanFactory beanFactory;

			/**
			 * 这里要重写 createBeanFactory
			 */
			@Override
			protected DefaultListableBeanFactory createBeanFactory() {
				beanFactory = new AsyncInitBeanFactory(getParentBeanFactory(), n);
				return beanFactory;
			}

			@Override
			protected void finishRefresh() {
				beanFactory.finish();// 必须
				super.finishRefresh();
			}
		};

		// System.out.println(ctx.getStartupDate());
		return ctx;
	}

	@Override
	protected void invokeCustomInitMethod(final String beanName, final Object bean, final RootBeanDefinition mbd)
		throws Throwable {
		// System.out.println("class=" + bean.getClass() + ", method=" + mbd.getInitMethodName());

		// super.invokeCustomInitMethod(beanName, bean, mbd);

		// 用线程池异步初始化
		Future<?> f = executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					doInvokeCustomInitMethod(beanName, bean, mbd);
				} catch (Throwable e) {
					logger.error(e);
				}
			}
		});

		list.add(f);
	}

	private void doInvokeCustomInitMethod(final String beanName, final Object bean, final RootBeanDefinition mbd)
		throws Throwable {
		// long id = Thread.currentThread().getId();
		String info = beanName + "." + mbd.getInitMethodName();

		// logger.warn("begin to invoke " + info + " by " + id);

		long start = System.currentTimeMillis();

		// 真正的开始调用
		super.invokeCustomInitMethod(beanName, bean, mbd);

		logger.warn("invoke " + info + ", cost " + (System.currentTimeMillis() - start));
	}

	public boolean finish() {
		if (list.size() == 0)
			return false;

		try {
			for (Future<?> task : list) {
				Object result = task.get();
				if (result != null) {
					// TODO
				}
			}
		} catch (Throwable e) {
			if (e instanceof BeanCreationException) {
				throw (BeanCreationException) e;
			} else {
				throw new BeanCreationException(e.getMessage(), e);
			}
		}

		// 记录耗时 2014-12-09 by 六三
		logger.warn(list.size() + " beans async invoke cost: " + (System.currentTimeMillis() - setupTimeMillis));

		// 清理
		list.clear();
		list = null;
		executor.shutdown();
		executor = null;

		return true;
	}

	// @Override
	// protected void invokeInitMethods(final String beanName, final Object bean, final RootBeanDefinition mbd)
	// throws Throwable {
	// super.invokeInitMethods(beanName, bean, mbd);
	// }
}
