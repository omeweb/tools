package tools.generator;

/**
 * 2012-02-12
 * 
 * @author liusan.dyf
 */
public class NoSuchGeneratorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchGeneratorException(Exception e) {
		super(e);
	}

	public NoSuchGeneratorException(String msg) {
		super(msg);
	}
}
