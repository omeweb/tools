package tools;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 如果类只需要初始化一次，则可继承该类
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-8-5
 */
public abstract class InitializeOnce {
	private boolean initialized = false; // java 5之后可以不用volatile
	private final Lock lock = new ReentrantLock();
	protected final long setupTimeMillis = System.currentTimeMillis();// 2014-12-09 by 六三

	/**
	 * 是否初始化OK
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		return initialized;
	}

	public final void init() {
		if (initialized) {
			return;
		}

		// 注意和finally对应，防止出错后无法unlock
		lock.lock();
		try {
			if (initialized)// 双重判断。被lock后，所有变量的值都会重新去内存里取最新的
				return;

			doInitialize();
			initialized = true;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 不要多次调用
	 */
	protected abstract void doInitialize();
}

// 2013-08-05 20:29:10,250 ERROR ExceptionHandlerImpl - tp_invoke:sfqz.1859 java.lang.NullPointerException
// 2013-08-05 20:29:10,259 WARN ElementChecker - BloomFilter initialized cost : 38
