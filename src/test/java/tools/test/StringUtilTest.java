package tools.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import tools.StringUtil;
import tools.Validate;
import tools.code.RunTimer;
import tools.http.HttpRequestBuilder;

public class StringUtilTest {

	@Test
	public void getMixedTimesTest() {
		Assert.assertEquals(1, Validate.mixedTimes("abd123"));
		Assert.assertEquals(2, Validate.mixedTimes("ab2d123"));
		Assert.assertEquals(2, Validate.mixedTimes("dredge2008laferty"));
		Assert.assertEquals(0, Validate.mixedTimes("2123"));
		Assert.assertEquals(0, Validate.mixedTimes("abc"));
		Assert.assertEquals(3, Validate.mixedTimes("a2b1c23"));
		Assert.assertEquals(3, Validate.mixedTimes("m7fgn8iq7lte"));
		Assert.assertEquals(2, Validate.mixedTimes("一a只2蝴蝶"));
	}

	@Test
	public void getNumbersTest() {
		String s = "我abc1234-dE|<>,.?⑼aaa⒚bbb⑥";
		Assert.assertEquals("123419196", StringUtil.getNumbers(s));
		Assert.assertEquals("1299768258", StringUtil.getNumbers("⒈⒉⒐⒐⒎⒍⒏⒉⒌⒏"));
		Assert.assertEquals("154038418", StringUtil.getNumbers("154O3㈧41㈧"));
		Assert.assertEquals("107162134", StringUtil.getNumbers("丨O 7丨6 2丨3 4"));
		Assert.assertEquals("957818035", StringUtil.getNumbers("联系口口９５７８一８０３５"));
		Assert.assertEquals("256300082202905", StringUtil.getNumbers("㈡㈤㈥㈢零零零㈧㈡ⅡO②⑨〇⑤"));

		new RunTimer().run("getNumbersTest", 100000, new Runnable() {
			@Override
			public void run() {
				StringUtil.getNumbers("联系口口９５７８一８０３５");// 130ms
			}
		});
	}

	@Test
	public void getLettersTest() {
		String s = "我abc1234-dE|<>,.?";
		Assert.assertEquals("abcdE", StringUtil.getLetters(s));
	}

	@Test
	public void removeCharactersTest() {
		Assert.assertEquals("ab", StringUtil.removeCharacters("a\r\nb", "\r\n"));
		Assert.assertEquals("\r\n", StringUtil.removeCharacters("a\r\nb", "ab"));
	}

	@Test
	public void matchesTest() {
		Assert.assertEquals(true, StringUtil.matches("abc", "a*"));
		Assert.assertEquals(true, StringUtil.matches("a_c", "a*"));
		Assert.assertEquals(true, StringUtil.matches("a_c", "a_*"));
		Assert.assertEquals(true, StringUtil.matches("a_200-c", "a_*"));
		Assert.assertEquals(true, StringUtil.matches("a:sxxx", "a:*"));
		Assert.assertEquals(true, StringUtil.matches("a.sxxx", "a.*"));
		Assert.assertEquals(true, StringUtil.matches("abc", "a*c"));
		Assert.assertEquals(true, StringUtil.matches("ab(c", "ab\\(*"));// 括号要转义
		Assert.assertEquals(true, StringUtil.matches("a(***---\\b)c", "a*c"));
		Assert.assertEquals(false, StringUtil.matches("a(***---\\b)", "a*c"));
	}

	public static List<String> ngram(String text, int n) {
		List<String> list = new ArrayList<String>();
		if (null == text) {
			return list;
		}

		for (int i = 0; i < text.length() + 1 - n; i++) {
			list.add(text.substring(i, i + n));
		}

		return list;
	}

	@Test
	public void equalsTest() {
		final String s = "__temp";

		new RunTimer().run("equalsTest", 100000, new Runnable() {
			@Override
			public void run() {
				// s.equals("__temp");//2-4ms
				s.startsWith("__temp");// 3-4ms
			}
		});
	}

