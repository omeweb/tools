package tools;

import java.util.Map;

/**
 * 适用于有id、parentId的树状类目判断
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-7-1
 */
public class CategoryService {
	/**
	 * key=id，value=parentId
	 */
	private Map<Integer, Integer> map = MapUtil.create();

	public void loadFrom(Map<Integer, Integer> map) {
		if (map != null)
			this.map = map;
	}

	public Map<Integer, Integer> getData() {
		return map;
	}

	/**
	 * @param id
	 * @param pid 0 表示顶级类别
	 * @return
	 */
	public boolean isChildOf(int id, int pid) {
		if (pid == id || pid == 0)
			return true;

		Integer temp = id;// 临时parent id

		while ((temp = map.get(temp)) != null) {
			if (temp == pid)
				return true;
		}

		return false;
	}

	/**
	 * @param child
	 * @param pidList eg "1,2,3"
	 * @return
	 */
	public boolean isChildOfAny(Object child, String pidList) {
		int id = Convert.toInt(child, -1);
		if (id <= 0)// 对于子类目，-1或者0都是无效值，返回false
			return false;

		String[] arr = StringUtil.split(pidList, ",");

		int pid = -1;// -1表示父类目里的无效值
		for (String item : arr) {
			pid = Convert.toInt(item, -1);

			if (pid != -1 && isChildOf(id, pid)) {
				return true;
			}
		}

		return false;
	}

	public static void main(String[] args) {
		// 构造树
		Map<Integer, Integer> map = MapUtil.create();
		map.put(1, 0);
		map.put(2, 1);
		map.put(3, 1);
		map.put(4, 3);
		map.put(5, 3);

		// 匹配测试
		CategoryService entry = new CategoryService();
		entry.loadFrom(map);

		System.out.println(entry.isChildOf(5, 1));

		System.out.println(entry.isChildOfAny(5, "4,2"));
	}
}
