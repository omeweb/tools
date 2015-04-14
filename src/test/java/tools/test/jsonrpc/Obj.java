package tools.test.jsonrpc;

import tools.jsonrpc.JsonRpcError;

public class Obj {

	// 以下是一些方法测试
	public static int add(int a, int b) {
		return a + b;
	}

	public static int addMulti(int[] args) {
		int r = 0;
		for (int v : args)
			r += v;
		return r;
	}

	public static int incr(int a) {
		return a + 1;
	}

	public static int getId() {
		return 1;
	}

	public int newAdd(int a, int b) {
		return a + b;
	}

	public JsonRpcError doEntity(JsonRpcError error) {
		return error;
	}

	public ComplexEntity doComplexEntity(ComplexEntity entry) {
		if (entry == null)
			throw new IllegalArgumentException("实体不能为null");

		if (entry.getPageSize() <= 0)
			throw new IllegalArgumentException("实体pageSize参数不能小于等于0");

		if (entry.getStartDate() == null)
			throw new IllegalArgumentException("实体startDate参数不能为null");

		return entry;
	}

	public int throwException(JsonRpcError error) throws Exception {
		// throw new IllegalArgumentException("异常测试");
		throw new Exception("异常测试");
	}
}
