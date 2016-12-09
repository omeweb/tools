package tools.http;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 目前暂时不支持直接post/put流到服务器 2012-02-03
 * 
 * @author liusan.dyf
 */
public class HttpRequest {

	static {
		// HttpURLConnection修改请求头Host问题
		// http://www.cnblogs.com/langke93/archive/2011/09/30/2196258.html

		// http://stackoverflow.com/questions/1936872/how-to-keep-multiple-java-httpconnections-open-to-same-destination
		System.setProperty("http.maxConnections", "100");
		System.setProperty("sun.net.http.errorstream.enableBuffering", "true");

		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

		// System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");

		// System.getProperty("http.proxyHost")
		// System.getProperty("http.proxyPort");
		// System.getProperty("http.agent");
		// System.getProperty("http.keepAlive");

		// java里的http相关的知识：http://www.blogjava.net/xjacker/articles/334709.html

		// ------处理https 2012-11-07 by liusan.dyf
		// from
		// http://socialauth.googlecode.com/svn/trunk/socialauth-core/src/org/brickred/socialauth/util/HttpUtil.java
		SSLContext ctx;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
			SSLContext.setDefault(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ------处理https 2014-04-18 by liusan.dyf
		// from http://caohongxing7604.blog.163.com/blog/static/320169742008101813253341/
		// package 都是 javax.net.ssl, 而非 com.sun.net.ssl，如果允许所有 ip 都可以通过认证, 甚至可以在 verify 中直接返回 true ! 当然这是不推荐的做法
		System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");

		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return urlHostName.equals(session.getPeerHost());
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(hv);

		// ------end
	}

	/**
	 * 2012-11-07 by liusan.dyf
	 * 
	 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
	 * @version 1.0
	 * @since 2012-11-7
	 */
	private static class DefaultTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	/**
	 * 一些方法里要用到，所以设置为全局变量
	 */
	private OutputStream outputStream = null;
	Map<String, String> cookies;// = new HashMap<String, String>();
	Map<String, String> headers;// = new HashMap<String, String>();
	private ProxyEntry proxyEntry;
	private String cookieString = null;// 2011-12-23

	public String getCookieString() {
		return cookieString;
	}

	public void setCookieString(String cookieString) {
		this.cookieString = cookieString;
	}

	/**
	 * 输入流的编码
	 */
	private String charset = HttpUtil.DEFAULT_CHARSET;// "utf-8";

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	private static final String URLENCODE_CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String BOUNDARY = "---------------------------7db3a8184203b6";
	private static final String MULTIPART_DATA_CONTENT_TYPE = "multipart/form-data; boundary=" + BOUNDARY;

	// 2015-8-24 09:54:51 by liusan.dyf
	public static final String CONTENT_TYPE = "Content-Type";

	/**
	 * 2012-02-02，场合：构造post/put请求，直接把一串内容发送过去
	 */
	public static final String EMPTY_KEY = HttpUtil.EMPTY_KEY;

	/**
	 * 是否启用multipart/form-data; boundary=...如果是get类的请求，则忽略该参数<br/>
	 * 如果是post，且没有files，则multipart的报文和x-www-form-urlencode的报文不一致
	 */
	private boolean multipartFormData;

	public boolean isMultipartFormData() {
		return multipartFormData;
	}

	public void setMultipartFormData(boolean multipartFormData) {
		this.multipartFormData = multipartFormData;
	}

	/**
	 * 连接超时
	 */
	private int connectTimeout = 0;

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * 读取超时
	 */
	private int readTimeout = 0;

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setProxy(String host, int port, String userName, String password) {
		// 设置代理
		if (proxyEntry == null)
			proxyEntry = new ProxyEntry();

		proxyEntry.setHost(host);
		proxyEntry.setPort(port);
		proxyEntry.setUserName(userName);
		proxyEntry.setPassword(password);
	}

	public void addHeader(String name, String value) {
		if (headers == null)
			headers = new HashMap<String, String>();
		headers.put(name, value);
	}

	public void addCookie(String name, String value) {
		if (cookies == null)
			cookies = new HashMap<String, String>();
		cookies.put(name, value);
	}

	/**
	 * 构造特殊的HashMap数据，好直接发送到服务器上 2012-02-02
	 * 
	 * @param url
	 * @param data 可以为a=b&c=d的格式，特殊字符请自行编码
	 * @return
	 * @throws Exception
	 */
	public HttpResponse post(String url, String data) throws Exception {
		HashMap<String, String> params = new HashMap<String, String>(1);
		params.put(EMPTY_KEY, data);

		return execute(HttpMethod.POST, url, params, null);
	}

	public HttpResponse post(String url, Map<String, String> params) throws Exception {
		return execute(HttpMethod.POST, url, params, null);
	}

	public HttpResponse post(String url, Map<String, String> params, List<HttpPostedFile> files) throws Exception {
		return execute(HttpMethod.POST, url, params, files);
	}

	public HttpResponse put(String url, Map<String, String> params) throws Exception {
		return execute(HttpMethod.PUT, url, params, null);
	}

	public HttpResponse put(String url, Map<String, String> params, List<HttpPostedFile> files) throws Exception {
		return execute(HttpMethod.PUT, url, params, files);
	}

	public HttpResponse get(String url, Map<String, String> params) throws Exception {
		return execute(HttpMethod.GET, url, params, null);
	}

	public HttpResponse delete(String url, Map<String, String> params) throws Exception {
		return execute(HttpMethod.DELETE, url, params, null);
	}

	public HttpResponse head(String url, Map<String, String> params) throws Exception {
		return execute(HttpMethod.HEAD, url, params, null);
	}

	public HttpResponse execute(String method, String url, Map<String, String> params, List<HttpPostedFile> files)
		throws Exception {
		if (method == null)
			throw new IllegalArgumentException("method不能为null");

		if (url == null || url.length() == 0)
			throw new IllegalArgumentException("url不能为null");

		method = method.toUpperCase();

		boolean isGet = false, isPost = false, isPut = false, isDelete = false, isHead = false;
		boolean isValidMethod = (isGet = method.equals(HttpMethod.GET)) || (isPost = method.equals(HttpMethod.POST))
				|| (isPut = method.equals(HttpMethod.PUT)) || (isDelete = method.equals(HttpMethod.DELETE))
				|| (isHead = method.equals(HttpMethod.HEAD));

		if (!isValidMethod)
			throw new IllegalArgumentException("MethodNotSupported:" + method);

		// 一些状态清理
		outputStream = null;

		// 处理URL，get类的请求，因为HttpURLConnection的创建需要url信息
		if (isGet || isDelete || isHead) {
			multipartFormData = false;// 修正useMultipart
			url = HttpUtil.generateUrl(url, params, getCharset()); // 2016-6-15 14:35:12 by liusan.dyf
		}

		// 创建连接
		HttpURLConnection connection = null;
		URL u = new URL(url);

		// 处理代理
		if (proxyEntry != null) {
			Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyEntry.getHost(), proxyEntry.getPort()));
			connection = (HttpURLConnection) u.openConnection(p);
		} else
			connection = (HttpURLConnection) u.openConnection();

