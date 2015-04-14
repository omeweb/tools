package tools.jsonrpc;

import tools.StringUtil;

/**
 * 一个简单的error对象 2012-04-09修改，依赖于StringUtil.jsonEscape
 * 
 * @author liusan.dyf
 */
public class JsonRpcError {
	private int id;
	private int code;
	private String message;

	private static final String TEMPLATE = "{\"jsonrpc\": \"2.0\", \"error\": {\"code\": #code#, \"message\": \"#message#\"}, \"id\": #id#}";
	private static final String[] TAGS = new String[] { "#code#", "#message#", "#id#" };

	public JsonRpcError(int id, int code, String message) {
		this.setId(id);
		this.setCode(code);
		this.setMessage(message);
	}

	public JsonRpcError() {
	}

	@Override
	public String toString() {
		String[] values = new String[] { String.valueOf(code), StringUtil.escapeJSON(message), String.valueOf(id) };
		return StringUtil.replaceAllArray(TEMPLATE, TAGS, values);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
