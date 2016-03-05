package tools.jsonrpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import tools.Convert;
import tools.Json;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;

/**
 * 依赖：javassist和jackson类库和tools.Convert、Json <br />
 * 局限：1，LocalVariable必须存在；2，方法必须是public的；3，不解决重载jsonrpc协议本身也有该问题；4，不支持proxy代理类<br />
 * 特性：params里的参数位置可以自由调整，并可以省略或者多出一些参数，不过参数名要对<br />
 * 协议：http://en.wikipedia.org/wiki/JSON-RPC#Version_2 .0_.28Specification_Proposal.29
 * 
 * @author liusan.dyf
 */
public class JsonRpcService {

	private static final Log logger = LogFactory.getLog("system");
	/**
	 * 2012-02-28 by liusan.dyf 解决【子类重写了父类部分方法，要在子类上进行jsonrpc操作，需要子类的其他方法的参数名，则要用到父类】
	 * 当子类没有重写父类方法时，子类的class文件里不存在方法表，所以无法找到参数列表
	 */
	private static Map<String, String> mapping = new java.util.concurrent.ConcurrentHashMap<String, String>();
	private static Map<String, MethodInfo> registeredMethodInfos = new java.util.concurrent.ConcurrentHashMap<String, MethodInfo>();
	private static Object locker = new Object();

	// /**
	// * Parse error.
	// */
	// public static final String JSONRPC_PARSE_ERROR_RESPONSE =
	// "{\"jsonrpc\": \"2.0\", \"error\": {\"code\": -32700, \"message\": \"#specMessage#\"}, \"id\": #id#}";
	//
	// /**
	// * Invalid request.
	// */
	// public static final String JSONRPC_INVALID_REQUEST_RESPONSE =
	// "{\"jsonrpc\": \"2.0\", \"error\": {\"code\": -32600, \"message\": \"#specMessage#\"}, \"id\": \"#id#\"}";
	//
	// /**
	// * Method not found.
	// */
	// public static final String JSONRPC_METHOD_NOT_FOUND_RESPONSE =
	// "{\"jsonrpc\": \"2.0\", \"error\": {\"code\": -32601, \"message\": \"#specMessage#\"}, \"id\": #id#}";
	//
	// /**
	// * Invalid params.
	// */
	// public static final String JSONRPC_INVALID_PARAMS_RESPONSE =
	// "{\"jsonrpc\": \"2.0\", \"error\": {\"code\": -32602, \"message\": \"#specMessage#\"}, \"id\": #id#}";
	//
	// /**
	// * Internal error.
	// */
	// public static final String JSONRPC_INTERNAL_ERROR_RESPONSE =
	// "{\"jsonrpc\": \"2.0\", \"error\": {\"code\": -32603, \"message\": \"#specMessage#\"}, \"id\": #id#}";

	public static final int PARSE_ERROR_CODE = -32700;
	public static final int INVALID_REQUEST_ERROR_CODE = -32600;
	public static final int METHOD_NOT_FOUND_ERROR_CODE = -32601;
	public static final int INVALID_PARAMS_ERROR_CODE = -32602;
	public static final int INTERNAL_ERROR_CODE = -32603;

	private static final ObjectMapper jsonMapper;

	/**
	 * 2012-04-09 是否返回错误的堆栈信息
	 */
	private static boolean outputErrorStackTrace = true;

	static {
		jsonMapper = Json.getObjectMapper();
	}

