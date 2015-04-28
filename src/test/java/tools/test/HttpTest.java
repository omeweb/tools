package tools.test;

import tools.http.HttpRequestBuilder;
import tools.http.HttpResponse;

public class HttpTest {

	public static void main(String[] args) {
		String url = "";
		url = "http://www.baidu.com";

		HttpResponse res = HttpRequestBuilder.create(url).head();

		String rtn = res.setContentCharset("gbk").getString();
		System.out.println("rtn=" + rtn + ", code=" + res.getResponseCode());
	}

	public static void mainx(String[] args) {
		String url = "*";

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
}
