package com.vc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class HttpRequestHandler {

	String URL;
	private final String USER_AGENT = "Mozilla/5.0";

	public HttpRequestHandler(String URL) {
		this.URL = URL;
	}

	public String requestGET() throws Exception {
		URL obj = new URL(URL);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.connect();
		System.out.println("res=" + conn.getResponseCode());
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuilder builder = new StringBuilder();
		while ((inputLine = in.readLine()) != null)
			builder.append(inputLine);
		in.close();
		return builder.toString();
	}

	public String RequestPOST(List<NameValuePair> urlParameters) {
		// String url = URL;//"https://selfsolve.apple.com/wcResults.do";
		// String url
		// ="http://retailbanking_mybluemix.net/banking/icicibank/account_summary?client_id=hemal.kotecha@hotmail.com&token=fd32283fc694&custid=88882126";

		System.setProperty("com.sun.net.ssl.rsaPreMasterSecretFix", "true");
		System.setProperty("https.protocols", "TLSv1,SSLv3");
		// System.setProperty("javax.net.ssl.trustStore",
		// "C:\\Program Files\\Java\\jre6\\lib\\security\\cacerts");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(URL);

		// add header
		post.setHeader("User-Agent", USER_AGENT);
		String responseStr = null;

		try {
			String param = "";
			for (NameValuePair nameValuePair : urlParameters) {
				param += nameValuePair.getName() + "="
						+ nameValuePair.getValue() + ", ";
			}
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response;
			response = client.execute(post);

			int resCode = response.getStatusLine().getStatusCode();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			responseStr = result.toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Post=" + responseStr);
		return responseStr;
	}

	public static void main(String[] args) throws Exception {
		// String url = "http://google.com";
		String url = "http://corporate_bank.mybluemix.net/corporate_banking/mybank/authenticate_client?client_id=hemal.kotecha@hotmail.com&password=ICIC7081";
		// String url =
		// "http://158.85.156.19:80/corporate_banking/mybank/authenticate_client";//?client_id=hemal.kotecha@hotmail.com&password=ICIC7081";

		// String url
		// ="http://retailbanking.mybluemix.net/banking/icicibank/account_summary?client_id=hemal.kotecha@hotmail.com&token=fd32283fc694&custid=88882126";
		// List<NameValuePair> list = new ArrayList<NameValuePair>();
		// list.add(new
		// BasicNameValuePair("client_id","hemal.kotecha@hotmail.com"));
		// list.add(new BasicNameValuePair("password","ICIC7081"));

		// System.out.println(new HttpRequestHandler(url).requestGET(list));

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.connect();
		System.out.println("res=" + conn.getResponseCode());

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		System.out.println(sb.toString());

	}

}
