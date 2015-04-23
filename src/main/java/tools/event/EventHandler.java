package tools.event;

public interface EventHandler {
	/**
	 * 要设置args.type属性，这是hook点，一来可以在event holder里找到注册的事件列表；二来也表示了事件本身的类型
	 * 
	 * @param sender
	 * @param args
	 */
	void fire(Object sender, EventArgs args);
}