		// 一些属性设置
		// http://www.cnblogs.com/guodongli/archive/2011/04/05/2005930.html
		connection.setDoOutput(true);// post时要写数据到流里，所以true，默认情况下是false
		connection.setDoInput(true);// 设置是否从httpUrlConnection读入，默认情况下是true;
		if (connectTimeout > 0)
			connection.setConnectTimeout(connectTimeout);
		connection.setDefaultUseCaches(false);// 请求不能使用缓存
		if (readTimeout > 0)
			connection.setReadTimeout(readTimeout);
		connection.setRequestMethod(method);

		// 注意，有关addRequestProperty的操作，要在connection的stream操作之前进行，否则addRequestProperty时报Already
		// connected，即在connection.getOutputStream()之前要设置完毕
		// 在httpUrlConn.getInputStream();才进行socket连接，发送http请求，解析http响应信息
		// getOutputStream时，已经连接上了服务器；addRequestProperty时还不能连接到服务器
		// getOutputStream会隐含的进行connect(即：connect()方法

		// 处理headers
		if (headers != null) {
			Set<Entry<String, String>> set = headers.entrySet();
			for (Entry<String, String> item : set) {
				connection.addRequestProperty(item.getKey(), item.getValue());
			}
		}

		// 处理cookies
		String cookie = appendCookieString(this.getCookieString(), this.cookies, getCharset());
		if (cookie != null)
			connection.addRequestProperty("Cookie", cookie);

