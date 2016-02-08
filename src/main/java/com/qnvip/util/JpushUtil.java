package com.qnvip.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.utils.StringUtils;

public class JpushUtil {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(JpushUtil.class);
	
	private final static boolean devMod = true;
	
	private final static String JPUSH_MASTERSECRET = "c09654453440b7e6e8c2312f",
								JPUSH_APPKEY       = "42eb4a12db3beacda5c351ea";

	/**
	 * ios/android推送
	 * @param content 内容
	 * @return
	 * @throws APIRequestException 
	 * @throws APIConnectionException 
	 */
	public static boolean push2All(String content) throws APIConnectionException, APIRequestException {
		return push(
				Audience.all(),
				IosNotification.newBuilder().setAlert(content).setSound("default").setBadge(1).build(),
				AndroidNotification.newBuilder().setAlert(content).build());
	}

	/**
	 * ios/android推送
	 * @param content 内容 args 推送字段
	 * @return
	 * @throws APIRequestException 
	 * @throws APIConnectionException 
	 */
	public static boolean push2AllWithExtras(String content, String... args) throws APIConnectionException, APIRequestException {
		cn.jpush.api.push.model.notification.IosNotification.Builder iosBuilder = IosNotification
				.newBuilder().setAlert(content).setSound("default").setBadge(1);
		cn.jpush.api.push.model.notification.AndroidNotification.Builder androidBuilder = AndroidNotification
				.newBuilder().setAlert(content);
		int length = args.length;
		if (length >= 2) {
			for (int i = 0, count = length / 2, index = 0; i < count; i++) {
				index = i * 2;
				iosBuilder.addExtra(args[index], args[index + 1]);
				androidBuilder.addExtra(args[index], args[index + 1]);
			}
		}
		return push(Audience.all(), iosBuilder.build(), androidBuilder.build());
	}
	
	/**
	 * 指定tag ios/android推送
	 * @param content 内容
	 * @return
	 * @throws APIRequestException 
	 * @throws APIConnectionException 
	 */
	public static boolean push2Tag(String content, String tag) throws APIConnectionException, APIRequestException {
		return push(
				Audience.tag(tag),
				IosNotification.newBuilder().setAlert(content).setSound("default").setBadge(1).build(),
				AndroidNotification.newBuilder().setAlert(content).build());
	}
	

	/**
	 * ios/android推送
	 * @param content 内容
	 * @param tag 多的标签以,隔开
	 * @param args
	 * @return
	 * @throws APIRequestException 
	 * @throws APIConnectionException 
	 */
	public static boolean push2TagWithExtras(String content, String tag, String... args) throws APIConnectionException, APIRequestException {
		cn.jpush.api.push.model.notification.IosNotification.Builder iosBuilder = IosNotification
				.newBuilder().setAlert(content).setSound("default").setBadge(1);
		cn.jpush.api.push.model.notification.AndroidNotification.Builder androidBuilder = AndroidNotification
				.newBuilder().setAlert(content);
		int length = args.length;
		if (length >= 2) {
			for (int i = 0, count = length / 2, index = 0; i < count; i++) {
				index = i * 2;
				iosBuilder.addExtra(args[index], args[index + 1]);
				androidBuilder.addExtra(args[index], args[index + 1]);
			}
		}
		return push(Audience.tag(tag.split(",")), iosBuilder.build(), androidBuilder.build());
	}
	
	/**
	 * 指定alias ios/android推送
	 * @param content
	 * @param alias 多的别名以,隔开
	 * @return
	 * @throws APIRequestException 
	 * @throws APIConnectionException 
	 */
	public static boolean push2Alias(String content, String alias) throws APIConnectionException, APIRequestException {
		return push(Audience.alias(alias.split(",")), IosNotification.newBuilder()
				.setAlert(content).setSound("default").setBadge(1).build(),
				AndroidNotification.newBuilder().setAlert(content).build());
	}
	
	/**
	 * 指定alias ios/android推送 增加多个推送字段
	 * @param content
	 * @param alias 多的别名以,隔开
	 * @param args 多字段
	 * @return
	 * @throws APIRequestException 
	 * @throws APIConnectionException 
	 */
	public static boolean push2AliasWithExtras(String content, String alias, String... args) throws APIConnectionException, APIRequestException {
		if (StringUtils.isEmpty(alias)) return false;
		IosNotification.Builder iosBuilder =
				IosNotification.newBuilder().setAlert(content).setSound("default").setBadge(1);
		AndroidNotification.Builder androidBuilder =
				AndroidNotification.newBuilder().setAlert(content);
		int length = args.length;
		if (length >= 2) {
			for (int i = 0, count = length / 2, index = 0; i < count; i++) {
				index = i * 2;
				iosBuilder.addExtra(args[index], args[index + 1]);
				androidBuilder.addExtra(args[index], args[index + 1]);
			}
		}
		return push(Audience.alias(alias.split(",")), iosBuilder.build(), androidBuilder.build());
	}

	private static boolean push(Audience audience, IosNotification iosNotification,
			AndroidNotification androidNotification) throws APIConnectionException, APIRequestException {
		JPushClient jpushClient = new JPushClient(JPUSH_MASTERSECRET, JPUSH_APPKEY);
		PushPayload payload = null;
		if (devMod) {
			payload = PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(audience)
					.setNotification(Notification.newBuilder()
							.addPlatformNotification(iosNotification).build())
					.build();
			payload.resetOptionsApnsProduction(false);
		} else {
			payload =
					PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(audience)
							.setNotification(Notification.newBuilder()
									.addPlatformNotification(iosNotification)
									.addPlatformNotification(androidNotification).build())
							.build();
		}
		PushResult result = jpushClient.sendPush(payload);
		if (result.isResultOK()) {
			return true;
		} else {
			return false;
		}
	}
}
