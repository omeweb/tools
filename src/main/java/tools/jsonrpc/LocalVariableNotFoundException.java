package tools.jsonrpc;

/**
 * 在class中未找到局部变量表信息<br>
 * 使用编译器选项 javac -g:{vars}来编译源文件
 * 
 * @author liusan.dyf
 */
public class LocalVariableNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static String msg = "class:%s 不包含局部变量表信息，请使用编译器选项 javac -g:{vars}来编译源文件。";

	public LocalVariableNotFoundException(String clazzName) {
		super(String.format(msg, clazzName));
	}
}
