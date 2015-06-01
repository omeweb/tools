package tools;

public class Result<T> {
	private int code = -1; // 初始状态2015-4-20 14:42:29 by 六三
	private T value;
	private String message; // 2015-5-28 10:16:26 by liusan.dyf

	public Result() {

	}

	public Result(int c, T v) {
		code = c;
		value = v;
	}

	public Result(int c, T v, String m) {
		code = c;
		value = v;
		message = m;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
