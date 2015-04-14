package tools.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tools.http.HttpPostedFile;
import tools.http.HttpRequestBuilder;
import tools.http.HttpResponse;
import tools.http.HttpUtil;

public class OAuthRequestContext {
	/**
	 * 申请应用时分配的AppKey
	 */
	private String clientId;
	private String clientSecret;
	private String defaultRedirectUri;

	private ProviderDefinition authorizeProvider;
	private ProviderDefinition accessTokenProvider;

	public String getUrl(ProviderDefinition de, Display display, String redirectUri, ResponseType responseType,
			String state) {

		// 可以参见 http://open.weibo.com/wiki/Oauth2/authorize

		// ProviderDefinition
		if (de == null)
			throw new IllegalArgumentException("authorizeProvider不能为null");

		// 判断action，只能为get
		if (!"GET".equalsIgnoreCase(de.getMethod())) {
			throw new IllegalArgumentException("authorizeProvider的method只能为get");
		}

		String url = de.getUrl();
		int index = url.indexOf('?');

		// 拼接url
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		if (index == -1)
			sb.append('?');

		Map<String, String> params = new HashMap<String, String>();
		params.put(FieldConstant.DISPLAY, display.getValue());
		params.put(FieldConstant.CLIENT_ID, getClientId());
		params.put(FieldConstant.REDIRECT_URI,
				redirectUri == null || redirectUri.length() == 0 ? getDefaultRedirectUri() : redirectUri);
		params.put(FieldConstant.RESPONSE_TYPE, responseType.getValue());
		params.put(FieldConstant.STATE, state);

		sb.append(HttpUtil.toUrlEncodedString(params, "utf-8"));

		return sb.toString();
	}

	/**
	 * 该方法并不与服务器产生交互<br />
	 * 1，只针对get情况下，获取url<br />
	 * 2，手动跳转过去<br />
	 * 3，授权<br />
	 * 4，跳转回redirectUri，可能是code，也可能是token，取决于responseType<br />
	 * 
	 * @param display
	 * @param redirectUri
	 * @param responseType
	 * @param state
	 * @return
	 */
	public String getAuthorizeUrl(Display display, String redirectUri, ResponseType responseType, String state) {

		// 可以参见 http://open.weibo.com/wiki/Oauth2/authorize

		return this.getUrl(getAuthorizeProvider(), display, redirectUri, responseType, state);
	}

	public HttpResponse doRequestByAccessToken(String url, String method, Map<String, String> data,
			List<HttpPostedFile> list, String token) throws Exception {

		HttpRequestBuilder http = createOAuthRequestBuilder(url, token);

		HttpResponse res = http.data(data).file(list).execute(method);

		// {"error":"expired_token","error_code":21327,"request":"/2/statuses/home_timeline.json"}
		return res;
	}

	public HttpRequestBuilder createOAuthRequestBuilder(String url, String token) {
		HttpRequestBuilder http = HttpRequestBuilder.create(url).data(FieldConstant.ACCESS_TOKEN, token);

		return http;
	}

	public AccessToken refreshAccessToken(String refreshToken, String state) throws Exception {
		// https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=password&username=USER_NAME&password=PASSWORD

		HttpRequestBuilder http = HttpRequestBuilder.create(this.getAccessTokenProvider().getUrl())
				.data(FieldConstant.REFRESH_TOKEN, refreshToken).data(FieldConstant.CLIENT_ID, this.getClientId())
				.data(FieldConstant.CLIENT_SECRET, this.getClientSecret())
				.data(FieldConstant.GRANT_TYPE, GrantType.REFRESH_TOKEN.getValue()).data(FieldConstant.STATE, state);

		// http.proxy("localhost", 8888);
		// http.multipart(true);
		HttpResponse res = http.execute(this.getAccessTokenProvider().getMethod());

		return getAccessTokenFromResponse(res);
	}

	public AccessToken getAccessTokenByPassword(String userName, String password, String state) throws Exception {
		// https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=password&username=USER_NAME&password=PASSWORD

		HttpRequestBuilder http = HttpRequestBuilder.create(this.getAccessTokenProvider().getUrl())
				.data(FieldConstant.USERNAME, userName).data(FieldConstant.PASSWORD, password)
				.data(FieldConstant.CLIENT_ID, this.getClientId())
				.data(FieldConstant.CLIENT_SECRET, this.getClientSecret())
				.data(FieldConstant.GRANT_TYPE, GrantType.PASSWORD.getValue()).data(FieldConstant.STATE, state);

		// http.proxy("localhost", 8888);
		// http.multipart(true);
		HttpResponse res = http.execute(this.getAccessTokenProvider().getMethod());

		return getAccessTokenFromResponse(res);
	}

