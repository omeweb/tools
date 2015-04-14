package tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tools.http.HttpRequestBuilder;
import tools.http.HttpResponse;
import tools.http.HttpUtil;
import tools.jsonrpc.SingleInvokeResult;

/**
 * 发送jsonrpc的http请求，并获取结果。其中httpParams支持responseCharset、cookie、charset、obj、urlSuffix、、、、、、、、、 <br />
 * 请求协议是私有的：post内容jsonrpcContent到url上即可
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-9-4
 */
public class JsonRpcUtil {
	private static final Log logger = LogFactory.getLog("system");// 日志

	/**
	 * 发送jsonrpc请求，只要发送成功即可，不关心结果是什么
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @param httpParams
	 * @return
	 */
	public static boolean request(String url, String method, Map<String, Object> params, Map<String, Object> httpParams) {
		return getResponse(url, method, params, httpParams) != null;
	}

	/**
	 * 获取jsonrpc请求结果
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @param httpParams
	 * @return
	 */
	public static String getResponse(String url, String method, Map<String, Object> params,
			Map<String, Object> httpParams) {
		// 参数校验
		if (Validate.isNullOrEmpty(url))
			return null;

		if (url.startsWith("//") || url.startsWith("#"))// 2013-03-28 by liusan.dyf 当作注释的开始
			return null;

		int fixedTimeout = Convert.toInt(httpParams.get("timeout"), 1000 * 2);

		// 构造请求报文，这个是自己的标准
		Map<String, Object> body = tools.MapUtil.create();
		body.put("id", 1);
		body.put("method", method);
		body.put("jsonrpc", "2.0");
		body.put("params", params);

		String jsonrpc = Json.toString(body);
		// System.out.println(jsonrpc);

		// 修正url，默认是http协议 2013-08-28 by liusan.dyf
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;

		// url后缀，传入的url可能是个host名称
		String urlSuffix = "";
		if (httpParams != null) {
			String tempKey = "urlSuffix";
			if (httpParams.containsKey(tempKey))
				urlSuffix = (String) httpParams.get(tempKey);
		}
		url += urlSuffix;

		// 请求
		try {
			// 发送请求
			HttpRequestBuilder builder = HttpRequestBuilder.create(url).data("jsonrpcContent", jsonrpc)
					.connectTimeout(fixedTimeout).timeout(fixedTimeout);

			if (httpParams != null) {
				String tempKey = "cookie";
				if (httpParams.containsKey(tempKey))
					builder.cookie((String) httpParams.get(tempKey));

				tempKey = "charset";
				if (httpParams.containsKey(tempKey))
					builder.charset((String) httpParams.get(tempKey));

				tempKey = "obj";// 协议相关的一个参数 2013-08-28 by liusan.dyf
				if (httpParams.containsKey(tempKey))
					builder.data(tempKey, (String) httpParams.get(tempKey));
			}
			// .proxy("127.0.0.1", 8888)
			HttpResponse res = builder.post();

			// 结果判断
			// String r = res.setContentCharset("utf-8").getString();
			// return r.indexOf("error") == -1;

			// System.out.println( res.setContentCharset("utf-8").getString());

			// 响应的编码
			String responseCharset = null;
			if (httpParams != null) {
				String tempKey = "responseCharset";
				if (httpParams.containsKey(tempKey))
					responseCharset = (String) httpParams.get(tempKey);
			}

			if (res.getResponseCode() == 200)
				return res.toString(responseCharset);
			else {
				logger.warn("请求失败：url=" + url + "，code=" + res.getResponseCode());

				return null;
			}
		} catch (Exception e) {
			logger.warn("请求失败：url=" + url + "，message=" + e.getMessage());
		}

		return null;
	}

	/**
	 * 批量发起jsonrpc请求
	 * 
	 * @param n 线程数
	 * @param arr url列表
	 * @param method GET/POST
	 * @param params
	 * @param httpParams
	 * @return
	 */
	public static Map<String, String> requestList(int n, Collection<String> arr, final String method,
			final Map<String, Object> params, final Map<String, Object> httpParams) {

		final Map<String, String> rtn = MapUtil.concurrentHashMap(arr.size());

		/**
		 * 并行方案 2012-12-20 by liusan.dyf
		 */
		List<Runnable> jobs = new ArrayList<Runnable>();

		for (String item : arr) {
			// final版本的item
			final String itemx = item;

			// 加到队列里去
			jobs.add(new Runnable() {
				@Override
				public void run() {

					String resp = getResponse(itemx, method, params, httpParams);

					// 加入到返回结果里
					if (!tools.Validate.isNullOrEmpty(resp))
						rtn.put(itemx, resp);
				}
			});
		}

		// 调整线程数
		if (n <= 0)
			n = 10;
		tools.concurrent.Parallel.each(jobs, n);// 多个线程并行执行

		return rtn;
	}

	public static Object parseResult(String json) {
		SingleInvokeResult r = Json.toObject(json,
				new com.fasterxml.jackson.core.type.TypeReference<SingleInvokeResult>() {
				});
		if (r == null)
			return null;
		return r.getResult();
	}

	public static void main(String[] args) {
		String url = "mtee.admin.taobao.org";

		// jsonrpc参数
		Map<String, Object> params = tools.MapUtil.create();
		params.put("typeCode", "50");
		params.put("key", "clusterIpList");

		// http其他参数
		Map<String, Object> httpParams = MapUtil.create();
		String c = "auth=" + HttpUtil.encode("666172:六三:*:1363229031:573f16dce4b82a50b265c00931fcaa82", "utf-8");
		httpParams.put("cookie", c);
		httpParams.put("charset", "gbk");
		httpParams.put("responseCharset", "gbk");
		httpParams.put("obj", "kv");
		httpParams.put("urlSuffix", "/s.do");

		Collection<String> urlList = new ArrayList<String>();
		urlList.add(url);

		Object result = JsonRpcUtil.requestList(1, urlList, "getOne", params, httpParams);
		System.out.println(result);
	}
}
