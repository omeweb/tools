package tools;

/**
 * 可以用java.lang.UnsupportedOperationException代替
 * 
 * @author 六三
 * @version 1.0
 * @since 2014年12月12日
 */
public class NotSupportedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotSupportedException() {
	}

	public NotSupportedException(String message) {
		super(message);
	}
}
