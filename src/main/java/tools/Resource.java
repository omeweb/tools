package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件要放到resource目录下面
 * 
 * @author liusan.dyf
 */
public class Resource {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassLoader c1 = Thread.currentThread().getContextClassLoader();
		ClassLoader c2 = ClassLoader.getSystemClassLoader();
		ClassLoader c3 = Resource.class.getClassLoader();

		Global.println(c1);
		Global.println(c2);
		Global.println(c3);

		// c1,c2,c3是同一个对象 2014-05-04 by liusan.dyf
	}

	/**
	 * 2011-11-17 请不要以斜杠开头，ClassLoader已经是根目录了 21:44 做了容错，如果以/开头，自动去掉 <br />
	 * 文件要放到resource目录下面 2012-07-25
	 * 
	 * @param cl
	 * @param name
	 * @return
	 */
	public static InputStream getResource(ClassLoader cl, String name) {
		if (cl == null)
			cl = Thread.currentThread().getContextClassLoader();
		if (cl == null)
			cl = ClassLoader.getSystemClassLoader();

		if (Validate.isEmpty(name))
			return null;

		char ch = name.charAt(0);
		if (ch == '/' || ch == '\\')
			name = name.substring(1);

		return cl.getResourceAsStream(name);
	}

	/**
	 * 请不要以斜杠开头，ClassLoader已经是根目录了<br />
	 * 文件要放到resource目录下面 2012-07-25
	 * 
	 * @param cl
	 * @param name
	 * @param encoding
	 * @return
	 */
	public static String getResourceAsString(ClassLoader cl, String name, String encoding) {
		InputStream in = getResource(cl, name);
		if (in == null)
			return null;
		return streamToString(in, encoding);
	}

	/**
	 * 从当前class的路径开始加载Resource，以【/】开头表示路径从根开始；也可以是相对目录，表示以当前class为基准目录
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static InputStream getResourceByClass(Class<?> clazz, String name) {
		if (clazz == null)
			return null;
		return clazz.getResourceAsStream(name);
	}

	/**
	 * 从当前class的路径开始加载Resource，以【/】开头表示路径从根开始；也可以是相对目录，表示以当前class为基准目录
	 * 
	 * @param clazz
	 * @param name
	 * @param encoding
	 * @return
	 */
	public static String getResourceAsStringByClass(Class<?> clazz, String name, String encoding) {
		InputStream in = getResourceByClass(clazz, name);
		if (in == null)
			return null;
		return streamToString(in, encoding);
	}

	/**
	 * 转换为string
	 * 
	 * @param in
	 * @param encoding
	 * @return
	 */
	public static String streamToString(InputStream in, String encoding) {
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(in, encoding));
			StringBuffer sb = new StringBuffer();
			String s = null;
			boolean first = true;
			while ((s = bf.readLine()) != null) {
				if (first)
					first = false;
				else
					sb.append("\n");

				sb.append(s);
			}

			bf.close();// 2011-11-18

			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
