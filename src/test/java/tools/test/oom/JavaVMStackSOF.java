package tools.test.oom;

/**
 * @author Yong Huang VM args: -Xss128K
 */
public class JavaVMStackSOF {

	private int stackLength = 1;

	public JavaVMStackSOF() {
		// TODO Auto-generated constructor stub
	}

	public void stackLeak() {
		stackLength++;
		stackLeak();
	}

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		// TODO Auto-generated method stub
		JavaVMStackSOF oom = new JavaVMStackSOF();
		try {
			oom.stackLeak();
		} catch (Throwable e) {
			System.out.println("stack length:" + oom.stackLength);
			throw e;
		}
	}

}
