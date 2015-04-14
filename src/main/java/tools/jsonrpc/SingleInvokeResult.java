package tools.jsonrpc;

/**
 * 对单个返回结果的封装，如果正确，result就是方法的返回值；如果错误，result=JsonRpcError
 * 
 * @author liusan.dyf
 */
public class SingleInvokeResult {
	private Object result;
	private int id;

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
