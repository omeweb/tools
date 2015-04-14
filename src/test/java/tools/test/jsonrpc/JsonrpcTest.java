package tools.test.jsonrpc;

import org.junit.Assert;

import org.junit.Test;

import tools.code.RunTimer;
import tools.jsonrpc.JsonRpcError;
import tools.jsonrpc.JsonRpcService;

public class JsonrpcTest {
	public static void main(String[] args) throws Exception {
		System.out.println("hello jsonrpc");
		new JsonrpcTest().test();
	}

	@Test
	public void test_() throws Exception {
		Object r = JsonRpcService.invoke(new Obj(), "add", "{\"b\":10,\"a\":12}", true);
		System.out.println("结果是：" + r);
		Assert.assertEquals(22, tools.Convert.toInt(r, 0));
	}

	@Test
	public void test() throws Exception {
		Object r = JsonRpcService.invoke(new Obj(),
				"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"add\",\"params\":{\"b\":1,\"a\":2}}", true);
		System.out.println("结果是：" + r);

		Assert.assertEquals(3, tools.Convert.toInt(r, 0));

		final Obj obj = new Obj();

		// 测试 449ms/471ms
		new RunTimer().run("test", 100000 * 1, new Runnable() {

			@Override
			public void run() {
				JsonRpcService.invoke(obj,
						"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"add\",\"params\":{\"b\":1,\"a\":2}}", true);
			}
		});
	}

	@Test
	public void oneParamTest() throws Exception {
		Object r = JsonRpcService.invoke(new Obj(),
				"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"incr\",\"params\":{\"a\":1,\"b\":2}}", true);
		// System.out.println("结果是：" + r);

		Assert.assertEquals(2, tools.Convert.toInt(r, 0));
	}

	@Test
	public void getMethodInfoTest() throws Exception {
		Object r = JsonRpcService.invoke(new Obj(),
				"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"add\",\"params\":'meta.methodinfo'}", true);
		System.out.println("结果是：" + r);
	}

	@Test
	public void instanceMethodTest() throws Exception {
		Object r = JsonRpcService.invoke(new Obj(),
				"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"newAdd\",\"params\":{\"a\":1,\"b\":2}}", true);
		// System.out.println("结果是：" + r);

		Assert.assertEquals(3, tools.Convert.toInt(r, 0));
	}

	@Test
	public void noParamTest() throws Exception {
		Object r = JsonRpcService.invoke(new Obj(),
				"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"getId\",\"params\":{\"a\":1,\"b\":2}}", true);
		// System.out.println("结果是：" + r);

		Assert.assertEquals(1, tools.Convert.toInt(r, 0));
	}

	@Test
	public void multiParamTest() throws Exception {
		Object r = JsonRpcService.invoke(new Obj(),
				"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"addMulti\",\"params\":[1,2,3]}", true);
		Assert.assertEquals(6, tools.Convert.toInt(r, 0));
	}

	@Test
	public void toStringTest() throws Exception {
		String r = JsonRpcService
				.invokeToString(
						new Obj(),
						"d[{\"id\":99,\"jsonrpc\":\"2.0\",\"method\":\"addMulti\",\"params\":[1,2,3]},{\"id\":100,\"jsonrpc\":\"2.0\",\"method\":\"addMulti\",\"params\":[1,2,3,\"a\"]},{\"id\":102,\"jsonrpc\":\"2.0\",\"method\":\"throwException\",\"params\":null}]",
						true);
		// Assert.assertEquals(6, tools.Convert.toInt(r, 0));
		System.out.println(r);
	}

	@Test
	public void entityParamTest() throws Exception {
		Object r = JsonRpcService
				.invoke(new Obj(),
						"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"doEntity\",\"params\":{\"error\":{\"message\":\"杜有发-message\"}}}",
						true);

		// 注意json的写法
		Assert.assertEquals("杜有发-message", ((JsonRpcError) r).getMessage());
	}

	@Test
	public void complexEntityParamTest() throws Exception {
		String r = JsonRpcService
				.invokeToString(
						new Obj(),
						"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"doComplexEntity\",\"params\":{\"entry\":{\"startDate\":\"2011-11-02\",\"pageIndex\":10,\"pageSize\":10}}}",
						true);

		System.out.println(r);

		// Assert.assertEquals(10, ((ComplexEntity) r).getPageIndex());

		// 测试
		final Obj obj = new Obj();
		new RunTimer().run("complexEntityParamTest", 1, new Runnable() {

			@Override
			public void run() {
				JsonRpcService
						.invoke(obj,
								"{\"id\":\"100\",\"jsonrpc\":\"2.0\",\"method\":\"doComplexEntity\",\"params\":{\"entry\":{\"startDate\":\"2011-11-02\",\"pageIndex\":10,\"pageSize\":10}}}",
								true);
			}
		});
	}
}