	@Test
	public void ngramTest() {
		final String s = "abdcdefeadfeffffffweasddddddddddddddddexdefasdfged";
		System.out.println(ngram(s, 2));

		// Assert.assertEquals(ngram(s, 2), StringUtil.ngram(s, 2));

		new RunTimer().run("ngramTest", 100000, new Runnable() {
			@Override
			public void run() {
				// ngram(s, 2);// 195
				StringUtil.ngram(s, 2);// 140
			}
		});
	}

	@Test
	public void startsWithAnyTest() {
		Assert.assertEquals(true, StringUtil.startsWithAny("ab", "ab,123"));
		Assert.assertEquals(false, StringUtil.startsWithAny("ab", "xab,123,\n\n"));
	}

	@Test
	public void removeIllegalCharactersTest() {
		final String s = " __--a我！ /爱i ～中国()*/-（）！！!~ ~~";
		Assert.assertEquals("a我爱i中国", StringUtil.removeSpecialCharacters(s));

		Assert.assertEquals("httphao123com", StringUtil.removeSpecialCharacters("http://hao123.com"));

		new RunTimer().run("removeSpecialCharacters", 100000, new Runnable() {
			@Override
			public void run() {
				StringUtil.removeSpecialCharacters(s);// 50
			}
		});
	}

	public static List<String> split(String value, char separtor) {
		List<String> list = new ArrayList<String>();

		int start = 0;
		int end = 0;

		char c;

		for (int i = 0; i < value.length(); i++) {
			c = value.charAt(i);
			end++;
			if (c == separtor) {
				list.add(value.substring(start, end));
				start = end;
			}
		}

		list.add(value.substring(start, end));
		return list;
	}

	static int indexOf(char[] source, int sourceOffset, char[] target, int targetOffset, int fromIndex) {
		// from http://www.arstdesign.com/articles/fastsearch.html
		int sourceCount = source.length;
		int targetCount = target.length;

		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		char first = target[targetOffset];
		int i = sourceOffset + fromIndex;
		int max = sourceOffset + (sourceCount - targetCount);

		startSearchForFirstChar: while (true) {
			/* Look for first character. */
			while (i <= max && source[i] != first) {
				i++;
			}
			if (i > max) {
				return -1;
			}

			/* Found first character, now look at the rest of v2 */
			int j = i + 1;
			int end = j + targetCount - 1;
			int k = targetOffset + 1;
			while (j < end) {
				if (source[j++] != target[k++]) {
					i++;
					/* Look for str's first char again. */
					continue startSearchForFirstChar;
				}
			}
			return i - sourceOffset; /* Found whole string. */
		}
	}

	static int indexOf(String source, String p) {
		return indexOf(source.toCharArray(), 0, p.toCharArray(), 0, 0);
	}

