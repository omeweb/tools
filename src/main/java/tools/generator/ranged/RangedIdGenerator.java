package tools.generator.ranged;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import tools.generator.IdGenerator;

/**
 * 也需要单例运行，生成的值的范围是（start,end】（增量为1时）<br />
 * 如果（0,max]表示最大只能为max <br />
 * 注意：range只能往大的方向设置，以此来解决并发问题 2012-11-20
 * 
 * @author liusan.dyf
 */
public class RangedIdGenerator implements IdGenerator {
	// private long start;
	private AtomicLong max = new AtomicLong(0);// 可能为null，所以设置为Long；0也可能是有效的max值
	private IdGenerator idGenerator = null;
	private RangeGenerator rangeGetter = null;

	private ReentrantLock lock = new ReentrantLock();

	private Condition waitReadyFlag = lock.newCondition();

	/**
	 * 区间是否已经设置，默认为true 2012-11-19 by liusan.dyf
	 */
	private AtomicBoolean readyFlag = new AtomicBoolean(true);

	public RangedIdGenerator(IdGenerator idGenerator, RangeGenerator rangeGetter) {
		this.idGenerator = idGenerator;
		this.rangeGetter = rangeGetter;

		if (rangeGetter == null)
			throw new IllegalArgumentException("rangeGetter不能为null");

		init();
	}

	@Override
	public long current() {
		return idGenerator.current();
	}

	@Override
	public void init() {
		idGenerator.init();

		// 设置range
		Range r = rangeGetter.current(getKey());
		if (r == null) // 当前range不存在
			r = rangeGetter.next(getKey());

		setRange(r);
	}

	/**
	 * 获取一个新的range，并设置
	 * 
	 * @param r
	 */
	private synchronized void setRange(Range r) {
		if (r == null)
			throw new IllegalArgumentException("range不能为空");

		// if (max.get() < r.getMax())// 重要，防止由于并发问题引起小区间在大区间后设置 2012-11-20 by liusan.dyf
		max.compareAndSet(max.get(), r.getMax());

		// sleep(20);

		setInitialValue(r.getMin());

		// 保存 2012-02-12
		rangeGetter.save(getKey(), r);
	}

	@SuppressWarnings("unused")
	private void println(Object obj) {
		sleep(0);
		System.out.println(System.nanoTime() + " " + Thread.currentThread().getName() + ":" + obj);
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {

		}
	}

	/*
	 * 如果返回Long.MIN_VALUE，则表示无效
	 */
	@Override
	public long next() {
		while (!readyFlag.get()) {
			try {
				lock.lock();
				if (!readyFlag.get())
					waitReadyFlag.await();
			} catch (InterruptedException e) {
				// IGNORE
			} finally {
				lock.unlock();
			}
		}

		long m = this.max.get();
		long rtn = this.idGenerator.next();

		if (rtn > m) {
			try {
				lock.lock();
				// if (readyFlag.get()) {
				// return next();
				// }
				readyFlag.set(false);

				// do sth...

				Range r = null;
				while (true) {
					r = rangeGetter.next(getKey());

					if (r == null) {// 2013-05-06 by liusan.dyf
						sleep(100);
						continue;
					}

					if (max.get() < r.getMax())
						break;
				}

				setRange(r);
			} finally {
				if (!readyFlag.get()) {
					readyFlag.set(true);
					waitReadyFlag.signalAll();
				}
				lock.unlock();
			}
			return next();
		}

		return rtn;
	}

	@Override
	public void setInitialValue(long value) {
		idGenerator.setInitialValue(value);
	}

	@Override
	public String next(int count) {
		throw new RuntimeException("NotImplemented");
	}

	@Override
	public String getKey() {
		return idGenerator.getKey();
	}

	@Override
	public int getIncrement() {
		return idGenerator.getIncrement();
	}
}
