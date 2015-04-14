package tools.test.spring;

public class SlowBean {
	public void init() {
		System.out.println("begin init");

		tools.Global.sleep(1000 * 10);

		System.out.println("end init");
	}
}
