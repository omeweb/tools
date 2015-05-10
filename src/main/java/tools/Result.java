package tools;

public class Result<T> {
	private int code = -1; // 初始状态2015-4-20 14:42:29 by 六三
	private T value;

	public Result() {

	}

	public Result(int c, T v) {
		code = c;
		value = v;
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
}
