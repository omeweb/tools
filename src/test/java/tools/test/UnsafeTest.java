package tools.test;

import sun.misc.Unsafe;
import tools.Global;

public class UnsafeTest {
	private static final Unsafe unsafe = Global.getUnsafe();
	private static final long stateOffset;

	/**
	 * The synchronization state.
	 */
	private volatile int state;

	static {
		try {
			stateOffset = unsafe.objectFieldOffset(UnsafeTest.class.getDeclaredField("state"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	public final boolean compareAndSetState(int expect, int update) {
		// See below for intrinsics setup to support this
		return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
	}

	public int getState() {
		return state;
	}

	public static void main(String[] args) {
		UnsafeTest entry = new UnsafeTest();
		entry.compareAndSetState(0, 10);
		System.out.println(entry.getState());

		// 申请堆外内存 2014-12-15
		long value = 12345;
		byte size = 8; // a long is 64 bits (http://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html)
		long allocateMemory = unsafe.allocateMemory(size);
		unsafe.putLong(allocateMemory, value);
		long readValue = unsafe.getLong(allocateMemory);
		System.out.println("read value : " + readValue);
	}
}