	/**
	 * 请求accessTokenProvider
	 * 
	 * @param grantCode
	 * @param redirectUri
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public AccessToken getAccessTokenByCode(String grantCode, String redirectUri, String state) throws Exception {
		// https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
		HttpRequestBuilder http = HttpRequestBuilder
				.create(this.getAccessTokenProvider().getUrl())
				.data(FieldConstant.CODE, grantCode)
				.data(FieldConstant.REDIRECT_URI,
						redirectUri == null || redirectUri.length() == 0 ? getDefaultRedirectUri() : redirectUri)
				.data(FieldConstant.CLIENT_ID, this.getClientId())
				.data(FieldConstant.CLIENT_SECRET, this.getClientSecret())
				.data(FieldConstant.GRANT_TYPE, GrantType.AUTHORIZATION_CODE.getValue())
				.data(FieldConstant.STATE, state);

		// http.proxy("localhost", 8888);
		// http.multipart(true);
		HttpResponse res = http.execute(this.getAccessTokenProvider().getMethod());

		return getAccessTokenFromResponse(res);
	}

	private AccessToken getAccessTokenFromResponse(HttpResponse res) throws JsonParseException, JsonMappingException,
		IOException, OAuthException {
		System.out.println(res.toString());
		String result = res.toString();

		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = new ObjectMapper().readValue(result, HashMap.class);

		if (res.getResponseCode() == 200) {
			return new AccessToken(map);
		} else {
			throw new OAuthException(new OAuthError(map));
		}
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public ProviderDefinition getAuthorizeProvider() {
		return authorizeProvider;
	}

	public void setAuthorizeProvider(ProviderDefinition authorizeProvider) {
		this.authorizeProvider = authorizeProvider;
	}

	public ProviderDefinition getAccessTokenProvider() {
		return accessTokenProvider;
	}

	public void setAccessTokenProvider(ProviderDefinition accessTokenProvider) {
		this.accessTokenProvider = accessTokenProvider;
	}

	public String getDefaultRedirectUri() {
		return defaultRedirectUri;
	}

	public void setDefaultRedirectUri(String defaultRedirectUri) {
		this.defaultRedirectUri = defaultRedirectUri;
	}

	public static void main(String[] args) throws Exception {
		// core
		OAuthRequestContext context = new OAuthRequestContext();
		context.setClientId("2350731631");
		context.setClientSecret("1dd8c08e823d6ea32c9e184056a1ddf6");
		context.setDefaultRedirectUri("http://auth.xml5.com/oauth/callback");

		// 参数一：authorizeProvider
		ProviderDefinition authorizeProvider = new ProviderDefinition();
		authorizeProvider.setUrl("https://api.weibo.com/oauth2/authorize");
		authorizeProvider.setMethod("GET");
		context.setAuthorizeProvider(authorizeProvider);

		// 参数二：accessTokenProvider
		ProviderDefinition accessTokenProvider = new ProviderDefinition();
		accessTokenProvider.setUrl("https://api.weibo.com/oauth2/access_token");
		accessTokenProvider.setMethod("POST");
		context.setAccessTokenProvider(accessTokenProvider);

		// 构造一个url：这个url要在浏览器里调整过去，回调里会有code参数
		String url = context.getAuthorizeUrl(Display.DEFAULT, null, ResponseType.CODE, "testState");
		System.out.println(url);

		// 通过上面url的交互后，有个回调来获取access token
		// 这里仅仅做一次 2015-4-6 20:01:41 by 六三
		// AccessToken token = context.getAccessTokenByCode("0723f20765a218b7c9d2a81dd568d6cf", null, null);
		// System.out.println(token.getToken());

		// access token
		// 请求个人微博列表
		String tokenString = "2.00pdW6NB_g7FZCd518a4734dq9wPCE";
		HttpResponse res = context.doRequestByAccessToken("https://api.weibo.com/2/statuses/user_timeline.json", "get",
				null, null, tokenString);
		System.out.println(res);

		// 发布图片微博
		HttpPostedFile f = new HttpPostedFile();
		f.setFieldName("pic");
		f.setFileName("32ea26250b038a5530d4bc91f7348732.jpg");
		// f.setContent(new FileInputStream(new File("d:/32ea26250b038a5530d4bc91f7348732.jpg")));
		f.setContent("http://ww1.sinaimg.cn/bmiddle/829b959djw1dyjxs9olung.gif");

		res = context.createOAuthRequestBuilder("https://api.weibo.com/2/statuses/upload.json", tokenString)
				.data("status", "让狗狗去捡球。。。结果回来时把我笑趴了").file(f).multipart(true).post();
		System.out.println(res);
	}
}

// response_type为token
// //请求
// https://api.t.sina.com.cn/oauth2/authorize?client_id=123050457758183&redirect_uri=http://www.example.com/response&response_type=token
//
// //同意授权后会重定向
// http://www.example.com/response#access_token=ACCESS_TOKEN&expires_in=250327040&refresh_token=REFRESH_TOKEN
//
//
//
// response_type为code
// //请求
// https://api.t.sina.com.cn/oauth2/authorize?client_id=123050457758183&redirect_uri=http://www.example.com/response&response_type=code
//
// //同意授权后会重定向
// http://www.example.com/response&code=CODE
