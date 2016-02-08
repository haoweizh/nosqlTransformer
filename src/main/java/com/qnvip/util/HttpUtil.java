/* 
  * Created by linzheyan at 2012-1-13 
 * Copyright HiSupplier.com 
 */

package com.qnvip.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang3.StringUtils;

/**
 * @author linzheyan
 *
 * 2012-1-13
 */
public class HttpUtil {
	
	public static String doGet(String url, String queryString) {
		return doGet(url, queryString, null);
	}

	public static String doGet(String url, String queryString, Map<String, String> headers) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		if (StringUtils.isNotEmpty(queryString)) {
			if (url.indexOf("?") > 0) {
				url += "&" + queryString;
			} else {
				url += "?" + queryString;
			}
		}
		HttpMethod method = new GetMethod(url);
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				method.addRequestHeader(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "utf-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}
	
	public static String doPost(String url, Map<String, String> params) {
		return doPost(url, params, null);
	}
	
	public static String doPost(String url, Map<String, String> params, Map<String, String> headers) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		if (url.indexOf("https://") >= 0) {
			Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);   
			Protocol.registerProtocol("https", myhttps);
		}
		PostMethod post = new PostMethod(url);
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				post.addRequestHeader(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
		if (params != null) {
			NameValuePair[] pairs = new NameValuePair[params.size()];
			int i = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				pairs[i++] = new NameValuePair(entry.getKey(), entry.getValue());
			}
			post.setRequestBody(pairs);
		}
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		try {
			client.executeMethod(post);
			if (post.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(post.getResponseBodyAsStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return response.toString();
	}
	
	/**
	 * @param validationUrl
	 * @return
	 */
	public static String retrieveResponseFromServer(URL validationUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) validationUrl.openConnection();
            final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            final StringBuffer stringBuffer = new StringBuffer(255);

            synchronized (stringBuffer) {
                while ((line = in.readLine()) != null) {
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
                return stringBuffer.toString();
            }

        } catch (final IOException e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
