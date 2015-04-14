package tools.test;

import org.junit.Test;

import tools.http.HttpRequestBuilder;
import tools.http.HttpResponse;

public class HttpTest {

	public static void main(String[] args) {
		String url = "https://mtee.alibaba-inc.com/requestLimited.do?obj=kv&method=getOne&params={%22typeCode%22:%22app%22,%22key%22:%22raider%22}&t=0&appKey=raider&appSecret=taobao1234&host=raider010152034010.et1/10.152.34.10";
		url = "http://www.baidu.com";

		HttpResponse res = HttpRequestBuilder.create(url).head();

		String rtn = res.setContentCharset("gbk").getString();
		System.out.println("rtn=" + rtn + ", code=" + res.getResponseCode());
	}

	public static void main_y(String[] args) {
		String url = "https://mtee.alibaba-inc.com/requestLimited.do?obj=kv&method=getOne&params={%22typeCode%22:%22app%22,%22key%22:%22raider%22}&t=0&appKey=raider&appSecret=taobao1234&host=raider010152034010.et1/10.152.34.10";
		url = "http://www.baidu.com";

		HttpResponse res = HttpRequestBuilder.create(url).post();

		String rtn = res.setContentCharset("gbk").getString();
		System.out.println("rtn=" + rtn + ", code=" + res.getResponseCode());
	}

	public static void mainx(String[] args) {
		String url = "http://mtee.admin.taobao.org/request.do?__code=__system_service&__sync=true&__script_keys=1650&__return_what=10&action=script";

		HttpResponse res = HttpRequestBuilder
				.create(url)
				// .proxy("localhost", 8888)
				.charset("gbk")
				.data("script",
						"{\"id\":1,\"method\":\"getPagedList\",\"params\":{\"query\":{\"typeCode\":\"60\",\"keyword\":\"操作\"},\"pageSize\":20,\"pageIndex\":0},\"jsonrpc\":\"2.0\"}")
				.post();

		String rtn = res.setContentCharset("gbk").getString();
		System.out.println(rtn);
	}

	// @Test
	public void substring() {
		HttpResponse res = HttpRequestBuilder
				.create("http://mtee.admin.taobao.org:8080/s.do")
				.proxy("localhost", 8888)
				// .data("wd=淘宝")
				.charset("gbk")
				.cookie("auth=666172%3A%E5%85%AD%E4%B8%89%3A*%3A1347594581%3A02287ab01bcb826ecf5e365c9cccf7c2")
				.data("jsonrpcContent",
						"{\"id\":1,\"method\":\"getPagedList\",\"params\":{\"query\":{\"typeCode\":\"60\",\"keyword\":\"操作\"},\"pageSize\":20,\"pageIndex\":0},\"jsonrpc\":\"2.0\"}")
				.post();

		String rtn = res.setContentCharset("gbk").getString();
		System.out.println(rtn);
	}

	@Test
	public void uaTest() {
		HttpResponse res = HttpRequestBuilder
				.create("http://acjs.aliyun-inc.com/acserver/check_pc_ua_post/?bid=pc_mtee_wangwang&is_return_plaintext=1")
				.data("data",
						"AAEAXwANAkwBAAAAAAAAAGUBAAhD+/2SGt5atWYBABBxtCgVHod5UbaAzLY1h901ZwEAEN+7MXfUO+pXOJRSEcZzw1JoAQAIQmb0/+fQHdppAQAQzszicaxvaySSDZRFh3uVV2oBAAheH0dKHX/PdGsBAAi7xXGeFNEdv2wBABi3V+9cylQvpFr8zdRS6wGdtDEONYPCv5FtAQAQML9CjnRYYCUWTAGVSaZ4/W4BACj8OXnSS7Xk2zMUKsKhjO3tF7i8kuYBgkHars09wnAHhnFsTi8c/4vEbwEA+MGuiSfeD+CJ9Zy0l20M5xkznkqGPCZFbUHkRiMmwYMvwHI4FrkCEBnFX0CAyu0CIcpZ6RkIofVCVlntddWN0UFA2odBIHXaFjvrlMiid1/M2U1HNz+HITPOwobwJwnIeAIurwyb+fv1yRJ9LEcg8yzmeffX9nxEjFcbRATakl9WtnjRdfvuNOCUPlYap+mh6/IhdNpcvPicVSffKriB3+Z9Wjp3xFk2EjoFjcimMEbct16LIOMGzRvm+HlS5LbJW8vA9FpUK0VjnKxgWz9xTcavFU445vF9UMdQ/a2s1Gys2LNt+4TtrpF5DOUnSAAP7RZMAZVJpnj9cAEAcJA6UPv6bI/3qDpnlDPQiQdVspOKBgvSY66/PLptt8xl9y8a04oam30AirjXFbo9G3BGYl6cX9MPIIeA/xHYZ8a8UzgYJjmE3I//PxSHn5K8OfQ650Dqyk2h7E7lAy7Nn3rg9atoAEzhphbpxqwSlpNxAQAQML9CjnRYYCVGKGmMXdX4zw==")
				.post();

		String rtn = res.setContentCharset("utf-8").getString();
		System.out.println(rtn);
	}

	@Test
	public void ww() {
		HttpResponse res = HttpRequestBuilder
				.create("http://mtee.admin.taobao.org/request.do?__code=__system_service&__sync=true&__script_keys=1650&action=sendWWMessage")
				.data("names", "六三").data("content", "你好世界").charset("gbk").post();

		String rtn = res.setContentCharset("utf-8").getString();
		System.out.println(rtn);
	}
}
