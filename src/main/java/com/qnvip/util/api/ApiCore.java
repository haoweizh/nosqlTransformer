package com.qnvip.util.api;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.qnvip.util.RSA;

public class ApiCore {
	private static Log log = LogFactory.getLog(ApiCore.class);
	private final static String UTF8 = "utf-8";
	
	/**
	 * 客户端接口参数签名建议使用此方法，以统一传入格式
	 * 格式： {name1, value1, name2,value2...} 成对传入
	 * @param key
	 * @param args 需要签名的参数
	 * @return
	 */
	public static String sign(String key, Object...args) {
		String text = createLinkString(createParamsMap(args)) + key;
		return DigestUtils.md5Hex(getContentBytes(text, UTF8));
	}
	
	/**
	 * 对map型参数签名
	 * 将map数据取出拼装成k1=v1&k2=v2字符串后再签名
	 * 
	 * @param key
	 * @param params
	 * @return 签名结果
	 */
	public static String sign(String key, Map<String, String> params) {
		String text = createLinkString(params) + key;
		return DigestUtils.md5Hex(getContentBytes(text, UTF8));
	}
	
	/**
	 * 传入的url参数，不得有urlencode转码，否则会验签会出错
	 * 
	 * @param key
	 * @param queryString URL格式参数  a=b&c=d
	 * @return
	 */
	public static String sign(String key, String queryString) {
		Map<String, String> params = new HashMap<>();
		if (StringUtils.isNotEmpty(queryString)) {
			for (String node : queryString.split("&")) {
				String[] param = node.split("=");
				String k = param[0];
				String v = null;
				if (param.length == 2) {
					v = StringUtils.isNotEmpty(param[1]) ? param[1] : null;
				}
				params.put(k, v);
			}
		}
		return sign(key, params);
	}
	
	/**
	 * 验证签名
	 * 格式： {name1, value1, name2,value2...} 成对传入
	 * @param key 
	 * @param sign 待验证的签名
	 * @param args 需要签名的参数
	 */
	public static boolean verify(String key, String sign, Object... args) {
		String text = createLinkString(createParamsMap(args)) + key;
		String mysign = DigestUtils.md5Hex(getContentBytes(text, UTF8));
		return mysign.equals(sign) ? true : false;
	}
    
    /**
     * 服务端验证使用此方法
     * @param key
     * @param sign 待验证的签名
     * @param params
     * @return
     */
    public static boolean verify(String key, String sign, Map<String, String> params) {
    	String text = createLinkString(params) + key;
    	String mysign = DigestUtils.md5Hex(getContentBytes(text, UTF8));
    	return mysign.equals(sign) ? true : false;
    }
	
	/**
	 * 加密
	 * @param text
	 * @return
	 */
	public static String encrypt(String text) {
		try {
			return RSA.encryptByPublicKey(text, ApiConfig.PUBLIC_KEY);
		} catch (Exception e) {
			log.error("RSA加密失败", e);
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * 解密
	 * @param text
	 * @return
	 */
	public static String decrypt(String text) {
		try {
			return RSA.decryptByPrivateKey(text, ApiConfig.PRIVATE_KEY);
		} catch (Exception e) {
			log.error("RSA解密失败", e);
		}
		return StringUtils.EMPTY;
	}

	private static Map<String, String> createParamsMap(Object... args) {
		Map<String, String> params = new HashMap<String, String>();
		int length = args.length;
		for (int i = 0, count = length / 2, index = 0; i < count; i++) {
			index = i * 2;
			params.put(String.valueOf(args[index]), String.valueOf(args[index + 1]));
		}
		return params;
	}
	
	/** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
    	if (params == null || params.size() == 0)
    		return StringUtils.EMPTY;
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder link = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = StringUtils.isEmpty(params.get(key)) ? StringUtils.EMPTY : params.get(key);
            link.append("&").append(key).append("=").append(value);
        }
        return link.substring(1);
    }
    
    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException 
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
}
