package tools.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {
	public static final String DEFAULT_CHARSET = "utf-8";

	public static String decode(final String content, final String encoding) {
		if (content == null) {
			return null;
		}
		try {
			return URLDecoder.decode(content, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String encode(final String content, final String encoding) {
		if (content == null) {
			return null;
		}
		try {
			return URLEncoder.encode(content, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toUrlEncodedString(final Map<String, String> parameters, final String encoding) {
		final StringBuilder result = new StringBuilder();
		boolean willAppendAnd = false;

		for (final Entry<String, String> item : parameters.entrySet()) {
			String key = item.getKey();
			String value = item.getValue();

			// 2011-12-19如果为blank，这不用追加了
			if (key == null || key.length() == 0 || value == null || value.length() == 0)
				continue;

			final String encodedName = encode(key, encoding);
			final String encodedValue = encode(value, encoding);

			if (willAppendAnd) {// if (result.length() > 0) {
				result.append('&');
			} else
				willAppendAnd = true;

			// append name value
			result.append(encodedName);
			result.append('=');
			result.append(encodedValue);
		}
		return result.toString();
	}

	public static byte[] inputStreamToByte(InputStream stream) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		int ch;
		while ((ch = stream.read()) != -1) {
			byteStream.write(ch);
		}
		byte data[] = byteStream.toByteArray();
		byteStream.close();
		return data;
	}
}
