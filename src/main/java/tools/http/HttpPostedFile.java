package tools.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 通过HTTP上传的文件类
 * 
 * @author liusan.dyf
 */
public class HttpPostedFile {
	private String fieldName;
	private String fileName;
	private byte[] content;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getBytes() {
		return content;
	}

	/**
	 * 直接设置文件流
	 * 
	 * @param bytes
	 */
	public void setContent(byte[] bytes) {
		this.content = bytes;
	}

	public void setContent(InputStream content) throws IOException {
		this.content = HttpUtil.inputStreamToByte(content);
	}

	public void setContent(File file) throws IOException {
		this.setContent(new FileInputStream(file));
	}

	/**
	 * 从http的一个url上下载资源，用于图片、文件等 2012-11-07 by liusan.dyf
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void setContent(String url) throws IOException {
		this.content = HttpRequestBuilder.create(url).get().getBytes();
	}
}
