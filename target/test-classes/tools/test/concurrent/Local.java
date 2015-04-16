package tools.test.concurrent;

public class Local {
	private static ThreadLocal<String> local = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return "Local";
		}
	};

	public String get() {
		return local.get().toString();
	}
}
