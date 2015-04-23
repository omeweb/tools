package tools.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventContainer {
	private Map<String, List<EventHandler>> handlers = null;

	/**
	 * 2012-06-01 by liusan.dyf map的key= hook name
	 */
	public Map<String, List<EventHandler>> getHandlers() {
		return handlers;
	}

	public int size() {
		if (handlers != null)
			return handlers.size();

		return 0;
	}

	public void clearAll() {
		if (handlers != null)
			handlers.clear();
	}

	/**
	 * 清理掉一个hook下的所有的EventHandler 2013-12-16 by liusan.dyf
	 * 
	 * @param hook
	 */
	public void clearHandlers(String hook) {
		if (handlers != null)
			handlers.remove(hook);
	}

	/**
	 * 2013-12-16 by liusan.dyf
	 * 
	 * @param hook
	 * @param handler
	 */
	public void addHandler(String hook, EventHandler handler) {
		// 2013-12-16 by liusan.dyf
		if (handlers == null)
			handlers = tools.MapUtil.create();

		// 查找事件
		List<EventHandler> list = handlers.get(hook);
		if (list == null)
			list = new ArrayList<EventHandler>();

		list.add(handler);

		handlers.put(hook, list);
	}

	public void setHandlers(Map<String, List<EventHandler>> v) {
		this.handlers = v;
	}

	/**
	 * 通过EventArgs的type属性来查找注册的事件列表 2012-07-05
	 * 
	 * @param sender
	 * @param args
	 */
	public void onEvent(Object sender, EventArgs args) {
		if (args == null || handlers == null)
			return;

		// 是否存在type 2012-07-05
		String hook = args.getType();
		if (hook == null)
			return;

		execute(hook, sender, args);
		execute("*", sender, args);// 2012-08-02
	}

	private void execute(String hook, Object sender, EventArgs args) {
		// 查找事件
		List<EventHandler> list = handlers.get(hook);
		if (list == null)
			return;

		// 循环执行
		for (EventHandler item : list)
			item.fire(sender, args);
	}
}
