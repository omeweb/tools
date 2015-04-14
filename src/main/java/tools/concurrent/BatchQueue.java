package tools.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tools.Predicate;

/**
 * 延迟、批量队列；一旦队列停止了，就无法再启用，因为负责拉取队列数据的线程已经销毁了 2014-05-21
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2014-3-22
 */
public class BatchQueue<T> extends tools.InitializeOnce {
	private static final Log logger = LogFactory.getLog("system");

	private BlockingQueue<T> q = new LinkedBlockingQueue<T>();// 阻塞队列
	private int batchSize = 100;// 批次大小
	private int timeout = 10;// 超时时间
	private String name = null;// 名称
	private Thread workingThread = null;// 执行的线程
	private Predicate<List<T>> callback = null;// 批次的回调
	private boolean working = true;// 是否还在工作 2014-04-17

	@Override
	protected void doInitialize() {
		// 检查参数
		if (batchSize <= 0)
			throw new IllegalArgumentException("batchSize不能小于等于0");

		if (getTimeout() <= 0)
			throw new IllegalArgumentException("timeout不能小于等于0");

		if (name == null)
			throw new IllegalArgumentException("name不能为null");

		if (callback == null)
			throw new IllegalArgumentException("callback不能为null");

		// 创建线程
		workingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!working)
						break;// 2014-04-17 by liusan.dyf

					int nums = 0;// 数

					List<T> list = new ArrayList<T>(batchSize);

					boolean isTimeout = false;
					for (int i = 0; i < getBatchSize(); i++) {
						try {
							T entry = q.poll(getTimeout(), TimeUnit.MILLISECONDS);// 这里有超时
							if (entry != null) {
								nums++;
								list.add(entry);
							} else {
								// 取不到数据了，立即提交 2014-02-24 by liusan.dyf
								break;
							}
						} catch (InterruptedException e) {
							logger.error("poll error", e);
							// break;// 等一段时间拉取不到，就提交 2014-01-21 by liusan.dyf

							isTimeout = true;
						}

						if (isTimeout)
							break;// 跳出for
					}// end for

					// 开始提交
					if (nums == 0)
						continue;

					// 注意try-catch，可能会执行失败
					try {
						// long startMs = System.currentTimeMillis();
						boolean f = callback.execute(list);

						if (!f) {
							// TODO 执行失败了
						} else {
							// logger.warn("BatchQueueExecute:nums=" + nums + ",left=" + q.size()); // 2014-07-14
						}
						// logger.warn("成功execute,took=" + (System.currentTimeMillis() - startMs) + ",size=" + nums);
					} catch (Throwable e) {
						// logger.error("batch execute error", e);
					}

				}// end while true
			}
		});

		workingThread.setName(getName() + "_working_thread");
		workingThread.setDaemon(true);
		workingThread.start();
	}

	public void insert(T entry) {
		checkStatus();

		if (entry == null)
			return;

		try {
			q.put(entry);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void insert(List<T> list) {
		checkStatus();

		if (list == null)
			return;

		for (T item : list)
			insert(item);
	}

	private void checkStatus() {
		if (!this.working)
			throw new RuntimeException("队列已经停止了");
	}

	/**
	 * 2014-05-21 by liusan.dyf
	 */
	public void stop() {
		if (!this.isInitialized())
			throw new RuntimeException("还未初始化，无法停止");

		checkStatus();

		this.working = false;
	}

	public int size() {
		return q.size();// 2014-07-14 by liusan.dyf
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Predicate<List<T>> getCallback() {
		return callback;
	}

	public void setCallback(Predicate<List<T>> callback) {
		this.callback = callback;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isWorking() {
		return working;
	}
}
