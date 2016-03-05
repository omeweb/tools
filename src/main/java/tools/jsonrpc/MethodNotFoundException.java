package tools.jsonrpc;

public class MethodNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2571099572816268317L;

	public MethodNotFoundException(String message) {
		super("不存在的方法名：" + message);
	}
}