	@Test
	public void stringToMapTest() {
		String str = "b@d2=1.2, b@d1=0.1201, b@d0=0.1, b@d26=2.0, b@d27=1.7, a@d26=1.0, _=1.3542048E9, b@d24=1.4, b@d28=1.8, b@d29=1.9";
		System.out.println(StringUtil.toMap(str, ",", "=", null));
		System.out
				.println(StringUtil.parseQueryString("max_active=10&max_idle=20&max_wait=1000&timeout=5000", "utf-8"));

		new RunTimer().run("stringToMapTest", 0, new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	@Test
	public void splitTest() {
		final String s = "6e06a0ea1330492835,6e06afdb1330564807,011ad2fc1330580868,6e06a9551330647591,6e077320133073434613307956333,6e06a3e21330818102,6e0778731330928272,7475a9991330931397,6e06a276133099607013310215872,6e06ac891331079521,6e06a5a21331251725,011a999a1331267231,6e06a1351331339207,6e06a77b1331422889,6e06a2f81331446395,011a99a9133144742513314528423,6e06af851331508479,6e06affd1331595257,6e06a5021331597090,3a12b70a133163813113324236194,6e0775a2133168263913316894252,6e06a48f1331768357,011ad0dc1331862436,6e0771e01331880413,6e0779d81331950787,6e06ac831332030013,3d8a4e161332082532,6e06acd51332115521,011a9dd51332151990,011ad3111332199932,011ad08a1332221779,011ad3e41332289126,6e06ae901332376113,6f7f14311332416352,6e07718f1332460774,6e06ad611332545614,6e077d50133263606613326414032,011a94131332643076,011ad0c01332727500,011ab3d61332763682,6e06a5ca1332807620,6e06ae9b1332890365,6e06a53a1332981815,6e06681c1333027319,6e0642e2133302902913330315272,6e0776411333151628,6e077f1d1333159772,3a12ba62133320095313332053742,6e0777321333237069,6e06471e133326130213332630582,6e077a671333326606,6e0675ee1333337172,6e06ac571333410067,6e0779ac1333431717,6f7f0be91333440345,6e06a3f31333495806,6e06a60b1333583544,6e0773171333590089,011ad11d1333670228,6e0777ea1333751869,6e06a9861333842823,6e06ae161333926025,6e0770d21334018050,6e06aeb31334101107,011ad0eb1334188573,6e0772181334274531,6e06a3611334362753,011ad1ff1334447535,011ad3071334533366,6e06a0e91334561671,6e06a03d1334621779,6e0770e41334708483,6e0772de1334825955,6e0771f81334878365,6e06a9281334962660,6e077f1c1334995028,6e06adc41335052966,6e07777d133515433213351626742,6e0770c21335244937,3a12b7961335258255,6e077305133531160513353123982,6e06a3f91335406674,6e0656161335418709,6e07780d1335495185,6e06a7611335568883,7bb3f91c1335693364,7bb3e9fd133576220313357676242,7bb3ef501335847668,7bb3e39f133637555613363866055,7bb3eb2c133643906313364482142,7bb3ec5a1336474912,7bb3f28b1336565663,7bb3e087133661978113366353212,7bb3f2e01336653971,7bb3fa6b1336805559,7bb3fb061336820926,7bb3f0a5133722784813372550175,7bb3e85c133733540413373363102,7bb3e3531337401704,7bb3e4d0133748419913374870002,7bb3ec801338173382,7bb3e9901338535288,7bb3e9521338552442,7bb3e76d1339845971,7bb3eed21339924630,7bb3e80b134024527113455538126,01b662841341495121,7bb3e4c21341571945,7bb3e7f6134167119713416746612,7bb3eafc1341736733,7bb3f3171341812812,7bb3f6f71341888990,7bb3e44d1342015001,7bb3f859134206809613420844912,7bb3f840134240804813424522693,7bb3f6161342668097,7938f9f7134311534613431275893,7bb3f5d21343211462,7938462b134339339713433949933,7938f9d41343653308,793847b61344600999,7bb3f661134525841413452619003,7bb3e1c8134542892513454598242,7bb3ebc2134560673013456273664,daca6ac91345735594,7bb3f5981345860954,7bb3e3c11346030981,7bb3ee111346161507,7bb3e4e41346241663,7bb3f1bd134709699813471044074,7bb3f0a81347342711,7bb3f6ae1347527555,7bb3f2021347589692,7bb3fbb51347946239,7bb3eefe1348198852,7bb3e578134854877113485525152,7bb3f5731348727738,7bb3fa61134882104613488259433,7bb3f5df1349491483,793846ac1350208083,7bb3f1c11350217696,7bb3ea95135025986813502765835,7bb3e9ae135035807413503649682,7bb3ed391350442804135047856510,7bb3e3db135052752513505535424,7bb3f5d1135061460213506399873,7bb3fbfb135069406813507232264,7bb3e88b135079865813508016523,ac1872d61350824742,7bb3f4b6135088611113508972922,7bb3f466135097730313509953002,7938f9b2135116295013511668332,7bb3fbc9135122443713512561393,7bb3eb15135164782213516916022,7bb3f658135173406913517673875,7bb3e91c1351851013,7bb3e2811351862087,7bb3f0f31351907654,7bb3fa111352036115,7bb3e0421352189614,7bb3e6891352283086,7938f8111352452354,01b662711352453690,7bb3efe1135253385413525342112,7bb3fa60135269347113527268082,7bb3e9df1353130897,7bb3e39d1353298100,7bb3e81b1353397143,7bb3f6501353570391,7bb3f5ab135381816113538232162,7bb3ee341354087655,6f7e16cc135416654913541676362";
		// System.out.println(tools.Json.toJson(split(s, ',')));

		new RunTimer().run("splitTest", 10000, new Runnable() {
			@Override
			public void run() {
				// split(s, ',');// 211
				// StringUtil.split(s, ",");// 172
				StringUtil.splitEx(s, ",");// 274
			}
		});
	}

	@Test
	public void indexOfLargeTest() {
		final String source = tools.http.HttpRequestBuilder.create("http://religiose.iteye.com/blog/1488806").get()
				.toString();
		final String p = "</body>";
		System.out.println(indexOf(source, p));
		// System.out.println(source);

		final Boyer b = new Boyer(source);
		System.out.println(b.indexOf(p));

		// 测试，正则方案慢了很多
		new RunTimer().run("indexOfLargeTest", 100000, new Runnable() {
			@Override
			public void run() {
				indexOf(source, p);// 600
				// source.indexOf(p);// 230
				// b.indexOf(p);//174
			}
		});
	}

	@Test
	public void countTest() {
		Assert.assertEquals(2, StringUtil.count("abcdefgab", "a"));
		Assert.assertEquals(2, StringUtil.count("大家好才是真的好，大家说是不", "大家"));
		Assert.assertEquals(0, StringUtil.count("大家好才是真的好，大家说是不", "你们"));

		final String source = HttpRequestBuilder.create("http://www.baidu.com").get().toString();
		System.out.println(source);

		Assert.assertEquals(45, StringUtil.count(source, "<a"));

		new RunTimer().run("countTest", 100000, new Runnable() {
			@Override
			public void run() {
				StringUtil.count(source, "<a");// 1321
			}
		});
	}

	@Test
	public void indexOfTest() {
		final String source = "hello，杜有发";
		final String p = "o，";
		System.out.println(indexOf(source, p));

		final Boyer b = new Boyer(source);
		System.out.println(b.indexOf(p));

		// 测试，正则方案慢了很多
		new RunTimer().run("indexof", 100000, new Runnable() {
			@Override
			public void run() {
				// indexOf(source, p);// 13
				// source.indexOf(p);// 4
				// b.indexOf(p);//5
				Boyer.indexOf(source, p);// 12
			}
		});
	}

	@Test
	public void isNumericTest() {
		Assert.assertEquals(true, Validate.isNumeric("-0.5"));
		// Assert.assertEquals(true, Validate.isNumeric("+0.5"));
		// Assert.assertEquals(true, isNumeric("+0.5"));
		Assert.assertEquals(true, isNumeric("-0.5"));

		// 测试，正则方案慢了很多
		new RunTimer().run("test", 100000, new Runnable() {

			@Override
			public void run() {
				Validate.isNumeric("-0.5");// 5ms
				// isNumeric("-0.5");// 180ms
			}
		});
	}

	public static boolean isNumeric(String inputData) {
		return inputData.matches("[-+]?\\d+(\\.\\d+)?");
	}

	@Test
	public void leftRightTest() {
		Assert.assertEquals("x", StringUtil.left("x.sb", "."));
		Assert.assertEquals(" ", StringUtil.left(" .sb", "."));
		Assert.assertEquals("sb", StringUtil.right("x.sb", "."));
		Assert.assertEquals(null, StringUtil.left("xsb", "."));
		Assert.assertEquals(null, StringUtil.right("xsb", "."));
		Assert.assertEquals(null, StringUtil.right(null, "."));
		Assert.assertEquals("2,3", StringUtil.right("1,2,3", ","));
		Assert.assertEquals("3", StringUtil.right("1,2,3", ",", true));
	}

	@Test
	public void md5Test() {
		String act = StringUtil.md5("a");
		String exp = "0cc175b9c0f1b6a831c399e269772661";
		Assert.assertEquals(exp, act);
	}

	@Test
	public void validateSignedContent() {
		String rawContent = StringUtil.getRawContent("14aab3238922bcc25a6f606eb525ffdc56", "");
		Assert.assertEquals(true, rawContent != null);
		Assert.assertEquals("14", rawContent);
	}

	@Test
	public void getFeatureV2Test() {
		final String str = "name=杜有发@age=28,type=dev,boss=false , sex=true|, a=a@#$%^b ";
		Assert.assertEquals("28", StringUtil.getFeature(str, "age="));
		Assert.assertEquals("false", StringUtil.getFeature(str, "boss="));
		Assert.assertEquals("杜有发", StringUtil.getFeature(str, "name="));
		Assert.assertEquals("", StringUtil.getFeature(str, "namex="));
		Assert.assertEquals("true", StringUtil.getFeature(str, "sex="));
		Assert.assertEquals("a", StringUtil.getFeature(str, "a="));

		new RunTimer().run("getFeatureV2", 100000, new Runnable() {

			@Override
			public void run() {
				Assert.assertEquals("28", StringUtil.getFeature(str, "age="));// 55ms
			}
		});
	}

	@Test
	public void getFeatureTest() {
		@SuppressWarnings("deprecation")
		String act = StringUtil.getFeature("name=杜有发@age=28", "age", "@");
		String exp = "28";
		Assert.assertEquals(exp, act);

		new RunTimer().run("getFeature", 100000, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				StringUtil.getFeature("name=杜有发@age=28", "age", "@");// 66ms
			}
		});
	}

