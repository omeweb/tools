package tools.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 不主动关闭HttpURLConnection，一旦被消费，则自行关闭连接
 * 
 * @author liusan.dyf
 */
public class HttpResponse {
	private HttpURLConnection connection;
	private InputStream stream;
	private String responseString = null;// 缓存的响应内容
	/**
	 * 消息是否被消费，消费则意味着连接要关闭，而且不能多次消费
	 */
	private boolean isClosed = false;

	/**
	 * 服务器响应code，200表示OK
	 */
	private int responseCode = 0;

	/**
	 * 2012-01-30
	 */
	private static final String EMPTY = "";

	public int getResponseCode() {
		return responseCode;
	}

	// public void setResponseCode(int responseCode) {
	// this.responseCode = responseCode;
	// }

	/**
	 * 来自服务器的header信息，有的header有多个值，所以是List，比如set-cookie
	 */
	private Map<String, List<String>> responseHeaders;

	public Map<String, List<String>> getResponseHeaders() {
		if (responseHeaders == null)
			responseHeaders = new HashMap<String, List<String>>();
		return responseHeaders;
	}

	/**
	 * 内容编码
	 */
	private String contentCharset = HttpUtil.DEFAULT_CHARSET;// "utf-8";

	public String getContentCharset() {
		return contentCharset;
	}

	public HttpResponse setContentCharset(String contentCharset) {
		this.contentCharset = contentCharset;
		return this;
	}

	public HttpResponse(HttpURLConnection connection) {
		this.connection = connection;
		this.responseHeaders = connection.getHeaderFields();

		try {
			this.responseCode = connection.getResponseCode();
			this.stream = connection.getInputStream();

			// System.out.println(connection.getHeaderFields());
			// is-------------------
			// {null=[HTTP/1.1 200 OK]
			// Date=[Thu, 15 Dec 2011 12:56:17 GMT]
			// Content-Length=[148]
			// Content-Type=[text/plain;charset=gbk]
			// Server=[Apache-Coyote/1.1]}

		} catch (IOException e) {
			// System.out.println(connection.getResponseCode());
			// System.out.println(connection.getResponseMessage());

			System.err.println(e);// 2015-1-2 17:27:30 by 六三

			if (connection != null)
				this.stream = connection.getErrorStream();
		}

		if (null != this.getStream() && "gzip".equals(getResponseHeader("content-encoding"))) {
			// the response is gzipped
			try {
				this.stream = new GZIPInputStream(stream);
			} catch (IOException e) {

			}
		}
	}

	// /**
	// * getResponseHeader(String name)的简写形式
	// *
	// * @param name
	// * @return
	// */
	// public String header(String name) {
	// return getResponseHeader(name);
	// }

	public String getResponseHeader(String name) {
		if (responseHeaders != null) {
			List<String> list = responseHeaders.get(name);
			if (list != null && list.size() > 0)
				return list.get(0);// 只取第一个
			else
				return null;
		} else
			return null;
	}

	/**
	 * 根据ContentCharset属性获取字符串
	 * 
	 * @return
	 */
	public String getString() {
		if (responseString != null)// 先从缓存里取
			return responseString;
		else {
			if (null == getStream()) {// stream为空
				responseString = EMPTY;
				return responseString;
			}

			// 解析stream
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(getStream(), getContentCharset()));
				StringBuffer buf = new StringBuffer();
				String line;
				while (null != (line = br.readLine())) {
					buf.append(line).append("\n");
				}

				this.responseString = buf.toString();

				disconnect();// 关闭
			} catch (IOException e) {
				responseString = EMPTY;
			}
		}

		return responseString;
	}

	private void disconnect() throws IOException {
		if (isClosed)
			return;

		// 关闭流 Disconnects the internal HttpURLConnection silently
		getStream().close();
		connection.disconnect();

		// 设置为已经消费了
		isClosed = true;
	}

	/**
	 * 文本文件会出现编码问题
	 * 
	 * @param f
	 */
	public void saveAs(File f) {
		try {
			if (f == null)
				return;
			if (!f.exists())
				f.createNewFile();

			FileOutputStream s = new FileOutputStream(f);
			s.write(getBytes());
			s.close();
		} catch (IOException e) {

		}
	}

	/**
	 * 改为私有方法，该stream还需要关闭以及断开连接 2012-11-09 by liusan.dyf
	 * 
	 * @return
	 */
	private InputStream getStream() {
		if (isClosed) {
			throw new IllegalStateException("Stream has already been consumed.");
		}
		return stream;
	}

	public byte[] getBytes() throws IOException {
		if (isClosed) {
			throw new IllegalStateException("Stream has already been consumed.");
		}
		byte[] b = HttpUtil.inputStreamToByte(stream);

		disconnect();

		return b;
	}

	public String toString() {
		return toString(null);
	}

	public String toString(String charset) {
		if (charset != null)
			this.setContentCharset(charset);
		return getString();
	}
}
