package tools.jsonrpc;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodInfo {
	private String[] parameterNames;
	private Type[] parameterTypes;
	private Method method;

	public String[] getParameterNames() {
		return parameterNames;
	}

	public void setParameterNames(String[] parameterNames) {
		this.parameterNames = parameterNames;
	}

	public Type[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Type[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
