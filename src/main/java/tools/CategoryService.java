package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 适用于有id、parentId的树状节点判断，约定root节点id为0，小于0的id都不合法
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-7-1
 */
public class CategoryService {
	private static final Log logger = LogFactory.getLog("system");// 日志
	/**
	 * 最多的层数，map是外面传入的，数据可能出现质量问题(自己指向自己、环状结构)导致死循环 2016-7-22 09:56:51 by liusan.dyf
	 */
	private static final int MAX_DEPTH = 20;
	private static final int MAGIC = -9527;
	private static final List<Integer> EMPTY_LIST = new ArrayList<Integer>(0);

	private int root = 0;
	/**
	 * key=id，value=parentId
	 */
	private Map<Integer, Integer> map = MapUtil.create();

	private String version;// 2016-8-25 16:56:26 by liusan.dyf

	/**
	 * key=id，value=parentId
	 * 
	 * @param map
	 */
	public void init(Map<Integer, Integer> map) {
		if (map != null)
			this.map = map;
	}

	/**
	 * 改调用init方法，2016-8-31 16:18:58 by liusan.dyf
	 * 
	 * @param map
	 */
	@Deprecated
	public void loadFrom(Map<Integer, Integer> map) {
		init(map);
	}

	public Map<Integer, Integer> getData() {
		return map;
	}

	/**
	 * 包含自己本身的路径列表 2016-7-22 19:29:01 by liusan.dyf
	 * 
	 * @param id
	 * @return
	 */
	public List<Integer> getPath(int id) {
		if (id <= 0) {
			return EMPTY_LIST;
		}

		List<Integer> list = new ArrayList<Integer>();
		list.add(id); // 先添加自己

		// 开始查找
		int childId = id;
		Integer parentId;
		int count = 0;

		while (true) {
			parentId = map.get(childId);
			if (parentId == null)// 不存在父节点，数据错误
				return EMPTY_LIST;
			else if (parentId == childId) {
				logger.warn("CategoryService：数据错误，节点指向了自己：" + childId);
				return EMPTY_LIST;
			} else {
				list.add(parentId);
			}

			if (parentId == root)// 找到root了，退出
				return list;

			childId = parentId;// 继续循环

			// 判断查找的次数，外部数据可能出现环状数据，这里过保护
			count++;
			if (!canContinue(count, id, null)) {
				return EMPTY_LIST;// 数据错误，返回空
			}
		}
	}

	private boolean canContinue(int count, Integer id, Integer pid) {
		if (count >= MAX_DEPTH) {
			logger.warn("CategoryService：已达最大查找次数：" + MAX_DEPTH + "，入参：id=" + id + "，pid=" + pid);
			return false;
		}

		return true;
	}

	/**
	 * @param id
	 * @param pid 0 表示顶级类别
	 * @return
	 */
	public boolean isChildOf(int id, int pid) {
		if (pid == id || pid == root)// 2个相等或者pid为root
			return true;

		if (id == root && pid != root)// id已经是root但pid却不是
			return false;

		//
		int childId = id;
		Integer parentId;
		int count = 0;

		while (true) {
			parentId = map.get(childId);
			if (parentId == null)// 不存在父节点，数据错误
				return false;
			else if (parentId == pid)// 命中
				return true;
			else if (parentId == childId) {
				logger.warn("CategoryService：数据错误，节点指向了自己：" + childId);
				return false;
			}

			if (parentId == root)
				return false;

			childId = parentId;// 继续循环

			// 判断查找的次数，外部数据可能出现环状数据，这里过保护
			count++;
			if (!canContinue(count, id, pid)) {
				return false;
			}
		}
	}

	/**
	 * @param child
	 * @param pidList eg "1,2,3"
	 * @return
	 */
	public boolean isChildOfAny(Object child, String pidList) {
		int id = Convert.toInt(child, MAGIC);
		if (id == MAGIC)// 对于子节点无效值，返回false
			return false;

		String[] arr = StringUtil.split(pidList, ",");

		int pid = -1;// -1表示父节点里的无效值
		for (String item : arr) {
			pid = Convert.toInt(item.trim(), MAGIC);// 注意trim

			if (pid != MAGIC && isChildOf(id, pid)) {
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
		map.put(7, 0);
		map.put(8, 7);
		map.put(9, 8);

		// 错误的干扰数据 2016-7-22 09:45:13 by liusan.dyf
		// map.put(-1, -1);
		// map.put(0, -1);
		map.put(6, 6);

		map.put(1, 5);// 组成环状数据

		// 匹配测试
		CategoryService entry = new CategoryService();
		entry.init(map);

		System.out.println(entry.isChildOf(5, 1));// true
		System.out.println(entry.getPath(7));// [7, 0]
		System.out.println(entry.getPath(9));// [9, 8, 7, 0]
		System.out.println(entry.getPath(9527));// [9, 8, 7, 0]
		System.out.println(entry.isChildOf(6, 1));// false，节点指向了自己
		System.out.println(entry.isChildOfAny(5, "4,2"));// false，环状数据，已达最大查找次数
		System.out.println(entry.isChildOfAny(9, "7,2"));// true
		System.out.println(entry.isChildOfAny(0, "7,2"));// false

		//
		String trim = "trim";
		System.out.println(trim == trim.trim());// true，可以看trim的源码，没有空格时返回本身
	}

	public int getRoot() {
		return root;
	}

	public void setRoot(int root) {
		this.root = root;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
