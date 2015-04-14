package tools.oauth;

public class OAuthException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4164945653610362059L;

	private OAuthError error;

	public OAuthError getError() {
		return error;
	}

	public void setError(OAuthError error) {
		this.error = error;
	}

	public OAuthException(OAuthError error) {
		this.error = error;
	}
}
