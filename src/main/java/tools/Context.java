package tools;

public class Context<T> implements StoreService<T> {
	public final ThreadLocal<T> store = new ThreadLocal<T>();

	public T get() {
		return store.get();
	}

	public void set(T t) {
		store.set(t);
	}

	public void remove() {
		store.remove();
	}

	@Override
	public void set(T value, int seconds) {
		store.set(value);
	}
}
