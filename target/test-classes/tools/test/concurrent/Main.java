package tools.test.concurrent;

public class Main {
	public static void main(String[] args) {
		Local a = new Local();
		AnotherLocal b = new AnotherLocal();

		System.out.println(a.get());
		System.out.println(b.get());

		// 同一个线程里可以有多个thradlocal对象

	}
}
