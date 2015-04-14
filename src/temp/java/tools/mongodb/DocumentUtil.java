package tools.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.Json;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DocumentUtil {
	/**
	 * 把DBObject里的_id映射为class的id属性上
	 * 
	 * @param dbObject
	 * @param clz
	 * @return
	 */
	public static <T> T toObject(DBObject dbObject, Class<T> clz) {
		if (dbObject == null)
			return null;

		// 修改id，把_id换为id
		Object id = dbObject.get("_id");
		dbObject.put("id", id);
		// dbObject.removeField("_id");// 可以不用删除 2012-02-01

		T rtn = Json.toObject(dbObject.toString(), clz);

		return rtn;
	}

	/**
	 * 只把object的id属性当作mongo的object_id，注意Date类型的值要修正下
	 * 
	 * @param value
	 * @return
	 */
	public static DBObject toMongodbObject(Object value) {
		Map<?, ?> map = Json.toMap(value);
		if (map == null)
			return null;

		BasicDBObject document = new BasicDBObject();
		document.putAll(map);

		// 循环下，把value为null的干掉
		List<String> keys = new ArrayList<String>(document.size());
		for (Map.Entry<String, Object> item : (Set<Map.Entry<String, Object>>) document.entrySet()) {
			if (item.getValue() == null) {
				keys.add(item.getKey());
				// document.remove(item.getKey());
				// java.util.ConcurrentModificationException
			}
		}

		for (String item : keys)
			document.removeField(item);

		// 修正id的问题
		String key = "id";
		Object oid = document.get(key);
		if (oid != null) {
			document.remove(key);
			document.put("_id", oid);
		}

		return document;
	}
}
