package tools.test.ip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.carrotsearch.sizeof.RamUsageEstimator;

import tools.Action;

/**
 * 数据文件来自：http://ip.taobao.org:9999/ipdata_download.html，格式8，300多M
 * 
 * @author <a href="mailto:liusan.dyf@taobao.com">liusan.dyf</a>
 * @version 1.0
 * @since 2016年3月3日
 */
public class GeoIpService extends tools.InitializeOnce {
	private static final char SEPARATOR = ',';

	// ---属性
	private String encoding = "utf-8";
	private String dataFolder = "";

	// ---缓存和字段
	private Map<String, String> constantMap = null;
	private byte[] cache = null;
	private int count = 0;// 记录总数
	private String[] infoArr = null;// 把ip位置信息和isp用一个large array来存储

	//
	private int itemLength = 0;// 每一个item的长度

	/**
	 * 过滤掉无效的值
	 * 
	 * @param value
	 * @return
	 */
	private static String adjustValue(String value) {
		if (tools.Validate.isNullOrEmpty(value) || "-1".equals(value))
			return tools.StringUtil.EMPTY;

		return value;
	}

	/**
	 * http://ip.taobao.org:9999/ipdata/language.txt
	 * 
	 * @param file
	 * @return
	 */
	private Map<String, String> loadConstants(File file) {
		// String file = "D:/Downloads/ip/language.txt";
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(file, encoding);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> map = tools.MapUtil.create();

		for (String item : lines) {
			String[] arr = tools.StringUtil.split(item, SEPARATOR);// country,SV,en,EL SALVADOR
			if ("cn".equals(arr[2]) || true) { // 只加载有限的条目
				// country,area,region,city,county,isp
				String key = arr[2] + SEPARATOR + arr[0] + SEPARATOR + arr[1];// key -> en.contry.HK，value -> HongKong
				String value = arr[3];// -> China

				if (map.containsKey(key)) {
					println("重复的key：" + key + " > " + item + ", old value > " + map.get(key));
				} else {
					map.put(key, value);
				}
			}
		}

		println("constant count " + map.size());
		println("constant size " + RamUsageEstimator.humanSizeOf(map));// 2 MB
		// println(map);

		return map;
	}

	private static void println(Object value) {
		System.out.println(value);
	}

