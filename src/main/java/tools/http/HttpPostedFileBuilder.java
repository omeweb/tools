package tools.http;

import java.io.File;
import java.io.IOException;

/**
 * TODO 类说明
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2012-11-7
 */
public class HttpPostedFileBuilder {
	private HttpPostedFile entry = new HttpPostedFile();

	public static HttpPostedFileBuilder create(String path) {
		HttpPostedFileBuilder rtn = new HttpPostedFileBuilder();

		try {
			rtn.entry.setContent(new File(path));
		} catch (IOException e) {
		}

		return rtn;
	}

	public HttpPostedFileBuilder field(String value) {
		entry.setFieldName(value);
		return this;
	}

	public HttpPostedFileBuilder fileName(String value) {
		entry.setFieldName(value);
		return this;
	}
}
