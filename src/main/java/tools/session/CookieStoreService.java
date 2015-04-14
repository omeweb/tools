package tools.session;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.StoreService;
import tools.web.CookieUtil;

public class CookieStoreService implements StoreService<String> {
	private static final String ENCODING = "UTF-8";// cookie的编码 2012-10-08
	private tools.web.CookieUtil cookie = null;
	private String name;
	private String domain;
	
	public CookieStoreService(HttpServletRequest request, HttpServletResponse response, String name, String domain){
		this.cookie = new CookieUtil(request, response);
		this.name = name;
		this.domain = domain;
	}

	@Override
	public void remove() {
		cookie.delete(name, domain, null);
	}

	@Override
	public void set(String value, int seconds) {
		try {
			cookie.set(name, URLEncoder.encode(value, ENCODING), domain, null, seconds);
		} catch (UnsupportedEncodingException e) {
		}
	}

	@Override
	public String get() {
		try {
			return URLDecoder.decode(cookie.getValue(name), ENCODING);
		} catch (UnsupportedEncodingException e) {

		}
		return null;
	}
}
