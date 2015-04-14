package tools.test.oom;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author Yong Huang VM args: -XX:PermSize=10M -XX:MaxPermSize=10M
 */
public class JavaMethodAreaOOM {

	public JavaMethodAreaOOM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		while (true) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(OOMObject.class);
			enhancer.setUseCache(false);// 设置为true则不会oom
			enhancer.setCallback(new MethodInterceptor() {
				@Override
				public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
					// TODO Auto-generated method stub
					return arg3.invokeSuper(arg0, arg2);
				}
			});
			enhancer.create();
		}
	}

	static class OOMObject {

	}
}

//
// Exception in thread "main" java.lang.OutOfMemoryError: PermGen space
// at java.lang.Class.forName0(Native Method)
// at java.lang.Class.forName(Class.java:247)
// at net.sf.cglib.core.ReflectUtils.defineClass(ReflectUtils.java:386)
// at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:219)
// at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377)
// at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:285)
// at tools.test.oom.JavaMethodAreaOOM.main(JavaMethodAreaOOM.java:35)