	@Test
	public void trimTest() {
		String ip = "10.16.80.220, 218.102.34.64";
		ip = "127.0.0.1";

		if (ip != null && ip.indexOf(',') > -1) {
			String[] arr = tools.StringUtil.split(ip, ",");

			int len = arr.length;

			System.out.println(arr[len - 1].trim());
		}

		Assert.assertEquals("d", tools.StringUtil.trim(",d,", ','));
		Assert.assertEquals("", tools.StringUtil.trim("aaaaaa", 'a'));
	}

	@Test
	public void replaceAllTest() {
		String act = StringUtil.replaceAll("ab", "a", "b");
		String exp = "bb";
		Assert.assertEquals(exp, act);

		act = StringUtil.replaceAllArray("a2a1b", new String[] { "a1", "a2" }, new String[] { "c", "c" });
		exp = "ccb";
		Assert.assertEquals(exp, act);

		// 31ms
		new RunTimer().run("replaceAllTest", 100000, new Runnable() {

			@Override
			public void run() {
				StringUtil.replaceAllArray("hello $name", new String[] { "$name" }, new String[] { "杜有发" });// 48ms/31ms
			}
		});
	}

	@Test
	public void urlEncodeTest() {
		String act = StringUtil.urlEncode("http://127.0.0.1/?a=b&c=d", "utf-8");
		String exp = "http%3A%2F%2F127.0.0.1%2F%3Fa%3Db%26c%3Dd";
		Assert.assertEquals(exp, act);
	}

