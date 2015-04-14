package tools.session;

import tools.ObjectConverter;
import tools.StoreService;
import tools.StringUtil;

/**
 * 把session序列化为string，存储到某地方，返回反序列化回来 2012-02-27<br />
 * 注意，存储的时候，需要md5签名验证
 * 
 * @author liusan.dyf
 * @param <T>
 */
public abstract class SessionServiceBase<T> {
	private ObjectConverter<T, String> converter;
	private StoreService<String> storer;

	/**
	 * salt在编码后的串里不可见
	 * 
	 * @return
	 */
	public String getSalt() {
		return StringUtil.EMPTY;
	}

	public void set(T entry) {
		// 写入cookie
		set(entry, 3600 * 24 * 30); // 默认30天
	}

	public void remove() {
		storer.remove();
	}

	public final void set(T entry, int seconds) {
		String value = getStringFromEntryInternal(entry);
		storer.set(value, seconds);
	}

	public T get() {
		String v = storer.get();
		if (StringUtil.isNullOrEmpty(v)) {
			storer.remove();// 2012-10-08，防止无效的string一直存在
			return null;
		}

		T r = getEntryFromStringInternal(v);

		if (r == null) {
			storer.remove();// 2012-10-08，防止无效的string一直存在
			return null;
		}

		return r;
	}

	String getStringFromEntryInternal(T entry) {
		String s = converter.to(entry);// getStringFromEntry(entry);

		return StringUtil.signContent(s, getSalt());
	}

	T getEntryFromStringInternal(String str) {
		String raw = StringUtil.getRawContent(str, getSalt());
		// System.out.println("getEntryFromStringInternal:" + str);
		if (raw != null)
			return converter.from(raw);// getEntryFromString(raw);
		else {
			return null;
		}
	}

	public ObjectConverter<T, String> getConverter() {
		return converter;
	}

	public void setConverter(ObjectConverter<T, String> converter) {
		this.converter = converter;
	}

	public StoreService<String> getStorer() {
		return storer;
	}

	public void setStorer(StoreService<String> storer) {
		this.storer = storer;
	}
}
