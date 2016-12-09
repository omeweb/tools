package tools.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import tools.Convert;
import tools.Validate;

/**
 * 依赖于commons-io 2011-11-07
 * 
 * @author liusan.dyf
 */
public class FileDataStore implements DataStore {

	private String baseDirectory;
	private static final String ENCODING = "utf-8";

	public FileDataStore(String baseDirectory) {
		// 检查
		if (Validate.isEmpty(baseDirectory))
			throw new IllegalArgumentException("baseDirectory属性不能为空");
		baseDirectory = baseDirectory.replace("\\", "/");
		this.baseDirectory = baseDirectory;

		File f = new File(baseDirectory);
		if (f.exists()) {
			if (!f.isDirectory())
				throw new IllegalArgumentException("baseDirectory必须要是合法的目录");
		} else
			// 创建目录
			f.mkdirs();
	}

	@Override
	public long get(String key) {
		String filePath = getFilePath(key);

		File f = new File(filePath);
		if (!f.exists())
			return 0;
		try {
			// 可能不支持unix系统
			@SuppressWarnings("deprecation")
			String v = IOUtils.toString(new URL("file:///" + filePath));
			return Convert.toLong(v, 0);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean set(String key, long value) {
		String filePath = getFilePath(key);
		File f = new File(filePath);
		try {
			if (!f.exists())
				f.createNewFile();

			FileOutputStream s = new FileOutputStream(f);
			IOUtils.write(String.valueOf(value), s, ENCODING);
			s.flush();
			s.close();

			// java.io.FileNotFoundException: d:\data\temp\id.txt
			// (另一个程序正在使用此文件，进程无法访问。)

			// System.out.println("saved");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private String getFilePath(String key) {
		if (baseDirectory.endsWith("/"))
			return baseDirectory + key + ".txt";
		else
			return baseDirectory + "/" + key + ".txt";
	}
}
