package tools.http;

public class HttpMethod {
	public static final String GET = "GET";
	public static final String POST = "POST";
	/**
	 * 如果你预先就对响应内容不感兴趣，你可以使用HEAD 请求来代替GET请求。 例如，获取web资源的meta信息或者测试它的有效性，可访问性以及最近的修改。
	 */
	public static final String HEAD = "HEAD";
	public static final String OPTIONS = "OPTIONS";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String TRACE = "TRACE";
}
