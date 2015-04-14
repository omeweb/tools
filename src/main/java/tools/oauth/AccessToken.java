package tools.oauth;

import java.util.HashMap;
import java.util.Map;

public class AccessToken extends HashMap<String, Object> {
	private static final long serialVersionUID = -3517561924035633537L;

	// #state=testState&access_token=2.00u3Is_CpmjhRC9787a40fcc0G8lxT&expires_in=86400&uid=2125917932
	public String getToken() {
		return super.get(FieldConstant.ACCESS_TOKEN) + "";
	}

	public String getState() {
		return super.get(FieldConstant.STATE) + "";
	}

	public String getRefreshToken() {
		return super.get(FieldConstant.REFRESH_TOKEN) + "";
	}

	public long getExpires() {
		Object o = super.get(FieldConstant.EXPIRES_IN);
		if (o == null)
			return 0;

		// 这样可以兼容long和string类型的字段
		return Long.parseLong(super.get(FieldConstant.EXPIRES_IN) + "");
	}

	public AccessToken(Map<String, Object> map) {
		super.putAll(map);
	}

	public Object get(String key) {
		return super.get(key);
	}
}
