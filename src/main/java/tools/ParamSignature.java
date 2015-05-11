package tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2015年5月4日
 */
public class ParamSignature {
	private Set<String> ignoreSet = new HashSet<String>();
	private String encoding = "utf-8";
	private String timestampKey = "t";// "timestamp";

	private String signKey = "sign";
	private String signTypeKey = "sign_type";

	private boolean validateTimestamp = true;
	private String salt = "9527.&*^#$@!~";
	private int maxAge = 60 * 30;// x分钟内

	private static final int ENCODE_VALUE = 10;
	private static final int DECODE_VALUE = 20;
	private static final int RAW_VALUE = 0;

	// 一些响应码，可以给外部使用
	public static final int OK = 0;
	public static final int INVALID_SIGNATURE = 10;
	public static final int INVALID_TIMESTAMP = 20;
	public static final int EXPIRED_TIMESTAMP = 30;
	public static final int INVALID_SIGNATURE_TYPE = 40;

	public static void main(String[] args) throws Throwable {
		ParamSignature entry = new ParamSignature();

		// 构造参数
		Map<String, String> map = tools.MapUtil.create();
		map.put("userName", "六三");
		map.put("age", "30");

		// 签名
		String x = entry.createSignedPostData(map, "md5");
		System.out.println(x);

		// 验证签名
		Map<String, String> p = tools.StringUtil.parseQueryString(
				"age=30&t=1430747319&userName=%E5%85%AD%E4%B8%89&sign=c6039646000feea81e1e28042e4641e9&sign_type=md5",
				entry.getEncoding());

		System.out.println(entry.validate(p));

		//
		System.out.println(URLDecoder.decode("%E5%85%AD,%E4%B8%89", "utf-8"));

		//
		String res = tools.http.HttpRequestBuilder
				.create("http://127.0.0.1/api/getUser")
				.data("age=30&t=1430828249&userName=%E5%85%AD%E4%B8%89&sign=5df8e89dceb57e1842efc62b7b4a2946&sign_type=md5")
				.charset("utf-8").get().toString("utf-8");
		System.out.println(res);
	}

	/**
	 * map要事先解码好
	 * 
	 * @param map
	 * @return
	 */
	public int validate(Map<String, String> map) {
		try {
			String exp = this.createSignatureString(map, RAW_VALUE);// 这里假定传入的参数已经是解码好了的 2015-5-11 11:35:01
			String type = map.get(signTypeKey);

			exp = sign(exp, type);
			String act = map.get(signKey);
			if (exp.equalsIgnoreCase(act)) {// 不区分大小写
				if (validateTimestamp) {// 验证时间
					long ts = tools.Convert.toLong(map.get(timestampKey), 0);
					if (ts == 0)
						return INVALID_TIMESTAMP;

					long now = tools.Convert.toUnixTime();
					long diff = now - ts;// 参数里的timestamp肯定要比now的小
					if (diff <= maxAge)
						return OK;
					else
						return EXPIRED_TIMESTAMP;
				}
				return OK;
			} else
				return INVALID_SIGNATURE;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return INVALID_SIGNATURE;
	}

	public void addIgnore(String v) {
		if (v != null)
			this.ignoreSet.add(v);
	}

	/**
	 * url里面不带任何参数的，生成的url会已get的方式进行请求
	 * 
	 * @param url
	 * @param map
	 * @param signType
	 * @return
	 */
	public String createSignedUrl(String url, Map<String, String> map, String signType) {
		if (url != null) {
			if (url.indexOf('?') > -1 || url.indexOf('&') > -1)
				throw new IllegalArgumentException("url里不能包含queryString：" + url);
		}

		map.put(getTimestampKey(), tools.Convert.toUnixTime() + "");// 时间也要参与签名

		try {
			String str = this.createSignatureString(map, RAW_VALUE);
			String signed = sign(str, signType);
			String paramString = this.createSignatureString(map, ENCODE_VALUE) + "&" + signKey + "=" + signed + "&"
					+ signTypeKey + "=" + signType;

			if (url == null || url.isEmpty())
				return paramString;
			else
				return url + "?" + paramString;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String createSignedPostData(Map<String, String> map, String signType) {
		return createSignedUrl(null, map, signType);
	}

	/**
	 * type目前仅支持md5
	 * 
	 * @param v
	 * @param type
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String sign(String v, String type) throws UnsupportedEncodingException {
		// TODO type目前只能为md5
		byte[] source = (v + salt).getBytes(encoding);
		return tools.StringUtil.md5(source);
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串，区分大小写，key和value都参与签名
	 * 
	 * @param map
	 * @param action 10=编码，20=解码，0=什么也不做
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String createSignatureString(Map<String, String> map, int action) throws UnsupportedEncodingException {
		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);

		int len = keys.size();

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			String key = keys.get(i);
			String value = map.get(key);

			if (ignoreSet != null && ignoreSet.contains(key)) // 判断key
				continue;

			if (key.equals(signKey) || key.equals(signTypeKey))
				continue;

			if (value == null || value.isEmpty())// 判断value
				continue;

			if (this.encoding != null) {
				if (action == DECODE_VALUE) {// 解码
					// http://stackoverflow.com/questions/16527576/httpservletrequest-utf-8-encoding
					value = URLDecoder.decode(value, encoding);
				} else if (action == ENCODE_VALUE) {// 编码
					value = URLEncoder.encode(value, encoding);
				}
			}

			if (i == len - 1) {// 拼接时，不包括最后一个&字符
				sb.append(key).append("=").append(value);
			} else {
				sb.append(key).append("=").append(value).append("&");
			}
		}

		return sb.toString();
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isValidateTimestamp() {
		return validateTimestamp;
	}

	public void setValidateTimestamp(boolean validateTimestamp) {
		this.validateTimestamp = validateTimestamp;
	}

	public String getTimestampKey() {
		return timestampKey;
	}

	public void setTimestampKey(String timestampKey) {
		this.timestampKey = timestampKey;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	public String getSignTypeKey() {
		return signTypeKey;
	}

	public void setSignTypeKey(String signTypeKey) {
		this.signTypeKey = signTypeKey;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}
}
