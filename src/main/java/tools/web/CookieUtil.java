package tools.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tools.StringUtil;

/**
 * 可参见 http://www.tutorialspoint.com/servlets/servlets-cookies-handling.htm<br />
 * 要实例化对象才能使用
 * 
 * @author liusan.dyf
 */
public class CookieUtil {
	private static final Log logger = LogFactory.getLog(CookieUtil.class);

	private HttpServletRequest request;
	private HttpServletResponse response;

	private final static String DEFAULT_PATH = "/";
	private final static String DEFAULT_DOMAIN = ".";

	/**
	 * @param request HttpServletRequest才有getCookies()的方法
	 * @param response
	 */
	public CookieUtil(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public void set(String name, String value, String domain, String path, int seconds) {
		logger.debug("name=" + name + "，value=" + value + "，seconds=" + seconds);
		Cookie c = new Cookie(name, value);

		if (!StringUtil.isNullOrEmpty(domain))
			c.setDomain(domain);
		else
			c.setDomain(DEFAULT_DOMAIN);

		if (!StringUtil.isNullOrEmpty(path))
			c.setPath(path);
		else
			c.setPath(DEFAULT_PATH);

		// cookies.setMaxAge(-1);//设置cookie经过多长秒后被删除。如果0，就说明立即删除。如果是负数就表明当浏览器关闭时自动删除。
		c.setMaxAge(seconds);

		response.addCookie(c);
	}

	public String getValue(String name) {
		if (name != null) {
			Cookie c = get(name);
			if (c != null) {
				return c.getValue();
			}
		}
		return StringUtil.EMPTY;
	}

	/**
	 * 2012-09-28 by liusan.dyf
	 * 
	 * @return
	 */
	public String getRequestCookieString() {
		Cookie[] coll = request.getCookies();
		if (coll == null || coll.length == 0)
			return "";

		StringBuilder sb = new StringBuilder();

		for (Cookie item : coll) {
			sb.append(item.getName() + "=" + item.getValue() + "; ");
		}

		return sb.toString();
	}

	public Cookie get(String n) {
		Cookie[] coll = request.getCookies();

		if (coll == null || coll.length == 0)
			return null;

		for (Cookie item : coll) {
			if (item.getName().equals(n)) {
				return item;
			}
		}
		return null;
	}

	public boolean delete(String n, String domain, String path) {
		if (!StringUtil.isNullOrEmpty(n)) {
			Cookie c = get(n);
			if (c != null) {
				c.setMaxAge(0);// 如果0，就说明立即删除

				if (!StringUtil.isNullOrEmpty(domain))
					c.setDomain(domain);
				else
					c.setDomain(DEFAULT_DOMAIN);

				if (!StringUtil.isNullOrEmpty(path))
					c.setPath(path);// 不要漏掉
				else
					c.setPath(DEFAULT_PATH);

				response.addCookie(c);
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// CookieUtil util = new CookieUtil(request,response,-1);
		// util.addCookie("name","value");
		// String value = util.getCookieValue("name");
		// System.out.println("value="+value);
	}
}
