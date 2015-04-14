package tools;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonParser;

/**
 * 2012-02-01<br />
 * 升级指南 http://www.cowtowncoder.com/blog/archives/2012/03/entry_466.html
 * 
 * @author liusan.dyf
 */
public class Json {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 2011-11-07

		// 2011-11-07 15:39
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);

		// 2012-02-12 允许单引号的字段
		// from
		// http://stackoverflow.com/questions/4815231/allow-unquoted-field-names-in-jackon-json-library
		// http://hi.baidu.com/lzpsky/blog/item/c4d9d8c2a92c9b23e5dd3b8a.html

		// 2013-01-18 新版 http://stackoverflow.com/questions/11531298/jackson-objectmapping-not-getting-json-data
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

		// 2012-11-27 by liusan.dyf
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

		// 2012-11-27 by liusan.dyf
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		// // 2012-12-13 by liusan.dyf
		// objectMapper.configure(SerializationConfig.Feature.REQUIRE_SETTERS_FOR_GETTERS, true);

		// // setDateFormat，免得默认把date对象序列化为timestamp
		// objectMapper.setDateFormat(new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		// df.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (Exception ex) {
			// System.out.println(value);// 2014-06-12 by liusan.dyf
			ex.printStackTrace();
		}

		return null;
	}

	/**
	 * 2012-08-10 by liusan.dyf，生成格式化好的json代码
	 * 
	 * @param value
	 * @return
	 */
	public static String toPrettyJson(Object value) {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
		} catch (Exception ex) {
			System.out.println(value);// 2014-06-12 by liusan.dyf
			ex.printStackTrace();
		}

		return null;
	}

	public static String toString(Object value) {
		return toJson(value);
	}

	public static <T> T toObject(String value, Class<T> targetType) {
		if (StringUtil.isNullOrEmpty(value))
			return null;
		try {
			return objectMapper.readValue(value, targetType);
		} catch (Exception ex) {
			System.out.println(value);// 2014-06-12 by liusan.dyf
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 2012-07-25 eg. TypeReference ref = new TypeReference<List<Integer>>() { }; <br />
	 * 注意，如果对象里有接口、抽象类等类型的成员，则反序列化会失败，无法创建实例，注意使用方式【...(){}】
	 * 
	 * @param value
	 * @param targetType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toObject(String value, TypeReference<T> targetType) {
		if (StringUtil.isNullOrEmpty(value))
			return null;
		try {
			return (T) objectMapper.readValue(value, targetType);
		} catch (Exception ex) {
			System.out.println(value);// 2014-06-12 by liusan.dyf
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 2012-07-25
	 * 
	 * @param value
	 * @param targetType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toObject(String value, JavaType targetType) {
		if (StringUtil.isNullOrEmpty(value))
			return null;
		try {
			return (T) objectMapper.readValue(value, targetType);
		} catch (Exception ex) {
			System.out.println(value);// 2014-06-12 by liusan.dyf
			ex.printStackTrace();
		}

		return null;
	}

	/**
	 * 把一个对象转化为扁平结构的map 2012-02-02 <br />
	 * 会把Date()转换会为long或者是string，取决于日期的序列化配置<br />
	 * 2014-03-12，判断如果是string，则转换为普通的map
	 * 
	 * @param value
	 * @return
	 */
	public static Map<?, ?> toMap(Object value) {
		if (value instanceof String) {// 2014-03-12 by liusan.dyf
			return toObject((String) value, Map.class);
		}
		return objectMapper.convertValue(value, HashMap.class);
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public static void main(String[] args) {
		System.out.println(toMap(null));

		String json = "[1,2,3,4,'的'\n]";// "[1,2,3,4,\"的\"\n]";
		Object o = toObject(json, Object.class);

		@SuppressWarnings("unchecked")
		java.util.List<Integer> arr = (java.util.List<Integer>) o;
		// int[] arr = (int[]) o; 转换失败java.util.ArrayList cannot be cast to [I
		System.out.println(arr.size());// 5
		System.out.println(arr.get(4));// 的，很奇怪

		System.out.println(o instanceof java.util.List);// true
		System.out.println(o.getClass());// class java.util.ArrayList

		System.out.println(null + "");// null

		//
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", arr);
		map.put("longValue", 3054339262020730765L);
		System.out.println(toJson(map));// 格式比较好 2012-08-10

		// 得到当前执行的代码是哪一行，方法名是啥 2015-3-11 12:17:07 by 六三
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		System.out.println(st[1]); // 注意是1
	}
}
