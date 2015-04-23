package tools;

/**
 * 用在不需要堆栈信息的地方，性能高，用异常来做流程中断控制
 * 
 * @author liusan.dyf
 */
public class LightweightException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LightweightException() {
	}

	public LightweightException(String message) {
		super(message);
	}

	/**
	 * 注意，不需要堆栈信息 2012-07-19
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
}
