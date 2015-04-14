package tools.oauth;

public enum GrantType {
	AUTHORIZATION_CODE("authorization_code"), PASSWORD("password"), REFRESH_TOKEN("refresh_token");

	private final String value;

	private GrantType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
