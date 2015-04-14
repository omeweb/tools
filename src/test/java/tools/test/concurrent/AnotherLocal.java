package tools.test.concurrent;

public class AnotherLocal {
	private static ThreadLocal<String> local = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return "ThreadLocal";
		}
	};

	public String get() {
		return local.get().toString();
	}
}