		// 处理post的内容
		boolean isPostOrPut = isPost || isPut;
		if (isPostOrPut) {
			// 处理files
			if (files != null && files.size() > 0) {
				// 处理content type
				connection.addRequestProperty(CONTENT_TYPE, MULTIPART_DATA_CONTENT_TYPE);

				// 获取outputStream
				outputStream = connection.getOutputStream();

				// 附加fiels
				for (HttpPostedFile item : files) {
					writeFile(item.getFieldName(), item.getFileName(), item.getBytes());
				}

				// 附加其他字段
				if (params != null && params.size() > 0) {
					Set<Entry<String, String>> set = params.entrySet();
					for (Entry<String, String> item : set) {
						String key = item.getKey();
						String value = item.getValue();
						// 2011-12-20 fixed
						if (key == null || key.length() == 0 || value == null || value.length() == 0)
							continue;
						writeField(key, value);
					}
				}

				// 流的结尾部分
				writeBoundary();
				writeln("--");
			} else {// 没有文件
				if (params != null && params.size() > 0) {
					// 2015-8-24 09:53:39 by liusan.dyf
					if (!connection.getRequestProperties().containsKey(CONTENT_TYPE)) {
						// 处理content type
						if (multipartFormData)
							connection.addRequestProperty(CONTENT_TYPE, MULTIPART_DATA_CONTENT_TYPE);
						else
							connection.addRequestProperty(CONTENT_TYPE, URLENCODE_CONTENT_TYPE);
					}

					// 获取outputStream
					outputStream = connection.getOutputStream();

					if (multipartFormData) {
						Set<Entry<String, String>> set = params.entrySet();
						for (Entry<String, String> item : set) {
							String key = item.getKey();
							String value = item.getValue();
							// 2011-12-20 fixed
							if (key == null || key.length() == 0 || value == null || value.length() == 0)
								continue;
							writeField(key, value);
						}

						// 流的结尾部分
						// writeBoundary(); writeln("--");
						writeEnd();
					} else {// 普通的post/put请求
						// 2012-02-02 增加应用场合：直接把String或者是stream发往服务器上
						if (params.size() == 1 && params.containsKey(EMPTY_KEY)) {
							write(params.get(EMPTY_KEY));
						} else
							write(HttpUtil.toUrlEncodedString(params, getCharset()));
					}
				}
			}

			// 关闭流 2012-02-20修改，当post的data为null时，outputStream也为null
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
		}

		// System.out.println(connection.getHeaderField("Server"));
		// connection.getXXX 都是返回服务器响应的信息，不论代码是否放置connection.getInputStream()后

		// 得到结果

