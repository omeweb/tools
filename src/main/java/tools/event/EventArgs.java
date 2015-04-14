package tools.event;

import java.util.HashMap;

public class EventArgs {
	private final static String MAIN_KEY = "main";
	private final static String TYPE_KEY = "type";

	private final HashMap<String, Object> data = new HashMap<String, Object>();

	/**
	 * event的type，比如hook的名称 2012-06-01
	 */
	// private String type;

	public static EventArgs create(Object main) {
		EventArgs rtn = new EventArgs();
		rtn.set(MAIN_KEY, main);
		return rtn;
	}

	/* 主对象getter and setter */

	public Object getMainObject() {
		return get(MAIN_KEY);
	}

	public EventArgs setMainObject(Object main) {
		return set(MAIN_KEY, main);
	}

	/* 普通 */

	public Object get(String key) {
		return this.data.get(key);
	}

	public EventArgs set(String key, Object value) {
		this.data.put(key, value);
		return this;
	}

	public String getType() {
		Object v = get(TYPE_KEY);
		if (v == null)
			return null;
		return v.toString();
	}

	public EventArgs setType(String type) {
		return set(TYPE_KEY, type);
	}
}
