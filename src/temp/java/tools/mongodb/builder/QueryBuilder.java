package tools.mongodb.builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.mongodb.BasicDBObject;

/**
 * A builder for creating queries. 2011-11-13
 * 
 * @author liusan.dyf
 */
public class QueryBuilder extends Builder {
	private void add(String field, String op, Object value) {
		// 目前 op=null，只用来处理eq，以及设置objectId的操作 2011-11-14 17:15

		// $or，and 特殊些，要求是一个map的集合
		boolean needMapArrayValue = op != null && (op.equals("$or") || op.equals("$and"));

		boolean needPrimitiveValue = (op == null);

		String key = needMapArrayValue ? op : field;

		BasicDBObject operates = super.getResult();

		Object storedValue = operates.get(key);

		BasicDBObject tempValue;

		if (storedValue != null) { // 有值，一般条件都是个{}
			// 处理特殊值，有些操作符，右边是个集合，这里要单独处理
			if (op.equals("$all") || op.equals("$in") || op.equals("$nin")) {
				// 这里不处理集合里重复值的问题
				tempValue = (BasicDBObject) storedValue;
				Object[] arr = (Object[]) (tempValue.get(op)); // 原来的集合，field =
																// op
				// System.out.println(arr == null);
				int i = arr.length;
				Object[] v = (Object[]) value;
				Object[] newArray = new Object[i + v.length];// 新数组
				System.arraycopy(arr, 0, newArray, 0, i);

				// 添加新值
				for (Object item : v) {
					newArray[i++] = item;
				}
				tempValue.put(op, newArray);
			} else if (needMapArrayValue) { // must多个：[{},{}]，是个BasicDBObject数组

				@SuppressWarnings("unchecked")
				List<BasicDBObject> list = (List<BasicDBObject>) storedValue;

				QueryBuilder[] v = (QueryBuilder[]) value;
				for (QueryBuilder item : v) {
					list.add(item.getResult());
				}
			} else {
				if (storedValue instanceof BasicDBObject) {
					// 如果值已经是个BasicDBObject
					tempValue = (BasicDBObject) storedValue;
					tempValue.put(op, value);// 直接覆盖值
					// conditions.put(key, operate);
				} else {
					// storedValue并不是一个BasicDBObject
					// 可能的情况是 op = eq
					// eg. eq("a", 1).gt("a", 3)

					// 当冲突时才覆盖，比如 a eq 4 被 a lt 3覆盖，对象不一样
					// 当op相同时，会被后面的操作覆盖

					if (needPrimitiveValue)
						operates.put(field, value);
					else
						operates.put(field, new BasicDBObject(op, value)); // 也是覆盖
				}
			}
		} else {
			if (needMapArrayValue) { // BasicDBObject数组
				List<BasicDBObject> list = new ArrayList<BasicDBObject>();
				QueryBuilder[] v = (QueryBuilder[]) value;
				for (QueryBuilder item : v) {
					list.add(item.getResult());
				}
				operates.put(key, list);
			} else {
				if (!needPrimitiveValue) {// 不是eq等需要原始值的操作
					operates.put(key, new BasicDBObject(op, value));
				} else
					// 2011-11-14 eq操作
					operates.put(field, value);
			}
		}
	}

	public QueryBuilder and(QueryBuilder[] values) {
		add(null, "$and", values);
		return this;
	}

	public QueryBuilder or(QueryBuilder[] values) {
		add(null, "$or", values);
		return this;
	}

	/**
	 * Tests that the named array element contains all of the values (see $all).
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public QueryBuilder all(String field, Object[] values) {
		add(field, "$all", values);
		return this;
	}

	/**
	 * Tests that at least one item of the named array element matches a query (see $elemMatch).
	 * 
	 * @param field
	 * @param query
	 * @return
	 */
	public QueryBuilder elemMatch(String field, LinkedHashMap<String, Object> query) {
		add(field, "$elemMatch", query);
		return this;
	}

	/**
	 * Tests that an element of that name does or does not exist
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder exists(String field, boolean value) {
		add(field, "$exists", value);
		return this;
	}

	/**
	 * Tests that the value of the named element is greater than some value
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder gt(String field, Object value) {
		add(field, "$gt", value);
		return this;
	}

	/**
	 * Tests that the value of the named element is greater than or equal to some value (see $gte).
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder gte(String field, Object value) {
		add(field, "$gte", value);
		return this;
	}

	public QueryBuilder eq(String field, Object value) {
		add(field, null, value);
		return this;
	}

	/**
	 * 2012-02-01
	 * 
	 * @param value
	 * @return
	 */
	public QueryBuilder id(Object value) {
		return eq("_id", value);
	}

	/**
	 * 2011-11-18 by 63
	 * 
	 * @param value
	 * @return
	 */
	public QueryBuilder objectId(Object value) {
		return eq("_id", value);
	}

	/**
	 * Tests that the value of the named element is equal to one of a list of values (see $in).
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public QueryBuilder in(String field, Object[] values) {
		add(field, "$in", values);
		return this;
	}

	/**
	 * Tests that the value of the named element is less than some value
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder lt(String field, Object value) {
		add(field, "$lt", value);
		return this;
	}

	/**
	 * Tests that the value of the named element is less than or equal to some value
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder lte(String field, Object value) {
		add(field, "$lte", value);
		return this;
	}

	/**
	 * 2011-11-16 by 63
	 * 
	 * @param value
	 * @param register
	 * @return
	 */
	public QueryBuilder where(String value, boolean register) {
		super.getResult().put("$where", value);

		if (register)
			super.getResult().put("registered", true);
		else
			super.getResult().remove("registered");

		// db.myCollection.find( { $where: "this.a > 3" } );
		// db.myCollection.find("this.a > 3");
		// f = function() { return this.a > 3; } db.myCollection.find(f);

		// db.myCollection.find({registered:true, $where:"this.a>3"})
		// Javascript executes more slowly than the native operators listed on
		// this page, but is very flexible. See the server-side processing page
		// for more information.
		return this;
	}

	/**
	 * Tests that the modulus of the value of the named element matches some value
	 * 
	 * @param field
	 * @param modulus
	 * @param equals
	 * @return
	 */
	public QueryBuilder mod(String field, int modulus, int equals) {
		add(field, "$mod", new int[] { modulus, equals });
		return this;
	}

	/**
	 * Tests that the value of the named element is not equal to some value
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder ne(String field, Object value) {
		add(field, "$ne", value);
		return this;
	}

	/**
	 * Tests that the value of the named element is not equal to any of a list of values (see $nin).
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public QueryBuilder notIn(String field, Object[] values) {
		add(field, "$nin", values);
		return this;
	}

	/**
	 * Tests that the size of the named array is equal to some value
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder size(String field, int value) {
		add(field, "$size", value);
		return this;
	}

	/**
	 * Tests that the type of the named element is equal to some type
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder type(String field, int value) {
		add(field, "$type", value);
		return this;
	}
}
