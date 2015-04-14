package tools.generator;

public interface DataStore {
	long get(String key);

	boolean set(String key, long value);
}
