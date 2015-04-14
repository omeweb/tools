package tools;

import java.util.Collection;

/**
 * 分页辅助类 2011-11-07
 * 
 * @author liusan.dyf
 * @param <T>
 */
public class PagedList<T> {
	private int count;
	private Collection<T> list;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Collection<T> getList() {
		return list;
	}

	public void setList(Collection<T> list) {
		this.list = list;
	}
}
