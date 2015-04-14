package tools.generator;

import java.util.HashMap;

/**
 * 生成数字编号的类，线程安全的，默认的实现则需要一个文件写入的权限；2010-08-06增加该类
 * 
 * @author liusan.dyf
 */
public class GeneratorFacade {
	/**
	 * 缓存IdGenerator
	 */
	private static HashMap<String, IdGenerator> dict = new HashMap<String, IdGenerator>();

	// /**
	// * 缓存DataStore，不用每次都新建
	// */
	// private static HashMap<String, DataStore> dataStore = new HashMap<String,
	// DataStore>();

	// 生成一个 IdGenerator

	private static IdGenerator getGenerator(String key) {
		if (dict.containsKey(key)) {
			return dict.get(key);
		} else
			throw new NoSuchGeneratorException("指定key的Generator不存在，请先调用init方法:" + key);
	}

	public static IdGenerator createIdGenerator(String key, DataStore dataStore, int bufferSize, int increment) {
		AtomicIdGenerator g = new AtomicIdGenerator(key, dataStore, bufferSize, increment);
		return g;
	}

	public static long current(String key) {
		return getGenerator(key).current();
	}

	public static long next(String key) {
		return getGenerator(key).next();
	}

	/**
	 * 2012-02-13 by liusan.dyf
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public static String next(String key, int count) {
		return getGenerator(key).next(count);
	}

	/**
	 * 设置初始值，生成的最小值从初始值+1开始
	 * 
	 * @param key
	 * @param value
	 */
	public static void setInitialValue(String key, long value) {
		getGenerator(key).setInitialValue(value);
	}

	/**
	 * 基于文件的存储器 2012-06-21 by liusan.dyf
	 * 
	 * @param key
	 * @param dataPath
	 * @param bufferSize
	 * @param increment
	 */
	public static void init(String key, String dataPath, int bufferSize, int increment) {
		DataStore st = null;
		// // 先产生DataStore
		// if (!dataStore.containsKey(key)) {
		// synchronized (dataStore) {
		st = new FileDataStore(dataPath);
		// if (!dataStore.containsKey(dataPath))
		// dataStore.put(dataPath, st);
		// }
		// } else
		// st = dataStore.get(dataPath);

		init(key, st, bufferSize, increment);
	}

	/**
	 * 2012-02-12
	 * 
	 * @param key
	 * @param g
	 */
	public static void init(String key, IdGenerator g) {
		if (!dict.containsKey(key)) {
			synchronized (dict) {// 防止多线程调用出现“已添加了具有相同键的项”
				if (!dict.containsKey(key)) // db check，前面的“if (!_dict.ContainsKey(key))”不是线程安全的
					dict.put(key, g);
			}
		}
	}

	/**
	 * bufferSize和increment都可以为0，increment默认为1，bufferSize默认为10<br />
	 * 可以多次调用，如果已经初始化，则不再进行处理
	 * 
	 * @param key
	 * @param dataStore
	 * @param bufferSize
	 * @param increment
	 */
	public static void init(String key, DataStore dataStore, int bufferSize, int increment) {
		init(key, createIdGenerator(key, dataStore, bufferSize, increment));
	}

	/**
	 * 2012-06-21 by liusan.dyf
	 * 
	 * @param key
	 */
	public static void remove(String key) {
		synchronized (dict) {
			dict.remove(key);
		}
	}

	/**
	 * 2012-06-21 by liusan.dyf
	 */
	public static void removeAll() {
		synchronized (dict) {
			dict.clear();
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		String key = "id";
		String dataPath = "d:/data/temp";
		GeneratorFacade.init(key, dataPath, 100, 10);
		for (int i = 0; i < 10; i++) {
			System.out.println("currentId:" + GeneratorFacade.current(key) + ",id:" + GeneratorFacade.next(key));
		}

		// Random r = new Random();
		// for(int i=0;i<100;i++) {
		// System.out.println(r.nextGaussian()*10);
		// }
	}
}
