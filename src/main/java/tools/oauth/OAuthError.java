package tools.oauth;

import java.util.HashMap;
import java.util.Map;

/**
 * {"request":"/2/oauth2/access_token","error_code":10021,"error": "HTTP METHOD is not suported for this request!"}
 * 
 * @author liusan.dyf
 */
public class OAuthError extends HashMap<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -536848467094671187L;

	public static final OAuthError REDIRECT_URI_MISMATCH = new OAuthError(21322, "redirect_uri_mismatch", "重定向地址不匹配",
			null);

	public static final OAuthError INVALID_REQUEST = new OAuthError(21323, "invalid_request", "请求不合法", null);

	public static final OAuthError INVALID_CLIENT = new OAuthError(21324, "invalid_client",
			"client_id或client_secret参数无效", null);

	public static final OAuthError INVALID_GRANT = new OAuthError(21325, "invalid_grant",
			"提供的Access Grant是无效的、过期的或已撤销的", null);

	public static final OAuthError UNAUTHORIZED_CLIENT = new OAuthError(21326, "unauthorized_client ", "客户端没有权限", null);

	public static final OAuthError EXPIRED_TOKEN = new OAuthError(21327, "expired_token", "token过期", null);

	public static final OAuthError UNSUPPORTED_GRANT_TYPE = new OAuthError(21328, "unsupported_grant_type",
			"不支持的 GrantType", null);

	public static final OAuthError UNSUPPORTED_RESPONSE_TYPE = new OAuthError(21329, "unsupported_response_type",
			"不支持的 ResponseType", null);

	public static final OAuthError ACCESS_DENIED = new OAuthError(21330, "access_denied", "用户或授权服务器拒绝授予数据访问权限", null);

	public static final OAuthError TEMPORARILY_UNAVAILABLE = new OAuthError(21331, "temporarily_unavailable",
			"服务暂时无法访问", null);

	public OAuthError(int errorCode, String error, String errorDescription, String errorUrl) {
		super.put(FieldConstant.ERROR_CODE, errorCode);
		super.put(FieldConstant.ERROR, error);
		super.put(FieldConstant.ERROR_DESCRIPTION, errorDescription);
		super.put(FieldConstant.ERROR_URL, errorUrl);
	}

	public OAuthError(Map<String, Object> map) {
		super.putAll(map);
	}

	public int getErrorCode() {
		if (super.containsKey(FieldConstant.ERROR_CODE))
			return Integer.parseInt(super.get(FieldConstant.ERROR_CODE).toString());
		return 0;
	}

	public void setErrorCode(int errorCode) {
		super.put(FieldConstant.ERROR_CODE, errorCode);
	}

	public String getErrorUrl() {
		return super.get(FieldConstant.ERROR_URL) + "";
	}

	public void setErrorUrl(String errorUrl) {
		super.put(FieldConstant.ERROR_URL, errorUrl);
	}

	public String getErrorDescription() {
		return super.get(FieldConstant.ERROR_DESCRIPTION) + "";
	}

	public void setErrorDescription(String errorDescription) {
		super.put(FieldConstant.ERROR_DESCRIPTION, errorDescription);
	}

	public static void main(String[] args) {
		OAuthError error = new OAuthError(21322, "redirect_uri_mismatch", "重定向地址不匹配", null);
		System.out.println(error);
	}
}
