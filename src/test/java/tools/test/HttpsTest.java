package tools.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class HttpsTest {
	public static void main(String[] args) throws Exception {
		URL url = new URL(
				"https://mtee.alibaba-inc.com/requestLimited.do?obj=kv&method=getOne&params={%22typeCode%22:%22app%22,%22key%22:%22raider%22}&t=0&appKey=raider&appSecret=taobao1234&host=raider010152034010.et1/10.152.34.10");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
		conn.connect();
		InputStream ip = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(ip));
		String line;
		StringBuffer strb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			strb.append(line);
		}
		String ss = strb.toString();
		System.out.println(ss);
	}
}

class TrustAnyHostnameVerifier implements HostnameVerifier {
	public boolean verify(String hostname, SSLSession session) {
		// 直接返回true
		return true;
	}
}
