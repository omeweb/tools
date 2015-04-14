package tools.token;

import java.util.List;

public class ListHandler implements TokenHandler {
	private List<Object> list;
	private int length;

	public ListHandler(List<Object> list) {
		this.list = list;

		if (this.list != null)
			length = list.size();
	}

	@Override
	public String handle(String token) {
		if (list == null)
			return "";

		int index = tools.Convert.toInt(token, -1);
		if (index == -1 || index >= length)
			return "";

		Object ele = list.get(index);
		if (ele == null)
			return "";

		return ele.toString();
	}
}
