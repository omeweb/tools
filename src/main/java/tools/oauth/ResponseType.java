package tools.oauth;

public enum ResponseType {
	CODE("code"), TOKEN("token");

	private final String value;

	private ResponseType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
