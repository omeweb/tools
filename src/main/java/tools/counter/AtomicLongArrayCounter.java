package tools.counter;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * 初始化一个大小固定为size的AtomicLongArray
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2014年11月7日
 */
public class AtomicLongArrayCounter {
	public static final int INVALID_VALUE = -1;

	private int size = 1024;
	private AtomicLongArray ala = null;// 计数器的数组

	public AtomicLongArrayCounter(int size) {
		if (size <= 0)
			throw new IllegalArgumentException("size不能小于等于0");

		this.size = size;

		ala = new AtomicLongArray(size);
	}

	private boolean checkSize(int i) {
		return i >= 0 && i < getSize();
	}

	public long get(int i) {
		if (checkSize(i)) {
			return ala.get(i);
		}

		return INVALID_VALUE;
	}

	public long addAndGet(int i, int delta) {
		if (checkSize(i)) {
			return ala.addAndGet(i, delta);
		}

		return INVALID_VALUE;
	}

	public long getAndAdd(int i, int delta) {
		if (checkSize(i)) {
			return ala.getAndAdd(i, delta);
		}

		return INVALID_VALUE;
	}

	public long incrementAndGet(int i) {
		if (checkSize(i)) {
			return ala.incrementAndGet(i);
		}

		return INVALID_VALUE;
	}

	public long getAndSet(int i, int newValue) {
		if (checkSize(i)) {
			return ala.getAndSet(i, newValue);
		}

		return INVALID_VALUE;
	}

	public int getSize() {
		return size;
	}
}