	public static String invokeToString(Object obj, String request, boolean autoRegister) {
		InvokeResultList invokeResults = invokeBatch(obj, request, autoRegister);// 执行批量请求
		List<String> jsonStringList = new ArrayList<String>(invokeResults.getResults().size());

		try {
			List<SingleInvokeResult> list = invokeResults.getResults();

			// 循环拼接json
			for (SingleInvokeResult item : list) {
				if (item.getResult() instanceof JsonRpcError) {
					jsonStringList.add(item.getResult().toString());// 响应error
				} else {// 正确的响应
					Map<String, Object> m = toJsonRpcResultMap(item.getId(), item.getResult());
					jsonStringList.add(jsonMapper.writeValueAsString(m));
				}
			}

			// 输出json
			if (!invokeResults.isBatch())
				return jsonStringList.get(0);
			else {
				int left = jsonStringList.size();
				StringBuilder builder = new StringBuilder();

				builder.append("[");
				for (String s : jsonStringList) {
					left--;
					builder.append(s);
					if (left > 0)
						builder.append(",");
				}

				builder.append("]");
				return builder.toString();
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	/**
	 * 结果有包装，因为正确响应或者是错误响应，都要得到id的值。<br />
	 * 错误，返回result为JsonRpcError的SingleInvokeResult对象； 正确返回result为正常返回结果的SingleInvokeResult
	 * 
	 * @param obj
	 * @param request
	 * @param autoRegister
	 * @return
	 */
	public static InvokeResultList invokeBatch(Object obj, String request, boolean autoRegister) {
		List<SingleInvokeResult> results = null;
		List<JsonNode> requestContext = null;
		boolean isBatch = false;
		JsonNode node = null;

		// 解析请求的对象，看是否出错
		try {
			node = jsonMapper.readValue(request, JsonNode.class);
		} catch (Exception e) {
			JsonRpcError error = new JsonRpcError(0, PARSE_ERROR_CODE, getErrorMessage(e));

			// 包装 2011-10-27
			SingleInvokeResult wrapper = new SingleInvokeResult();
			wrapper.setId(0);
			wrapper.setResult(error);

			// 再包装
			InvokeResultList invokeResults = new InvokeResultList();
			invokeResults.getResults().add(wrapper);
			invokeResults.setBatch(false);
			return invokeResults;
		}

		// 看是否是批量调用
		if (node.isArray()) {// 批量调用
			isBatch = true;
			results = new ArrayList<SingleInvokeResult>(node.size());
			requestContext = new ArrayList<JsonNode>(node.size());

			for (JsonNode item : node) {
				requestContext.add(item);
			}
		} else {
			results = new ArrayList<SingleInvokeResult>(1);
			requestContext = new ArrayList<JsonNode>(1);
			requestContext.add(node);
		}

		// 依次请求调用
		int id = 0;

		for (JsonNode n : requestContext) {
			Object temp = null;

			try {
				JsonNode idNode = n.get("id");// 包在try里，这里也可以出异常
				if (idNode == null)
					throw new InvalidJsonRpcRequestException();
				else
					id = Integer.valueOf(idNode.asText());

				temp = invoke(obj, n, autoRegister);
			} catch (JsonParseException e) {
				logger.error(e);
				temp = new JsonRpcError(id, PARSE_ERROR_CODE, getErrorMessage(e));
			} catch (JsonMappingException e) {
				logger.error(e);
				temp = new JsonRpcError(id, INVALID_PARAMS_ERROR_CODE, getErrorMessage(e));
			} catch (IllegalArgumentException e) {
				logger.error(e);
				temp = new JsonRpcError(id, INVALID_PARAMS_ERROR_CODE, getErrorMessage(e));
			} // catch (IllegalAccessException e) {
				// e.printStackTrace();
				// }
				// 在反射机制中，如果当前执行的方法所调用的方法抛出异常，会被包装成这个异常 2011-11-09
			catch (InvocationTargetException e) { // 方法里抛出IllegalArgumentException，经过反射后，变成InvocationTargetException
				logger.error(e);
				temp = new JsonRpcError(id, INVALID_PARAMS_ERROR_CODE, getErrorMessage(e.getTargetException()));
				// 这里只取message，异常类型名java.lang.Exception就不需要了
				// 2011-11-05 为了方便调试，显示所有消息
			} catch (MethodNotFoundException e) {
				logger.error(e);
				temp = new JsonRpcError(id, METHOD_NOT_FOUND_ERROR_CODE, getErrorMessage(e));
			} catch (IOException e) {
				logger.error(e);
				temp = new JsonRpcError(id, INTERNAL_ERROR_CODE, getErrorMessage(e));
			} catch (InvalidJsonRpcRequestException e) {
				logger.error(e);
				temp = new JsonRpcError(id, INVALID_REQUEST_ERROR_CODE, "id missing");
			} catch (Exception e) {
				// e.printStackTrace();
				logger.error(e);
				temp = new JsonRpcError(id, INTERNAL_ERROR_CODE, getErrorMessage(e));// 异常可以为空
			}

			// 包装
			SingleInvokeResult wrapper = new SingleInvokeResult();
			wrapper.setId(id);
			wrapper.setResult(temp);
			results.add(wrapper);
		}

		// 返回结果
		InvokeResultList invokeResults = new InvokeResultList();
		invokeResults.setResults(results);
		invokeResults.setBatch(isBatch);
		return invokeResults;
	}

	/***
	 * 直接返回方法执行的结果，一般来说，该方法不应该暴露出去的，这里为了单元测试
	 * 
	 * @param obj
	 * @param singleRequestJson
	 * @param autoRegister
	 * @return
	 */
	public static Object invoke(Object obj, String singleRequestJson, boolean autoRegister) {
		try {
			JsonNode node = jsonMapper.readValue(singleRequestJson, JsonNode.class);
			if (node.isArray())
				throw new Exception("invoke方法只能处理请求单个jsonrpc请求");
			return invoke(obj, node, autoRegister);
		} catch (Exception e) {
			logger.error(e);
			// 这里有可能抛出异常，先不予处理 2011-11-02
		}

		return null;
	}

	/**
	 * 2013-12-03 by liusan.dyf
	 * 
	 * @param obj
	 * @param methodName
	 * @param paramsJson
	 * @param autoRegister
	 * @return
	 * @throws Exception
	 */
	public static Object invoke(Object obj, String methodName, String paramsJson, boolean autoRegister)
		throws Exception {
		JsonNode params = null;
		if (!tools.Validate.isNullOrEmpty(paramsJson))// 2014-01-09 by liusan.dyf
			params = jsonMapper.readValue(paramsJson, JsonNode.class);

		return invokeInternal(obj, methodName, params, autoRegister);
	}

	/**
	 * 限制：该方法的JsonNode不能是jsonrpc请求对象的数组 ，所以该方法为private<br />
	 * obj参数不能为null 2011-11-04，因为取缓存的key需要obj类型 <br />
	 * 
	 * @param obj
	 * @param jsonRpcRequestNode
	 * @param autoRegister
	 * @return
	 */
	static Object invoke(Object obj, JsonNode jsonRpcRequestNode, boolean autoRegister) throws Exception {
		// 100000 times = 375
		// return null;

		// parse request
		// String jsonRpc = jsonRpcRequestNode.get("jsonrpc").asText();
		String methodName = jsonRpcRequestNode.get("method").asText();
		// String id = jsonRpcRequestNode.get("id").asText();
		JsonNode params = jsonRpcRequestNode.get("params");

		return invokeInternal(obj, methodName, params, autoRegister);
	}

	static Object invokeInternal(Object obj, String methodName, JsonNode params, boolean autoRegister)
		throws Exception {
		// 100000 times = 375
		// return null;

		// parse request
		int paramsLenInRequest = (params != null) ? params.size() : 0;

		// key
		String key = null;
		boolean isStaticClass = false;// 静态类的处理方式不一样，由胡四海发现 2013-07-31 by liusan.dyf
		if (obj instanceof Class) {// 2011-11-04 如果obj自己是class
			key = getKeyEx((Class<?>) obj, methodName);
			isStaticClass = true;
		} else if (obj != null) {// 2011-11-04 检查obj是否为null
			key = getKeyEx(obj.getClass(), methodName);
		} else
			key = methodName;

		// System.out.println(key);// eg tools.test.jsonrpc.Obj.add

		// 找到方法
		if (!registeredMethodInfos.containsKey(key)) {
			if (autoRegister) {
				// obj.getClass()当obj为null时，可能有异常 2011-11-10
				if (isStaticClass) { // 2013-07-31 by liusan.dyf
					registerMethod((Class<?>) obj, methodName);
				} else {
					registerMethod(obj.getClass(), methodName);
				}
			} else {
				throw new MethodNotFoundException(obj.getClass().getName() + "." + methodName);// 找不到方法
			}
		}

		// 就算注册方法，也未必可以注册成功，有的方法找不到，比如名称错误 2011-10-26
		if (!registeredMethodInfos.containsKey(key))
			throw new MethodNotFoundException(methodName);
		// 100000 times = 422

		// 解析参数
		MethodInfo mi = registeredMethodInfos.get(key);

		// params调用时可以赋为null，这里不影响 2012-07-17
		if (params != null && params.isTextual() && "meta.methodinfo".equalsIgnoreCase(params.asText())) {
			return mi;// 2012-07-17做个拦截，得到方法信息
		}

		// System.out.println(jsonMapper.writeValueAsString(mi));
		// 可以被序列化 2012-07-17
		int paramsLen = mi.getParameterNames().length;

		// 如果没有参数，则直接为null
		Object[] methodParams = null;// 这里不使用请求发过来的参数的列表，因为请求里有些参数可以省略
		if (paramsLen > 0)
			methodParams = new Object[paramsLen];// 有参数 2013-08-27 by liusan.dyf

		// TypeFactory.rawClass是关键点 2011-10-27 by 63
		// readValue 反序列化为对象时，如果出行多余的属性，则报错 2011-11-04

		if (paramsLen > 0 && paramsLenInRequest > 0) {// 请求里也有参数
			if (params.isObject()) {
				// if (paramsLen > 1) {
				Type[] parameterTypes = mi.getParameterTypes();
				String[] parameterNames = mi.getParameterNames();
				for (int i = 0; i < paramsLen; i++) {
					JsonNode paramJsonNode = params.get(parameterNames[i]);// 按照参数名来加载

					if (paramJsonNode != null) { // 2011-11-07，如果null则报错
						methodParams[i] = jsonMapper.readValue(paramJsonNode.traverse(),
								TypeFactory.rawClass(parameterTypes[i]));

						// 2012-04-12 readValue会做基本的类型转换，直到转换出错，比如String=>int，int=>String
					}
				}
				// } else { // 参数个数如果为1，直接转换，有悖于jsonrpc协议 2011-11-04
				// methodParams[0] = jsonMapper.readValue(params,
				// TypeFactory.rawClass(mi.getParameterTypes()[0]));
				// }

				// 2011-11-04 取消这种做法，不安全，如果是正常的请求，这处理失败
				// incr(i); {i:5} is right,a single 5 is wrong
				// 2011-11-11 这样做的好处是，原本方法是1个参数，现在要增加参数，client几乎不用做调整

			} else if (params.isArray()) {// 数组比较特殊 2011-10-25 22:10
				methodParams = new Object[1];
				// 解决int... args和int[] args这样的情况，这些都只有一个参数
				methodParams[0] = jsonMapper.readValue(params.traverse(),
						TypeFactory.rawClass(mi.getParameterTypes()[0]));
			}
		}

		// 如果没有参数，直接调用，参数为0 2013-08-27 by liusan.dyf
		return mi.getMethod().invoke(obj, methodParams);// 100000 times = 610
	}

	/**
	 * 单纯的得到key，不解决映射问题
	 * 
	 * @param clazz
	 * @param method
	 * @return
	 */
	private static String getKey(Class<?> clazz, String method) {
		// 2011-10-27 如果方法是完整的方法名，这直接返回 by 63
		char ch = '.';
		if (method.indexOf(ch) > -1)
			return method;
		return clazz.getName() + "." + method;
	}

	/**
	 * 2012-02-28 为了解决key的映射问题，比如把子类的xx方法的参数信息映射到父类的xx方法参数算
	 * 
	 * @param clazz
	 * @param method
	 * @return
	 */
	private static String getKeyEx(Class<?> clazz, String method) {
		String key = getKey(clazz, method);

		if (mapping.containsKey(key))
			return mapping.get(key);

		return key;
	}

	/**
	 * 自动注册时调用
	 * 
	 * @param clazz
	 * @param method
	 */
	public static void registerMethod(Class<?> clazz, String method) {
		if (Proxy.isProxyClass(clazz)) {
			// TODO 处理代理类的情况 2013-09-18 by liusan.dyf
		}
		// System.out.println(clazz);
		Method[] methods = clazz.getMethods();
		// getMethods会获取所有的方法，来着接口、父类的
		// 而getDeclaredMethods只获取类里声明的、有方法体的方法

		for (Method m : methods) { // 因为无法确定参数信息，所以只能遍历 2012-04-10
			// System.out.println(m.getName());
			if (method.equals(m.getName())// 区分大小写 2011-11-10
					&& !m.isBridge() // 来自【康泽】 2012-09-20 by liusan.dyf
			) {
				registerMethodInternal(clazz, m);
				return;// 不解决重载问题
			}
		}

		// 2012-09-20 by liusan.dyf
		// isBridge，桥方法，知识普及：http://jiangshuiy.iteye.com/blog/1339105
		// 即在本类里自动生成了一个super类里的泛型方法，不论什么泛型，一律被替换为object
		// 并在桥方法内部调用真正实现了的方法，所以称为桥
	}

	public static void registerAllMethods(Class<?> clazz) {
		Method[] methods = clazz.getMethods();// 不是getDeclaredMethods，要拉取所有的方法
		for (Method item : methods) {
			if (!item.isBridge()) // 2012-09-20
				registerMethodInternal(clazz, item);
		}
	}

	/**
	 * 一般是子类映射到父类上 2012-02-28。getMethods方法可以获取到父类的方法，这些方法也可以被反射调用到 2012-04-09
	 * 
	 * @param from
	 * @param to
	 */
	@Deprecated
	public static void mapTo(Class<?> from, Class<?> to) {
		// 解决【会隐藏子类自己新增加的方法，主要是子类没有覆盖父类的方法，此时子类的方法表是不存在那些方法的】 2012-02-28 10:13
		// 2012-04-09被废弃，使用了向上转型查找方法表
		Method[] methods = to.getMethods();
		for (Method item : methods) {
			if (item.isBridge()) // 2012-09-20
				continue;

			String fromKey = getKey(from, item.getName());
			String toKey = getKey(to, item.getName());

			mapping.put(fromKey, toKey);
		}
	}

	/**
	 * 2012-04-09 同一个类的方法，设置一个别名
	 * 
	 * @param cls
	 * @param fromMethod
	 * @param toMethod
	 */
	@Deprecated
	public static void mapTo(Class<?> cls, String fromMethod, String toMethod) {
		String fromKey = getKey(cls, fromMethod);
		String toKey = getKey(cls, toMethod);
		mapping.put(fromKey, toKey);
	}

	// /**
	// * 循环向上转型, 获取对象的DeclaredField.
	// *
	// * 如向上转型到Object仍无法找到, 返回null.
	// */
	// protected static Field getField(final Object object, final String
	// fieldName) {
	// for (Class<?> cls = object.getClass(); cls != Object.class; cls = cls
	// .getSuperclass()) {
	// try {
	// return cls.getDeclaredField(fieldName);
	// } catch (NoSuchFieldException e) {
	// // Field不在当前类定义,继续向上转型
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * 强行设置Field可访问.
	// */
	// protected static void makeAccessible(final Field field) {
	// if (!Modifier.isPublic(field.getModifiers())
	// || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
	// field.setAccessible(true);
	// }
	// }
	//
	/**
	 * 循环向上转型, 获取对象的DeclaredMethod.如向上转型到Object仍无法找到, 返回null.
	 */
	@Deprecated
	protected static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
		// from http://code.btboys.com/index/view/id/29.html

		if (clazz == null)
			return null;

		for (Class<?> cls = clazz; cls != Object.class; cls = cls.getSuperclass()) {
			try {
				return cls.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	static MethodInfo registerMethodInternal(Class<?> clazz, Method method) {// clazz不能为Proxy
		String methodName = method.getName();
		String key = getKeyEx(clazz, methodName);

		if (!registeredMethodInfos.containsKey(key)) {
			synchronized (locker) {
				if (!registeredMethodInfos.containsKey(key)) {
					try {
						// 参数类型列表
						Type[] parameterTypes = method.getGenericParameterTypes();

						// 参数名称列表
						List<String> parameterNames = getMethodParamNames(method);

						// 转换为数组 2011-11-10
						String[] arrNames = null;
						if (parameterNames != null) { // 20:12
							arrNames = new String[parameterNames.size()];

							int i = 0;
							for (String item : parameterNames)
								arrNames[i++] = item;
						}

						// 构造实体
						MethodInfo mi = new MethodInfo();
						method.setAccessible(true);
						mi.setMethod(method);
						mi.setParameterNames(arrNames);
						mi.setParameterTypes(parameterTypes);

						// 加入到缓存里
						registeredMethodInfos.put(key, mi);

						if (logger.isDebugEnabled())
							logger.debug("registered key:" + key + ",parameterNames: " + Convert.join(parameterNames));

					} catch (NotFoundException e) {
						logger.error(e);
					} catch (LocalVariableNotFoundException e) {
						logger.error(e);
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}
		}

		// 返回
		return registeredMethodInfos.get(key);
	}

	// 一些工具方法开始............................................

	/**
	 * 获取方法参数名称，匹配同名的某一个方法
	 * 
	 * @param method
	 * @return
	 * @throws Exception
	 */
	private static List<String> getMethodParamNames(Method method) throws Exception {

		List<String> list = getMethodParamNamesInternal(method);

		// String methodName = method.getName();
		// Class<?> clazz = method.getDeclaringClass();
		//
		// // 2012-04-10
		// // method.getDeclaringClass();找到方法被申明的那个类，并不一定是当前类，有可能是他的父类

		// // 没有找到方法表之类的信息，往上继续查找 2012-04-09
		// if (list == null) {
		// Method newMethod = getMethod(clazz.getSuperclass(), methodName,
		// method.getParameterTypes());
		//
		// if (newMethod != null) {
		// list = getMethodParamNamesInternal(newMethod);
		//
		// if (LOGGER.isDebugEnabled()) {
		// if (list != null)
		// LOGGER.debug(clazz + "." + methodName + "向上转型查找方法表成功");
		// }
		// }
		// }

		return list;
	}

	private static List<String> getMethodParamNamesInternal(Method method) throws Exception {
		String methodName = method.getName();

		// 判断申明的类 2012-04-10
		Class<?> clazz = method.getDeclaringClass();
		if (clazz == Object.class) {
			logger.debug("getMethodParamNamesInternal:当前class无法获取参数列表：" + clazz + "." + methodName);
			return new ArrayList<String>(0);
		}

		logger.debug("getMethodParamNamesInternal:" + clazz + "." + methodName);

		List<String> paramNames = new ArrayList<String>();

		// 加入ClassClassPath 2011-11-10
		ClassPool pool = ClassPool.getDefault();
		CtClass c = null;
		try {
			c = pool.get(clazz.getName());
		} catch (NotFoundException e) {
			// LOGGER.error(e);

			// 该情况在tomcat下出现 2012-09-20，相关资料：
			// http://www.iteye.com/problems/61346
			// http://www.cnblogs.com/anncsr/archive/2011/03/28/1997799.html

			pool.insertClassPath(new ClassClassPath(clazz));// 2011-11-04 fixed
			c = pool.get(clazz.getName());
		}

		// c.getSuperclass()

		// 如果没有重写从父类继承的方法，则无法得到他们的参数，因为没有方法表 2011-11-10
		// wait,equals,toString,hashCode,getClass,notify,notifyAll

		// 得到方法信息
		CtMethod m = c.getDeclaredMethod(methodName);

		// fix 没有参数 2013-08-28 by liusan.dyf
		if (m.getParameterTypes() == null || m.getParameterTypes().length == 0)
			return paramNames;

		boolean found = (m != null);
		if (found) {
			// 方法表信息
			CodeAttribute code = (CodeAttribute) m.getMethodInfo().getAttribute("Code");
			LocalVariableAttribute lval = (LocalVariableAttribute) code.getAttribute("LocalVariableTable");

			if (lval == null) {// 2013-03-07 by liusan.dyf，如果没有lval，说明方法没有参数
				return paramNames;
			}
			// for (int i = 0; i < m.getParameterTypes().length
			// + (Modifier.isStatic(m.getModifiers()) ? 0 : 1); i++) {
			// if (lval == null)// 没有局部变量表信息
			// continue;
			//
			// String name = lval.getConstPool().getUtf8Info(lval.nameIndex(i));
			//
			// if (!name.equals("this")) {
			// paramNames.add(name);
			// }
			// }

			int len = lval.tableLength();

			List<Parameter> plist = new ArrayList<Parameter>(len);

			// 2012-03-12 有的class文件方法表的startPos并不是从0开始的，找出最小的index然后开始循环
			// startPos最小，说明是先定义的变量 2013-08-28 by liusan.dyf
			// 重要：如果某方法没有参数，那么方法体里的变量，要过滤掉，防止被判断为方法的参数 2013-08-28 by liusan.dyf
			int min = Integer.MAX_VALUE;
			for (int i = 0; i < len; i++) {
				int p = lval.startPc(i);
				if (p < min)
					min = p;
				if (min == 0)// 0已经是最小的了
					break;
			}

			logger.debug("min=" + min);

			for (int i = 0; i < len; i++) {
				String name = lval.variableName(i);
				// 要判断startPc，因为LocalVariableTable的顺序可能是被打乱了 2011-11-10
				int startPos = lval.startPc(i);

				// 2012-03-12 by liusan.dyf
				logger.debug("name=" + name + ",startPos=" + startPos + ",index=" + lval.index(i));

				// 2012-03-12 startPos还真是不一定是从0开始的，从0开始的，可能是方法的参数。不过方法的参数也可能从大于0的位置开始的
				if (startPos == min && !name.equals("this")) {

					// paramNames.add(lval.variableName(i));

					// 最好是按照nameIndex来排序，因为这个是参数定义的顺序

					// 18:40新版本
					Parameter p = new Parameter();
					p.setName(name);
					p.setIndex(lval.index(i));// 2011-11-14 index -> class文件里的slot
					plist.add(p);
				}
			} // end for

			// ------------输出示例信息如下：
			// min=9
			// name=item,startPos=49,index=3
			// name=i$,startPos=30,index=2
			// name=allList,startPos=9,index=0
			// name=rtn,startPos=23,index=1

			// System.out.println(new String(lval.get(),"ascii"));

			// 排序，按照slot
			Collections.sort(plist, new Comparator<Parameter>() {
				@Override
				public int compare(Parameter o1, Parameter o2) {
					return o1.getIndex() - o2.getIndex();
				}
			});// end sort

			// 转换为list
			for (Parameter item : plist) {
				paramNames.add(item.getName());
				// System.out.println(item.getIndex());
			} // end for

		} // end if (found)

		logger.debug("在" + clazz + "里找寻方法" + methodName + "：" + found);

		if (!found) {// 说明没有找到这个方法的方法表信息 2012-04-09
			// TODO 怕引起死循环
		}

		return paramNames;
	}

	// 一些工具方法开始............................................

	/**
	 * 把一个结果包装为jsonrpc结果 2011-11-23
	 * 
	 * @param id
	 * @param value
	 * @return
	 */
	public static String toJsonRpcResult(int id, Object value) {
		try {
			return jsonMapper.writeValueAsString(toJsonRpcResultMap(id, value));
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	/**
	 * 2012-04-06 by liusan.dyf
	 * 
	 * @param id
	 * @param code
	 * @param message
	 * @return
	 */
	public static String toJsonRpcErrorResult(int id, int code, String message) {
		try {
			// {"jsonrpc": "2.0", "error": {"code": -32603, "message":
			// "specMessage"}, "id": 1}
			Map<String, Object> error = new HashMap<String, Object>(2);
			error.put("code", code);
			error.put("message", message);

			// map
			Map<String, Object> map = new HashMap<String, Object>(3);
			map.put("id", id);
			map.put("error", error);
			map.put("jsonrpc", "2.0");
			return jsonMapper.writeValueAsString(map);
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	private static Map<String, Object> toJsonRpcResultMap(int id, Object value) {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("id", id);
		map.put("result", value);
		map.put("jsonrpc", "2.0");
		return map;
	}

	/**
	 * 2011-11-05 by 63，为了显示跟详细的异常信息
	 * 
	 * @param template
	 * @return
	 */
	private static String getErrorMessage(Throwable e) {
		String message = null;

		if (outputErrorStackTrace) {// 2012-04-09 增加该判断
			// 2012-03-31 打印异常堆栈信息
			// from org.apache.commons.logging.impl.SimpleLog
			java.io.StringWriter sw = new java.io.StringWriter(1024);
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			pw.close();

			message = sw.toString(); // stack trace as a string
			// StringWriter内部是个StringBuffer，可以不用close
		} else
			message = e.getMessage();

		return message;
	}

	/**
	 * 2012-04-09 by liusan.dyf 是否返回错误的堆栈信息，默认true
	 * 
	 * @param flag
	 */
	public static void outputErrorStackTrace(boolean flag) {
		outputErrorStackTrace = flag;
	}

	// 一些工具方法结束............................................
}

class Parameter {
	private int index;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