		return new HttpResponse(connection);
	}

	private static String appendCookieString(String cookie, Map<String, String> odditionalCookies, String charset) {
		if (odditionalCookies == null)
			return cookie;

		StringBuffer sb = new StringBuffer();
		if (cookie != null) { // 2012-09-15 fixbug，cookie可能为null
			sb.append(cookie);// 2011-12-23
			String suffix = "; ";
			if (!cookie.endsWith(suffix)) {
				sb.append(suffix);
			}
		}

		for (Iterator<Map.Entry<String, String>> i = odditionalCookies.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, String> entry = i.next();
			sb.append(HttpUtil.encode(entry.getKey(), charset) + "=" + HttpUtil.encode(entry.getValue(), charset));

			if (i.hasNext()) {
				sb.append("; ");
			}
		}

		return sb.toString();
	}

	private void writeField(String name, String value) throws IOException {
		writeBoundary();
		writeName(name);
		writeNewline();
		writeNewline();
		writeln(value);
	}

	private void writeFile(String name, String filename, byte[] bytes) throws IOException {
		writeBoundary();
		writeName(name);
		write("; filename=\"");
		write(filename);
		write('"');
		writeNewline();
		write("Content-Type: ");
		String type = URLConnection.guessContentTypeFromName(filename);
		if (type == null)
			type = "application/octet-stream";
		writeln(type);
		writeNewline();
		writeBytes(bytes);
		writeNewline();
	}

	@SuppressWarnings("unused")
	private void writeStream(InputStream in) throws IOException {
		byte[] buf = new byte[512];
		int read;
		// int navailable = 0;
		// int total = 0;
		synchronized (in) {
			while ((read = in.read(buf, 0, buf.length)) >= 0) {
				outputStream.write(buf, 0, read);
				// total += read;
			}
		}
		outputStream.flush();
		buf = null;
	}

	private void writeBytes(byte[] bytes) throws IOException {
		outputStream.write(bytes);
	}

	private void write(char c) throws IOException {
		outputStream.write(c);
	}

	private void write(String s) throws IOException {
		outputStream.write(s.getBytes(getCharset()));
	}

	private void writeNewline() throws IOException {
		write("\r\n");
	}

	private void writeln(String s) throws IOException {
		write(s);
		writeNewline();
	}

	private void writeBoundary() throws IOException {
		write("--");
		write(BOUNDARY);
	}

	private void writeEnd() throws IOException {
		writeBoundary();
		writeln("--");
	}

	private void writeName(String name) throws IOException {
		writeNewline();
		write("Content-Disposition: form-data; name=\"");
		write(name);
		write('"');
	}

	public static void main(String[] args) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setProxy("localhost", 8888, null, null);
		request.addHeader("testKey", "testValue");
		request.addCookie("name", "杜有发");

		// 参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("testKey", "testValue");
		params.put("name", "duyoufa");
		params.put("typeId", "100");

		// params = null;
		// request.post("http://www.baidu.com",params);
		// return;

		// 上传模式
		// request.setMultipartFormData(true);

		// 文件上传
		HttpPostedFile f = new HttpPostedFile();
		f.setFieldName("file1");
		f.setFileName("32ea26250b038a5530d4bc91f7348732.jpg");
		f.setContent(new FileInputStream(new File("d:/32ea26250b038a5530d4bc91f7348732.jpg")));

		List<HttpPostedFile> files = new ArrayList<HttpPostedFile>();
		files.add(f);

		// 请求
		String url = "http://localhost/com.taobao.demo/DefaultServlet";
		// url = "http://www.baidu.com";

		for (int i = 0; i < 1; i++) {
			// HttpResponse res = request.get(url, params);
			HttpResponse res = request.post(url, params, null);
			res.setContentCharset("gb2312");
			System.out.println("result is:" + res.getString());
		}
	}
}

/**
 * 代理基本信息类
 * 
 * @author liusan.dyf
 */
class ProxyEntry {
	private String host;
	private int port;
	private String userName;
	private String password;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

// POST http://localhost./com.taobao.demo/DefaultServlet HTTP/1.1
// Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg,
// application/x-shockwave-flash, application/xaml+xml,
// application/vnd.ms-xpsdocument, application/x-ms-xbap,
// application/x-ms-application, application/vnd.ms-excel,
// application/vnd.ms-powerpoint, application/msword, */*
// Referer: http://localhost./com.taobao.demo/index.jsp
// Accept-Language: zh-cn
// User-Agent: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0;
// .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR
// 3.0.4506.2152; .NET CLR 3.5.30729; MDDR; .NET4.0C; .NET4.0E)
// Content-Type: multipart/form-data;
// boundary=---------------------------7db3a8184203b6
// Accept-Encoding: gzip, deflate
// Host: localhost.
// Content-Length: 247
// Connection: Keep-Alive
// Pragma: no-cache
// Cookie: JSESSIONID=A7D0F1875DF63CF25B475A49523C04DF
//
// -----------------------------7db3a8184203b6
// Content-Disposition: form-data; name="file1"; filename="D:\My
// Documents\temp.txt"
// Content-Type: text/plain
//
// C572e9f35aafa0b32496da3641b32ce24##1#9
// -----------------------------7db3a8184203b6--
//
