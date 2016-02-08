package com.qnvip.easemob.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.qnvip.easemob.model.AccessToken;
import com.qnvip.util.MySSLProtocolSocketFactory;

/**
 * 辅助类
 * 
 * @author Ericlin
 */
public class Helper {
	
	private static Log log = LogFactory.getLog(EasemobUtil.class);
	
	private static long get_time = 0l;
	private static AccessToken accessToken = null;
	
	public static String getToken() {
//		return "YWMtBl1mjJmNEeWxfa2vwsNHxAAAAVKbq7c5z-4EdHJltQ6TaBcIuhXtZVrPEMI";
		if (accessToken == null || get_time + accessToken.getExpires_in() <= (System.currentTimeMillis() / 1000)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("client_id", Config.clientId);///
			params.put("client_secret", Config.clientSecret);
			params.put("grant_type", "client_credentials");
			String resp = doPost("token", params, null);
			Gson gson = new Gson();
			accessToken = gson.fromJson(resp, AccessToken.class);
			get_time = System.currentTimeMillis() / 1000;
		}
		return accessToken.getAccess_token();
	}

	public static String doPost(String url, Map<String, Object> params) {
		return doPost(url, params, getToken());
	}
	
	@SuppressWarnings("deprecation")
	public static String doPost(String url, Map<String, Object> params, String token) {
		url = Config.baseURL + url;
		int tryNumber = 0;
		while (true) {
			StringBuffer response = new StringBuffer();
			HttpClient client = new HttpClient();
			client.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
			if (url.indexOf("https://") >= 0) {
				Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);   
				Protocol.registerProtocol("https", myhttps);
			}
			PostMethod post = new PostMethod(url);
			
			post.addRequestHeader("Content-Type", "application/json");
			if (StringUtils.isNotEmpty(token)) {
				post.addRequestHeader("Authorization", "Bearer " + token);
			}
			if (params != null) {
				post.setRequestBody(new Gson().toJson(params));
			}
			post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			try {
				client.executeMethod(post);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(post.getResponseBodyAsStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				if (post.getStatusCode() == HttpStatus.SC_OK) {
					return response.toString();
				} else if (post.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
					if (tryNumber >= 3) {
						return null;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					tryNumber ++;
					continue;
				} else {
					log.error("环信处理出错，错误码：" + post.getStatusCode());
				}
				return null;
			} catch (IOException e) {
				log.error("环信处理出错：" + response.toString());
				e.printStackTrace();
			} finally {
				post.releaseConnection();
			}
		}
	}
	
	public static String doGet(String url, String queryString) {
		url = Config.baseURL + url;
		int tryNumber = 0;
		while (true) {
			StringBuffer response = new StringBuffer();
			HttpClient client = new HttpClient();
			client.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
			if (url.indexOf("https://") >= 0) {
				Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);   
				Protocol.registerProtocol("https", myhttps);
			}
			if (StringUtils.isNotEmpty(queryString)) {
				if (url.indexOf("?") > 0) {
					url += "&" + queryString;
				} else {
					url += "?" + queryString;
				}
			}
			GetMethod get = new GetMethod(url);
			
			get.addRequestHeader("Content-Type", "application/json");
			get.addRequestHeader("Authorization", "Bearer " + getToken());
			get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			try {
				client.executeMethod(get);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(get.getResponseBodyAsStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				if (get.getStatusCode() == HttpStatus.SC_OK) {
					return response.toString();
				} else if (get.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
					if (tryNumber >= 3) {
						return null;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					tryNumber ++;
					continue;
				} else {
					log.error("环信处理出错，错误码：" + get.getStatusCode());
				}
			} catch (IOException e) {
				log.error("环信处理出错：" + response.toString());
				e.printStackTrace();
			} finally {
				get.releaseConnection();
			}
		}
	}
	
	public static String doDelete(String url, String queryString) {
		url = Config.baseURL + url;
		int tryNumber = 0;
		while (true) {
			StringBuffer response = new StringBuffer();
			HttpClient client = new HttpClient();
			client.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
			if (url.indexOf("https://") >= 0) {
				Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);   
				Protocol.registerProtocol("https", myhttps);
			}
			if (StringUtils.isNotEmpty(queryString)) {
				if (url.indexOf("?") > 0) {
					url += "&" + queryString;
				} else {
					url += "?" + queryString;
				}
			}
			DeleteMethod delete = new DeleteMethod(url);
			
			delete.addRequestHeader("Content-Type", "application/json");
			delete.addRequestHeader("Authorization", "Bearer " + getToken());
			delete.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			try {
				client.executeMethod(delete);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(delete.getResponseBodyAsStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				if (delete.getStatusCode() == HttpStatus.SC_OK) {
					return response.toString();
				} else if (delete.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE){
					if (tryNumber >= 3) {
						return null;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					tryNumber ++;
					continue;
				} else {
					log.error("环信处理出错，错误码：" + delete.getStatusCode());
				}
			} catch (IOException e) {
				log.error("环信处理出错：" + response.toString());
				e.printStackTrace();
			} finally {
				delete.releaseConnection();
			}
		}
	}
}
