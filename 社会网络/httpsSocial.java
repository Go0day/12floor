package login.yang.com;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpClientKeepSession {
		
		public static String getCookie(String urls) throws IOException {
			URL url = new URL(urls);
	        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			String urlParameters = "username=user07&password=fb6f3684";   //这里改用户名密码
	        conn.setHostnameVerifier(new HostnameVerifier() {
	            @Override
	            public boolean verify(String arg0, SSLSession arg1) {
	                return true;
	            }
	        });
	        

	        //System.out.println(conn.getResponseMessage());*/
	        conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = conn.getResponseCode();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			Map<String, List<String>> hdrs = conn.getHeaderFields();
	        Set<String> hdrKeys = hdrs.keySet();
	        List<String> cookie= hdrs.get("Set-Cookie");
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			conn.disconnect();
			return cookie.get(0);
		}
		public static String getKey(String urls,String cookies) throws IOException {
			URL url = new URL(urls);
	        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			conn.setRequestProperty("Cookie", cookies);
	        conn.setHostnameVerifier(new HostnameVerifier() {
	            @Override
	            public boolean verify(String arg0, SSLSession arg1) {
	                return true;
	            }
	        });
	        

	        //System.out.println(conn.getResponseMessage());*/
	        conn.setDoOutput(true);

			int responseCode = conn.getResponseCode();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			Map<String, List<String>> hdrs = conn.getHeaderFields();
	        Set<String> hdrKeys = hdrs.keySet();
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			conn.disconnect();
			return response.toString();
		}
		private static class DefaultTrustManager implements X509TrustManager {

	        @Override
	        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

	        @Override
	        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

	        @Override
	        public X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }
	    }
		public static String getAccount(String urls,String cookies,String key,String args) throws IOException {
			URL url = new URL(urls);
	        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			conn.setRequestProperty("Api-Key", key);
			conn.setRequestProperty("Cookie", cookies);
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
	        conn.setHostnameVerifier(new HostnameVerifier() {
	            @Override
	            public boolean verify(String arg0, SSLSession arg1) {
	                return true;
	            }
	        });
	        

	        //System.out.println(conn.getResponseMessage());*/
	        conn.setDoOutput(true);
	        OutputStream wr = conn.getOutputStream();
			wr.write(args.getBytes());
			wr.flush();
			wr.close();

			int responseCode = conn.getResponseCode();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			Map<String, List<String>> hdrs = conn.getHeaderFields();
	        Set<String> hdrKeys = hdrs.keySet();
	        List<String> cookie= hdrs.get("Set-Cookie");
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			conn.disconnect();
			return response.toString();
		}
		
		public static void main(String[] args) throws Exception {
			SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
	        SSLContext.setDefault(ctx);
	        String urls="https://47.104.168.77/cancer/system/login";
	        String cookie=HttpClientKeepSession.getCookie(urls);
	        String key=HttpClientKeepSession.getKey("https://47.104.168.77/cancer/system/license/read", cookie);
	        String args1 = "{\"api_name\":\"/list_all_accounts\",\"count\":10,\"offset\":1,\"account_type\":null,\"account_id\":null,\"username\":null,\"sort_type\":-1}";
	        String result=HttpClientKeepSession.getAccount("https://47.104.168.77/cancer/api/account",cookie,key,args1);
	        System.out.println(result);
			
		}
		
				
		
		
}