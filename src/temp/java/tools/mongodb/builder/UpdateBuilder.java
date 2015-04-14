package tools.mongodb.builder;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;

/**
 * http://www.mongodb.org/display/DOCS/Updating
 * 
 * @author liusan.dyf
 */
public class UpdateBuilder extends Builder {
	public UpdateBuilder inc(String field, double value) {
		add(field, "$inc", value);
		return this;
	}

	public UpdateBuilder set(String field, Object value) {
		add(field, "$set", value);
		return this;
	}

	/**
	 * 删除一个字段
	 * 
	 * @param field
	 * @return
	 */
	public UpdateBuilder unset(String field) {
		add(field, "$unset", 1);
		return this;
	}

	/**
	 * appends value to field, if field is an existing array, otherwise sets field to the array [value] if field is not
	 * present. If field is present but is not an array, an error condition is raised.
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public UpdateBuilder push(String field, Object value) {
		add(field, "$push", value);
		return this;
	}

	public UpdateBuilder pushAll(String field, Object[] values) {
		add(field, "$pushAll", values);
		return this;
	}

	/**
	 * set，过滤掉重复的
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public UpdateBuilder addToSet(String field, Object value) {
		add(field, "$addToSet", value);
		return this;
	}

	public UpdateBuilder addToSet(String field, Object[] values) {
		add(field, "$addToSet", new BasicDBObject("$each", values));
		// { $addToSet : { a : { $each : [ 3 , 5 , 6 ] } } }
		return this;
	}

	/**
	 * removes the last element in an array
	 * 
	 * @param field
	 * @return
	 */
	public UpdateBuilder popLast(String field) {
		pop(field, 1);
		return this;
	}

	public UpdateBuilder popFirst(String field) {
		pop(field, -1);
		return this;
	}

	private UpdateBuilder pop(String field, int value) {
		add(field, "$pop", value);
		return this;
	}

	public UpdateBuilder pull(QueryBuilder value) {
		add(null, "$pull", value.getResult());
		return this;

		// 该方法参考了
		// https://github.com/mongodb/mongo-csharp-driver/blob/master/Driver/Builders/UpdateBuilder.cs

		// removes all occurrences of value from field, if field is an array. If
		// field is present but is not an array, an error condition is raised

		// { $pull : { field : {field2: value} } } removes array elements with
		// field2 matching value

		// { $pull : { field : {$gt: 3} } } removes array elements greater than
		// 3

		// { $pull : { field : {<match-criteria>} } } removes array elements
		// meeting match criteria
	}

	public UpdateBuilder pullAll(String field, Object[] values) {
		add(field, "$pullAll", values);
		return this;
	}

	public UpdateBuilder rename(String oldFieldName, String newFieldName) {
		add(oldFieldName, "$rename", newFieldName);
		return this;
	}

	public UpdateBuilder bitAnd(String field, int value) {
		add(field, "$bit", new KeyValue("and", value));
		return this;
	}

	public UpdateBuilder bitOr(String field, int value) {
		add(field, "$bit", new KeyValue("or", value));
		return this;

		// {$bit : {field : {and : 5}}}
		// {$bit : {field : {or : 43}}}
		// {$bit : {field : {and : 5, or : 2}}}
	}

	private void add(String field, String op, Object value) {
		BasicDBObject operates = super.getResult();

		// eg. { "$pushAll" : { "a" : [ 4 , 5 , 1 , 2 , 3]} , "$set" : { "b" :
		// 10.0} , "$unset" : { "b" : 1}}
		// 注意，key是操作符，value是一组要修改的字段与修改信息的map
		String key = op;
		Object storedValue = operates.get(key);

		// 注：key的对象为A，A的key为field，A的value是什么，一般是基本类型，
		// 不过这里的Boolean类型，都是说 A的value的类型

		boolean needArrayValue = op.equals("$pushAll"); // value :[]

		// eg. {$bit : {field : {and : 5, or : 2}}}
		boolean isBitOperate = op.equals("$bit");

		boolean isPullOperate = op.equals("$pull"); // 2011-11-15

		BasicDBObject tempValue;

		if (storedValue != null) {

			// 准备对象A
			tempValue = (BasicDBObject) storedValue;

			if (needArrayValue) {

				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) tempValue.get(field);// 这里是field

				Object[] v = (Object[]) value;
				for (Object item : v) { // 不处理重复值问题
					list.add(item);
				}
			} else if (isBitOperate) {
				BasicDBObject fValue = (BasicDBObject) tempValue.get(field);
				System.out.println("tempValue=" + tempValue.toString() + ",field=" + field);

				KeyValue t = (KeyValue) value;

				if (fValue == null) { // 这个字段首次添加bit操作符
					operates.put(field, new BasicDBObject(t.getKey().toString(), t.getValue()));
				} else { // 已经不是第一次给该字段添加bit操作符了 2011-11-14 22:47
					fValue.put(t.getKey().toString(), t.getValue());
				}
			} else if (isPullOperate) { // 2011-11-15
				BasicDBObject v = (BasicDBObject) value;
				tempValue.putAll(v.toMap());
			} else {// 值覆盖
				tempValue.put(field, value);
			}
		} else {
			if (needArrayValue) { // 值是[]的情况
				List<Object> list = new ArrayList<Object>();
				Object[] v = (Object[]) value;
				for (Object item : v) {
					list.add(item);
				}
				operates.put(key, new BasicDBObject(field, list));// 值不同
			} else if (isBitOperate) {
				KeyValue t = (KeyValue) value;
				BasicDBObject fValue = new BasicDBObject(t.getKey().toString(), t.getValue());
				operates.put(key, new BasicDBObject(field, fValue));
			} else if (isPullOperate) { // 2011-11-15
				operates.put(key, value);
			} else {
				operates.put(key, new BasicDBObject(field, value));
			}
		}
	}
}

class KeyValue {
	private Object key;
	private Object value;

	public KeyValue(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
