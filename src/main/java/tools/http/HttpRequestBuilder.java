package tools.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liusan.dyf
 */
public class HttpRequestBuilder {

	private HttpRequest httpRequest = null;
	private String url = null;

	/**
	 * 这些在HttpRequest里没有单独的容器来装，不像headers，cookies
	 */
	Map<String, String> data;
	List<HttpPostedFile> files;

	private HttpRequestBuilder(String url) {
		this.httpRequest = new HttpRequest();
		this.url = url;
	}

	public HttpRequestBuilder readTimeout(int timeout) {
		this.httpRequest.setReadTimeout(timeout);
		return this;
	}

	public HttpRequestBuilder connectTimeout(int timeout) {
		this.httpRequest.setConnectTimeout(timeout);
		return this;
	}

	/**
	 * nnd，单词写错了，应该是connectTimeout，这里为了兼容老的jar或者用法，暂时不予去掉 2014-11-19 by 六三
	 * 
	 * @param timeout
	 * @return
	 */
	@Deprecated
	public HttpRequestBuilder connecTimeout(int timeout) {
		return connectTimeout(timeout);
	}

	public HttpRequestBuilder charset(String charset) {
		this.httpRequest.setCharset(charset);
		return this;
	}

	public HttpRequestBuilder userAgent(String value) {
		return header("User-Agent", value);
	}

	public HttpRequestBuilder cookie(String name, String value) {
		this.httpRequest.addCookie(name, value);
		return this;
	}

	/**
	 * 注意value里的汉字要编码：auth="auth=" + HttpUtil.encode(汉字) 2013-08-27 by liusan.dyf
	 * 
	 * @param value
	 * @return
	 */
	public HttpRequestBuilder cookie(String value) {
		this.httpRequest.setCookieString(value);
		return this;
	}

	public HttpRequestBuilder data(String name, String value) {
		if (data == null)
			data = new HashMap<String, String>();

		if (value == null)
			return this;

		data.put(name, value);
		return this;
	}

	/**
	 * 直接把value的内容发送过去，该方法会把先前设置的data清理掉 2012-02-02
	 * 
	 * @param value 可以为a=b&c=d的格式，特殊字符请自行编码
	 * @return
	 */
	public HttpRequestBuilder data(String value) {
		if (data == null)
			data = new HashMap<String, String>();
		else
			data.clear();

		data.put(HttpRequest.EMPTY_KEY, value);
		return this;
	}

	public HttpRequestBuilder data(Map<String, String> map) {
		if (data == null)
			data = new HashMap<String, String>();
		if (map != null) // fixed
			data.putAll(map);
		return this;
	}

	public HttpRequestBuilder header(String name, String value) {
		this.httpRequest.addHeader(name, value);
		return this;
	}

	public HttpRequestBuilder host(String value) {
		return this.header("Host", value);
	}

	public HttpRequestBuilder referer(String value) {
		return this.header("Referer", value);
	}

	public HttpRequestBuilder multipart(boolean value) {
		this.httpRequest.setMultipartFormData(value);
		return this;
	}

	/**
	 * 读取超时，单位是毫秒 2012-07-17
	 * 
	 * @param ms
	 * @return
	 */
	public HttpRequestBuilder timeout(int ms) {
		this.httpRequest.setReadTimeout(ms);
		return this;
	}

	public HttpRequestBuilder proxy(String host, int port) {
		this.httpRequest.setProxy(host, port, null, null);
		return this;
	}

	public HttpRequestBuilder file(List<HttpPostedFile> f) {
		if (files == null)
			files = new ArrayList<HttpPostedFile>();
		// TODO 判断list里的file是不是重复了，防止重复添加文件
		if (f != null) // fixed
			files.addAll(f);
		return this;
	}

	public HttpRequestBuilder file(HttpPostedFile f) {
		if (files == null)
			files = new ArrayList<HttpPostedFile>();
		// TODO 判断list里的file是不是重复了，防止重复添加文件
		files.add(f);
		return this;
	}

	public HttpResponse get() {
		return this.execute(HttpMethod.GET);
	}

	/**
	 * 2012-07-02 by liusan.dyf
	 * 
	 * @return
	 */
	public HttpResponse head() {
		return this.execute(HttpMethod.HEAD);
	}

	public HttpResponse delete() {
		return this.execute(HttpMethod.DELETE);
	}

	public HttpResponse post() {
		return this.execute(HttpMethod.POST);
	}

	public HttpResponse put() {
		return this.execute(HttpMethod.PUT);
	}

	/**
	 * 增加trace请求 2015-9-9 17:05:18 by liusan.dyf
	 * 
	 * @return
	 */
	public HttpResponse trace() {
		return this.execute(HttpMethod.TRACE);
	}

	/**
	 * method大写；为了使用方便，这里隐藏了Exception，注意catch 2012-02-17
	 * 
	 * @param method
	 * @return
	 */
	public HttpResponse execute(String method) {
		// System.out.println(this.data);
		// httpRequest要求method大写
		try {
			return this.httpRequest.execute(method.toUpperCase(), url, data, files);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpRequestBuilder create(String url) {
		return new HttpRequestBuilder(url);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		int i = 0;

		// https测试
		String str = HttpRequestBuilder.create("http://127.0.0.1/s.do").cookie("auth", "57526:六三:*:1451961451:a6951f62eaf7d4a34f2ed5c8a000792d").get().setContentCharset("utf-8").toString();
		System.out.println(str);

		if (i == 0)
			return;

		HttpResponse res = HttpRequestBuilder.create("http://www.baidu.com/s").proxy("localhost", 8888)
				// .header("Content-Type", "application/json")
				// .data("wd=淘宝")
				.charset("gb2312").data("wd", "淘宝").post();
				// res.setContentCharset("gb2312").saveAs(new File("d:/baidu.txt"));

		// head测试 2012-07-02
		res = HttpRequestBuilder.create("http://ww1.sinaimg.cn/bmiddle/6a14bb60jw1duje404yqaj.jpg").head();
		System.out.println(res.getResponseHeader("Content-Length"));
	}
}
