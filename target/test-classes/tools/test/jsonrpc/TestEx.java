package tools.test.jsonrpc;

import org.junit.Assert;
import org.junit.Test;

import tools.jsonrpc.JsonRpcService;

public class TestEx {
	@Test
	public void mainX() {
		String expectedResult = "{\"id\":100,\"jsonrpc\":\"2.0\",\"result\":11}";
		// 静态方法
		String r = JsonRpcService.invokeToString(Obj.class,
				"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"add\",\"params\":{\"a\":1,\"b\":10}}", true);

		System.out.println(r);

		Assert.assertEquals(expectedResult, r);

		// 动态方法
		r = JsonRpcService.invokeToString(new Obj(),
				"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"add\",\"params\":{\"a\":1,\"b\":10}}", true);

		System.out.println(r);
		Assert.assertEquals(expectedResult, r);
	}

	public static void main(String[] args) {

	}
}
