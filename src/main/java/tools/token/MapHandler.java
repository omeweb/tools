package tools.token;

import java.util.Map;

public class MapHandler implements TokenHandler {

	private Map<String, Object> vars;

	public MapHandler(Map<String, Object> vars) {
		this.vars = vars;
	}

	@Override
	public String handle(String token) {
		if (vars == null)
			return "";

		Object obj = vars.get(token);
		if (obj == null)
			return "";

		return obj.toString();
	}
}
