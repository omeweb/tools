package tools.jsonrpc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 以jsonrpc的方式响应，支持扩展getObject、authenticate、intercept，以及用简单的userName/password做验证 <br />
 * 执行顺序为：doFilter -》 authenticate -》 intercept -》JsonRpcService.invoke -》 output
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2013-12-17
 */
public class JsonRpcFilterV2 implements Filter {
	private static final Log logger = LogFactory.getLog("system");

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	/**
	 * 得到要调用的对象，支持几种形式：spring.xxx和class.com.taobao.xxx静态类方式
	 * 
	 * @param objKey
	 * @return
	 */
	public Object getObject(String objKey) {
		if (tools.Validate.isEmpty(objKey))
			return null;

		// 得到调用的对象
		Object targetObject = null;

		String clz = "class.";

		if (objKey.startsWith(clz)) {// 调用静态类 2013-12-19
			try {
				targetObject = Class.forName(objKey.substring(clz.length()));
			} catch (ClassNotFoundException e) {

			}
		}
		return targetObject;
	}

	/**
	 * 看是否有权限调用 2013-12-19 by liusan.dyf
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean authenticate(ServletRequest request, ServletResponse response) {
		// TODO 判断token是否合法 2015-5-7 20:47:51 by liusan.dyf
		return true;
	}

	/**
	 * 交给子类，看能否尝试和优先处理，如果返回false表示没有打断
	 * 
	 * @param objKey
	 * @param requestId
	 * @param method
	 * @param params
	 * @param jsoncallback
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean intercept(String objKey, String requestId, String method, String params, String jsoncallback,
			ServletRequest request, ServletResponse response) {
		return false;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
		ServletException {
		// jsoncallback 2013-11-01 by liusan.dyf
		String jsoncallback = request.getParameter("jsoncallback");

		// 认证
		if (!this.authenticate(request, response)) {
			output(response, JsonRpcService.toJsonRpcErrorResult(0, -32602, "权限不足"), jsoncallback);
			return;
		}

		// invoke.do?obj=spring.xxx&method=getXXX&params=...

		// ajax下，Ajax强制将中文内容进行UTF-8编码，这样导致进入后端后使用UTF-8进行解码时发生乱码
		// http://www.blogjava.net/xylz/archive/2011/06/10/352073.html

		// input_charset
		String inputCharset = request.getParameter("input_charset");

		// output_charset
		String outputCharset = request.getParameter("output_charset");
		if (tools.Validate.isNullOrEmpty(outputCharset))
			outputCharset = "utf-8";

		// 如果有charset参数，输入和输出都按照这个 2013-12-20
		String charset = request.getParameter("charset");
		if (!tools.Validate.isNullOrEmpty(charset)) {
			inputCharset = charset;
			outputCharset = charset;

			response.setCharacterEncoding(outputCharset);// 显式设置，让CharacterEncodingFilter失效
		}

		// 结果
		String result = null;

		// http头部，支持jsoncallback参数
		if (tools.Validate.isNullOrEmpty(jsoncallback)) {
			tools.web.ServletUtil.addJsonContentType((HttpServletResponse) response, outputCharset);
		} else {
			tools.web.ServletUtil.addContentType((HttpServletResponse) response, "application/x-javascript",
					outputCharset);
		}

		// key
		String objKey = request.getParameter("obj");
		String requestId = request.getParameter("id");// jsonrpc的requestId
		String method = request.getParameter("method");
		String params = request.getParameter("params");

		// 解码
		if (!tools.Validate.isNullOrEmpty(inputCharset)) {
			// 只有params需要解码 2013-12-19 by liusan.dyf
			if (!tools.Validate.isNullOrEmpty(params)) {
				params = java.net.URLDecoder.decode(params, inputCharset);
			}
		}

		// 尝试处理，这里可以做拦截 2013-12-19 by liusan.dyf
		if (this.intercept(objKey, requestId, method, params, jsoncallback, request, response))
			return;

		// 得到对象
		Object targetObject = getObject(objKey);
		int rid = tools.Convert.toInt(requestId, 0);

		// 开始调用
		if (targetObject != null) {
			try {
				// if (tools.Validate.isNullOrEmpty(params)) // 2014-01-09 by liusan.dyf
				// params = "{}";

				Object resultObj = JsonRpcService.invoke(targetObject, method, params, true);
				result = JsonRpcService.toJsonRpcResult(rid, resultObj);
			} catch (MethodNotFoundException e) {
				result = JsonRpcService.toJsonRpcErrorResult(rid, JsonRpcService.METHOD_NOT_FOUND_ERROR_CODE,
						e.getMessage());

				logger.error(e);
			} catch (Exception e) {
				result = JsonRpcService.toJsonRpcErrorResult(rid, 0, e.getCause().getMessage());
				logger.error(e);
			}
		} else {
			result = JsonRpcService.toJsonRpcErrorResult(rid, -32602, "错误的请求，请检查参数obj、method、params是否合法！");
		}

		// 输出
		output(response, result, jsoncallback);
		return;
	}

	public void output(ServletResponse response, String content, String jsonCallback) throws IOException {
		boolean hasJsonCallback = !tools.Validate.isNullOrEmpty(jsonCallback);

		// 2013-11-01 by liusan.dyf
		if (hasJsonCallback)
			response.getWriter().append(jsonCallback + "(");

		response.getWriter().append(content);

		// 2013-11-01 by liusan.dyf
		if (hasJsonCallback)
			response.getWriter().append(");");

		response.getWriter().close();
	}
}
