package tools.event;

/**
 * 2012-08-06 用静态属性和方法，简化使用<br />
 * AbstractDao无法直接实例化，这里提供一个EventContainerManager，让AbstractDao直接使用静态方法。如同SqlMapClientManager。
 * 
 * @author liusan.dyf
 */
public class EventContainerManager {
	private static EventContainer eventContainer;
	private static Object locker = new Object();

	public static EventContainer getEventContainer() {
		return getEventContainer(null);
	}

	public static EventContainer getEventContainer(String biz) {
		if (eventContainer == null) {// 2014-03-07 by liusan.dyf
			synchronized (locker) {
				if (eventContainer == null)
					eventContainer = new EventContainer();
			}
		}

		return eventContainer;
	}

	public static void setEventContainer(EventContainer value) {
		eventContainer = value;
	}

	public static void onEvent(Object sender, EventArgs args) {
		if (eventContainer == null)
			return;
		eventContainer.onEvent(sender, args);
	}
}
