package com.qnvip.easemob.util;

import com.qnvip.util.PropertiesUtil;

/**
 * 环信配置类
 * 
 * @author Ericlin
 */
public class Config {
	
	public final static String orgName = PropertiesUtil.getString("easemob.org.name");
	public final static String appName = PropertiesUtil.getString("easemob.app.name");
	public final static String clientId = PropertiesUtil.getString("easemob.client.id");
	public final static String clientSecret = PropertiesUtil.getString("easemob.client.secret");
	
	public final static String baseURL = "https://a1.easemob.com/" + orgName + "/" + appName + "/";
	/** 群最大人数 */
	public final static Integer CHATROOM_MAX_USER = 10000;
}