	@Test
	public void containsAnyTest() {
		String source = "a\nb\ntaobao\n\n";
		Assert.assertEquals(true, StringUtil.containsAny("b", source));
		Assert.assertEquals(true, StringUtil.containsAny("liusan.dyf", source));
	}

	@Test
	public void inSetTest() {
		Assert.assertEquals(true, StringUtil.inSet("a", "a\nb\nc"));
		Assert.assertEquals(true, StringUtil.inSet("b", "a\nb\nc"));
		Assert.assertEquals(true, StringUtil.inSet("c", "a\nb\nc"));

		Assert.assertEquals(false, StringUtil.inSet("d", "a\nb\nc"));
		Assert.assertEquals(true, StringUtil.inSet("d", "d"));

		Assert.assertEquals(false, StringUtil.inSet("d", "a,b,c,e"));

		// 测试效率
		final String source = "cf6ee8f872515793ed9e3e64cbf1f1480\nc7619d0e98714fc766e8ae846eddf07e5\nce38fb8052d13fd74572a80d510e45d90\nce9fd0351243a7d20041fef37b80e518d\nc00000000000000000000000009202267\nc59131d195f3099ec21f9a060a363f6b3\nce19056af5dac6e8ad999281348531442\nc00000000000000000000000005296727\nc593b589528e466f4f92ed1f7a750aecb\nc5313f876a856479875ff5920a4c81acf\nc52dc22e943a82c4a5d1054feefdf8a90\ncb9339c6ef29c6d1bfe45978ff6e55150\nc00000000000000000000000003153338\nc7fd52ff0944e5ad874fd7cc7e54457a1\nc54053dc07e71ddba411c7c050a20fc04\nccf87c9f80008139715276c080dd848ef\nc288ed799da5e5b7695f838b9035e4248\nc723212e9b5ec4c3704b70099ee9a9671\ncc2e618a0552d33d80d11300a0a1136ca\nc19bbd7b08fd6d1f787a08ed584329db3\nc506454fce559bf8d77d5e790c7fef0cd\nc249159cd254eb3e91650d1d8819b4496\nc120b696f8e40ff9215c4e1c52b048df1\ncbdc3714d20ab93f853d56e81d8d18c5d\nc79eef2e06b7018a07dfefa8e53d1bf54\ncef843464e60f2bb110313421497250d7\nc2761155ea3a6494cfc0601db1758096e\nc6d31471c1a74c23de0333de716c8f265\ncd0104ecf02a5327e51b33b9b9c2099fc\nc6dd429b7f8c916aa557e51b26493fca6\nc68e069b2f13007c4d07dc316119f0c8a\ncc35f6a4bb9a08b1f0401a412ceed5f64\nc43f6684d14aebe0dc45b748e9e01f81d\nca66f18016b6cc4a3fdc4bc60ece5fb59\nc92f0f5b0203477748c47b7c4060035d1\ncb52a2b3f554b84507dac993e78ccd111\nc58ffbc22b7c80258e8dbc8f54ee340d1\nc60a18a9a3481b2e3296607f64a928804\nc23f3f98d53d5279e275d929bbca6de31\nc0a372b46cfc88f1f367641aa9c1b185c\nc01ba2a56d7e4a3c48cc64cc2ae752387\ncb394f2a52a46b0a7457bfe45bb46bd43\nc609f683f43549a2c7a60f869f74749d3\nc1342a179f611dc3b7fe4528dc350df02\nc0273aa5c2a2bc40a501ed109f2f7b9af\ncb892a4bd4286ebc6fc6851f8132c13cd\ncc9afc0cc8a2236abfd201bb43d418ee9\ncf475a77a384b53d2a3a7a5632d993bc7\nc23926961d4f1f839686d6bc83764b84b\nc5ab2608c0a8009d266ef1f46abc5d2c9\nc952226cdf4a4fc0b9d8520926ed70c1d\nc1ac019bdd86dc793d46e7a1873f21aeb\nc25ac55600d16e10e10b589ef676fb607\nc53318c85431135a84bb2957eff8f8d3d\nc150eaebf8c247a62832b37efae35bbb3\ncb65a184383d01eed1396c144861ea184\nc9644c4e790a2c7c28640cb6c1a5eddd8\nc00000000000000000000000003153338\nc00000000000000000000000003153338\nc00000000000000000000000000855772\nc00000000000000000000000005296727\nc00000000000000000000000009202267\nc01ba2a56d7e4a3c48cc64cc2ae752387\nc0273aa5c2a2bc40a501ed109f2f7b9af\nc03e3aca2a562d1dcb455270200604bae\nc0a372b46cfc88f1f367641aa9c1b185c\nc1052e4c605dc044a14dc09047fbab7a7\nc120b696f8e40ff9215c4e1c52b048df1\nc121fafe19c3caca0cfa9c40723715ed7\nc1342a179f611dc3b7fe4528dc350df02\nc150eaebf8c247a62832b37efae35bbb3\nc1798345b190b23fbc7e4fbec38ab6ea3\nc19bbd7b08fd6d1f787a08ed584329db3\nc1ac019bdd86dc793d46e7a1873f21aeb\nc1c217617222febc17914fd71cd0d7909\nc23926961d4f1f839686d6bc83764b84b\nc23f3f98d53d5279e275d929bbca6de31\nc249159cd254eb3e91650d1d8819b4496\nc25201a395170d1f6e52820981208f10d\nc252bce8556f1d9daf1afb94a4f8bbaf9\nc25ac55600d16e10e10b589ef676fb607\nc2761155ea3a6494cfc0601db1758096e\nc288ed799da5e5b7695f838b9035e4248\nc289a7a71e7a3490b28155bff1f0942bc\nc290c79f179ade1e96f0106db23486504\nc296a73000cbca72bcd4ada3fb123c91e\nc2b0210eb18643f0bcc362219abc20084\nc2d7728e0da11560e8c624c0f0562fc9f\nc2ec46e68fe047ad169160666cb82b178\nc317815e0913c5dd28273a14f4caa658b\nc32995829888ebc32c499449e39595059\nc350a43f49b7d13753b549f28dc5d243e\nc37336b8baada7705e165ff019e9e79c0\nc3743304dd31654727080aded6d351120\nc38405bc49684293c387664b7f00641dd\nc395dac31aa84fe6d4197d93fe8dbf3b1\nc3c358d4ed812c460df0c8babbd58de9e\nc3caaf790f8329a21307d317f8d78cddf\nc43f6684d14aebe0dc45b748e9e01f81d\nc4922f70645eb45098529ca223c4e006a\nc4b8858f5c51ad4ae0a6a8a8c81527224\nc4d60ed3d2bc4016c3a6bc127efce6752\nc506454fce559bf8d77d5e790c7fef0cd\nc52dc22e943a82c4a5d1054feefdf8a90\nc5313f876a856479875ff5920a4c81acf\nc53318c85431135a84bb2957eff8f8d3d\nc54053dc07e71ddba411c7c050a20fc04\nc58ffbc22b7c80258e8dbc8f54ee340d1\nc59131d195f3099ec21f9a060a363f6b3\nc593b589528e466f4f92ed1f7a750aecb\nc5a32778314b0f2e168f3918a0c9f60ec\nc5ab2608c0a8009d266ef1f46abc5d2c9\nc5ab5cc11464a8315337c30cf3aac51c5\nc5f4981d8723f3e3c932104a12d4cd3cd\nc609f683f43549a2c7a60f869f74749d3\nc60a18a9a3481b2e3296607f64a928804\nc6194fbc9eb118324d03745624c409763\nc628262c857c73d1c750ed31949d8b8e8\nc648cb3ee94f9a0496599ef6d8515a35a\nc666328d8cb45712e4894d9123648f3b0\nc66d19257e24c71701389dcde8041e899\nc68e069b2f13007c4d07dc316119f0c8a\nc6d31471c1a74c23de0333de716c8f265\nc6dd429b7f8c916aa557e51b26493fca6\nc723212e9b5ec4c3704b70099ee9a9671\nc72fd28dcb9f49736e752da9900dae843\nc7619d0e98714fc766e8ae846eddf07e5\nc7635be6a0e7fc0904f6333212ad9beba\nc769d372d8b0cc6d537d50297742cba25\nc795cdadc480ee7676064cd0fda729515\nc79eef2e06b7018a07dfefa8e53d1bf54\nc7b18dd6559bfdb97806330489361b97f\nc7b86bd3d37759bb9f930c3f9c54c7dec\nc7cf0c9742a0c75b24c3d0a645d3b3157\nc7cfc0f83f09fdfd4eb30d16a040e13c4\nc7ecfcc23e604427824d1dfdef9ec4196\nc7fd52ff0944e5ad874fd7cc7e54457a1\nc85e5c27a11d5356e1a5204f0ecfec6c8\nc86cc54577e1e4b7571c3807e6526a0d1\nc86eb917f77ad7eb5f609ab07bae15476\nc88066fc3cc41d6185001bb1286d8a9a8\nc8df3de427404a388f87268365655c153\nc92f0f5b0203477748c47b7c4060035d1\nc93cb6514cc073d915e6c1bed52d4fbf2\nc94c57c2ebf2fd76bb0e21c35f0cb9abc\nc952226cdf4a4fc0b9d8520926ed70c1d\nc9596e183995a014d4b223828960df065\nc95df8b09e995054ecde3dfa01599c4a0\nc9644c4e790a2c7c28640cb6c1a5eddd8\nc976340817551a78f13b68f7b3133d755\nc9a368a95dea062afef6d080ff65bd208\nc9a81493f5edd04881181dfa7dab48775\nc9b009d988f65f81c1ebd2ad634673583\nca0d7b48c3264bd020ceae6ecaf2fc824\nca18281832eaa8db60c113d16c8024268\nca225671ad465540e4bd03450472be2db\nca28103678315d92c2433b1e72fd56753\nca30bb797ba93a9143635b7223128fc58\nc03902729b25a120a8578f7be59b63cc7\nc7af6aa0169f1cb993ad101b5b1a6bcf1\nc9c1265d44a9d906b7e754ac5322b6ade\nccc12385d66eb6962b9323acbf7c73ed2\nc17cfa911d5c23abb19d7245026392780\nc00000000000000000000000003153338\nc58a5702bef740539716cf804806a3ae1\nc8bf757e9843717b9282b707c1aefed4e\nc24dd58815aaa7999a91c4dbf08fb2954\nc2958fedd3f546f9761c606f42d490970\nc59969dd775bdba8740925f472fb2413f\nc3103b5ee79c9883450b391c5363b681f\nce271b353e281bee9794e79fcc88a0f38\nca0cef2f2909c4f70f3f5623618d21cc8\nc81c42c17e501e495e2b0b12ad4b72510\ncb8404df4ab998276bc213a01cb174d97\ncbc870a93b7a2cc1b382ef9ffcc1b99ff\nc0a20ae01a2a1960d0fb2eb6043791386\nc230db4c813db72fb75468ff44e6f4dd4\ncab913de73e6e0c95e747c09cf7315626\ncf549697edb01aee6fb6503215e7f054a\ncda541c2f5291081d5eebfdcad4196a2c\ncb77d7c79c324eb890feb3d3987b3e882\nc37855475741ad9c73d983345fe161d31\nc00000000000000000000000003153338\nc58a5702bef740539716cf804806a3ae1\nc00000000000000000000000003153338\nc2700ed4ec8503b68bfb1ec221b926f77\ncd499bb82c5e432cee098fcac465e02c2\nc9ba0edc0e2ffb356fc385f480d8fd11b\nc17cfa911d5c23abb19d7245026392780\nccc12385d66eb6962b9323acbf7c73ed2\nc7af6aa0169f1cb993ad101b5b1a6bcf1\nc03902729b25a120a8578f7be59b63cc7\ncb42983284125bb9a319896903ea38254";
		Assert.assertEquals(true, StringUtil.inSet("c03902729b25a120a8578f7be59b63cc7", source));
		// Assert.assertEquals(false, StringUtil.inSet("d", source));
		Assert.assertEquals(true, StringUtil.containsAny("c03902729b25a120a8578f7be59b63cc7", source));
		Assert.assertEquals(true, StringUtil.containsAny("c7619d0e98714fc766e8ae846eddf07e5", source));
		System.out.println(source.substring(0, 5));

		new RunTimer().run("inSetTest", 100000, new Runnable() {
			@Override
			public void run() {
				// source.length();// 2
				// source.hashCode();//2
				// "c03902729b25a120a8578f7be59b63cc7d".equals(source);// 2
				// StringUtil.inSet("c03902729b25a120a8578f7be59b63cc7d", source);// 1690 改进后934
				// StringUtil.inSet("c03902729b25a120a8578f7be59b63cc7", source);// 777
				// StringUtil.inSet_("c03902729b25a120a8578f7be59b63cc7", source);// 1263
				// MySqlFunction.find_in_set("c03902729b25a120a8578f7be59b63cc7", source);// 2991
				StringUtil.containsAny("c03902729b25a120a8578f7be59b63cc7", source);// 1328
				// StringUtil.containsAny("c03902729b25a120a8578f7be59b63cc7d", source);// 1300
			}
		});
	}
}