	@Override
	protected void doInitialize() {
		// -------------------参数校验
		if (tools.Validate.isNullOrEmpty(dataFolder))
			this.dataFolder = tools.StringUtil.EMPTY;// 可以为空，表示是根目录

		File constantFile = new File(dataFolder + "/language.txt");
		File dataFile = new File(dataFolder + "/ipdata_code_with_maxmind.txt.utf8");

		// -------------------加载常量、字典
		this.constantMap = loadConstants(constantFile);

		// 第一列uint32格式的起始IP地址，
		// 第二列uint32格式的结束IP地址，
		// 第三列是国家编号
		// 第四列是省编号
		// 第五列是地级市编号或者地级市英文（国内采用编号，国外采用英文）
		// 第六列是县编号或者县英文（国内采用编号，国外采用英文）
		// 第七列是ISP编号或者ISP英文（国内采用编号，国外采用英文）
		// 第八列是维度
		// 第九列是经度

		long startMs = System.currentTimeMillis();

		// key = info, value = 转换后的string[]的index
		final Map<String, Integer> infoMap = tools.MapUtil.create();

		// -------------------读取每一行
		LineIterator it = null;
		try {
			it = FileUtils.lineIterator(dataFile, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int index = 0;

		println("start loading data...");

		try {
			while (it.hasNext()) {
				this.count++;// 记录计数
				String line = it.nextLine();// 16806144,16806399,JP,JP_36,Matsue,-1,2000774,35.4722,133.051

				// 解析line
				String[] arr = tools.StringUtil.split(line, SEPARATOR);
				String key = adjustValue(arr[2]) + SEPARATOR + adjustValue(arr[3]) + SEPARATOR + adjustValue(arr[4]);

				// 全球范围来看，城市数据可能会有重复，这里还要看是哪个国家的，key=国家+省+市

				// 得到其他信息的编号，这个编号就是数组的index
				int pointer = 0;
				if (!infoMap.containsKey(key)) {
					pointer = index++;
					infoMap.put(key, pointer);
				} else
					pointer = infoMap.get(key);

				//
				long start = tools.Convert.toLong(arr[0], 0);
				long end = tools.Convert.toLong(arr[1], 0);
				buf.write(tools.Convert.toBytes(start));// 8
				buf.write(tools.Convert.toBytes(end));// 8
				buf.write(tools.Convert.intToBytes(pointer));// 4
				// println(line);
			}

			this.itemLength = 20;// 按照上面的格式，长度=8+8+4
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			LineIterator.closeQuietly(it);
		}

		// -------------------把map转换为string[]，index = map的value
		final String[] tempInfoArr = new String[infoMap.size()];
		tools.MapUtil.eachKey(infoMap, new Action<String>() {
			@Override
			public void execute(String t) {
				int i = infoMap.get(t);
				tempInfoArr[i] = t;
			}
		});

		this.infoArr = tempInfoArr;

		// -------------------看内存消耗情况
		println("data load over, cost " + (System.currentTimeMillis() - startMs));
		println("lines count " + count);
		println("map count " + infoMap.size());
		println("map size " + RamUsageEstimator.humanSizeOf(infoMap));// 16.5MB
		println("info array size " + RamUsageEstimator.humanSizeOf(this.infoArr));// 10.1 MB

		this.cache = buf.toByteArray();

		println("cache langth " + this.cache.length);
		println("cache size " + RamUsageEstimator.humanSizeOf(this.cache));// 116.5 MB
		// println("map " + map);

		// -------------------清理
		infoMap.clear();
		try {
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		buf = null;
		// System.gc();
	}

	public IpEntry getIpInfo(String ip, String lang) {
		if (!this.isInitialized()) {
			throw new RuntimeException("not init yet");
		}

		// -------------------查找
		// String ip = "1.0.130.193";
		long target = tools.Convert.ipToLong(ip);// 16810689;
		if (target <= 0) {
			println("ip转换为long失败：" + ip + ", " + target);
			return null;
		}

		// -------------------二分法查找
		int begin = 0;
		int end = this.count - 1;
		int searchTimes = 0;// 统计查找次数
		byte[] temp = new byte[8];// 获取ip起始位置的临时变量
		byte[] tempItem = new byte[itemLength];//
		while (begin <= end) {
			searchTimes++;// 记录查找次数
			int middle = (begin + end) / 2;

			// // ---一条记录是20字节，所以是从 middle * itemLength 开始的itemLength字节，前8是v1，再8是v2
			// System.arraycopy(cache, middle * itemLength, temp, 0, 8);// 拷贝前8个
			// long v1 = tools.Convert.toLong(temp);
			//
			// System.arraycopy(cache, middle * itemLength + 8, temp, 0, 8);// 再拷贝8个
			// long v2 = tools.Convert.toLong(temp);

			// ---方案2，先拷贝一个单元到小的数组里，然后直接操作小数组 2016-3-5 11:19:18 by liusan.dyf
			System.arraycopy(this.cache, middle * this.itemLength, tempItem, 0, this.itemLength);// 拷贝一个单元到tempItem
			System.arraycopy(tempItem, 0, temp, 0, 8);// 拷贝前8个
			long v1 = tools.Convert.toLong(temp);
			System.arraycopy(tempItem, 8, temp, 0, 8);// 再拷贝8个
			long v2 = tools.Convert.toLong(temp);

			// // ---调试之用
			// if ((v1 > v2) || v1 <= 0 || v2 <= 0) {
			// println("convert error, ip=" + ip + ", long-ip=" + target + ", v1=" + v1 + ", v2=" + v2);
			// break;
			// }

			if (v1 <= target && v2 >= target) {
				// ---找到ip信息的index
				byte[] infoIndex = new byte[4];
				System.arraycopy(tempItem, 8 + 8, infoIndex, 0, 4);// 再拷贝4个
				int x = tools.Convert.toInt(infoIndex);

				// ---找到位置信息
				String itemInfo = this.infoArr[x];// -> TH,TH_66,Phatthalung

				// ---从字段里解析出国家、省份、城市
				String[] itemInfoArr = tools.StringUtil.split(itemInfo, SEPARATOR);
				String countryCode = itemInfoArr[0];
				String regionCode = itemInfoArr[1];
				String city = itemInfoArr[2];

				IpEntry entry = new IpEntry();
				entry.ip = ip;
				entry.countryCode = countryCode;
				entry.country = constantMap.get(lang + ",country," + countryCode);
				entry.region = constantMap.get(lang + ",region," + regionCode);
				entry.city = city;
				entry.lang = lang;
				entry.searchTimes = searchTimes;

				// ---中国的city在常量表里是存在的，这里check一下
				String cityX = constantMap.get(lang + ",city," + city);
				if (cityX != null)
					entry.city = cityX;

				// ---跳出查找
				return entry;
				// break;
			} else if (v1 > target) {
				end = middle - 1;
			} else if (v2 < target) {
				begin = middle + 1;
			}
		}

		return null;
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public void setDataFolder(String dataFolder) {
		this.dataFolder = dataFolder;
	}

	/**
	 * 数据来自：http://ip.taobao.org:9999/ipdata/ipdata_code_with_maxmind.txt.utf8
	 * 
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		//
		long ln = 6689247989L;
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		buf.write(tools.Convert.toBytes(ln));

		byte[] b = buf.toByteArray();
		println(tools.Convert.toLong(b));

		// if (System.currentTimeMillis() > 0)
		// return;

		//
		final String lang = "en";
		final GeoIpService serv = new GeoIpService();
		serv.setDataFolder("D:/Downloads/ip");
		serv.init();
		println(tools.Json.toString(serv.getIpInfo("122.226.247.178", lang)));
		println(tools.Json.toString(serv.getIpInfo("101.66.178.157", lang)));
		println(tools.Json.toString(serv.getIpInfo("221.175.224.245", lang)));
		println(tools.Json.toString(serv.getIpInfo("1.0.130.193", lang)));
		println(tools.Json.toString(serv.getIpInfo("1.100.130.193", lang)));

		// 简单性能测试
		int times = 100000;// 100000;
		final String ip = "42.120.72.30";
		println(tools.Json.toString(serv.getIpInfo(ip, lang)));

		new tools.code.RunTimer().run("xxx", times, new Runnable() {
			@Override
			public void run() {
				serv.getIpInfo(ip, lang);// 172
			}
		});

		tools.Global.sleep(1000 * 10);
	}
}

class IpEntry {
	public String countryCode;
	public String country;
	public String region;
	public String city;
	public String ip;
	public String lang;
	public int searchTimes;
}
