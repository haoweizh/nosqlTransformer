package com.qnvip.easemob.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.qnvip.easemob.Response;


/**
 * 环信开发辅助方法
 * @author Ericlin
 *
 */
public class EasemobUtil {
	
	public static void main(String[] args) {
		String id = createChatroom("聊天组", "我是一测试聊天组", "q101434376998581100");
		System.out.println(id);
//		System.out.println(joinChatroom("134458802991792544", "q101434377000835100"));
		getChatRoomDetail(id);
//		System.out.println(quitChatroom("134458802991792544", "q101434377000835100"));
//		addFriend("q101434376998581100", "q101434376999113100");
//		deleteFriend("q101434376998581100", "q101434376999113100");
	}
	
	/**
	 * 创建聊天室
	 * 
	 * @param name	名称
	 * @param desc	描述
	 * @param owner	创建者
	 * @return
	 */
	public static String createChatroom(String name, String desc, String owner) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", StringUtils.isEmpty(name) ? "群聊室" : name);
		params.put("description", StringUtils.isEmpty(desc) ? "群主很懒，什么都没留下" : desc);
		params.put("maxusers", Config.CHATROOM_MAX_USER);
		params.put("owner", owner);
		String respStr = Helper.doPost("chatrooms", params);
		Gson gson = new Gson();
		Response response = gson.fromJson(respStr, Response.class);
		Map<String, Object> map = (Map<String, Object>) response.getData();
		return (String) map.get("id");
	}
	
	/**
	 * 加入聊天室
	 * 
	 * @param groupId
	 * @param username
	 */
	public static boolean joinChatroom(String chatroomId, String username) {
		String url = "chatrooms/" + chatroomId + "/users/" + username;
		String resp = Helper.doPost(url, null);
		Gson gson = new Gson();
		Response response = gson.fromJson(resp, Response.class);
		Map<String, Object> map = (Map<String, Object>) response.getData();
		return (boolean) map.get("result");
	}
	
	/**
	 * 退出聊天室
	 * 
	 * @param chatroomId
	 * @param username
	 * @return
	 */
	public static boolean quitChatroom(String chatroomId, String username) {
		String url = "chatrooms/" + chatroomId + "/users/" + username;
		String resp = Helper.doDelete(url, null);
		Gson gson = new Gson();
		Response response = gson.fromJson(resp, Response.class);
		Map<String, Object> map = (Map<String, Object>) response.getData();
		return (boolean) map.get("result");
	}
	
	/**
	 * 查询聊天室详情
	 * 
	 * @param chatrootId
	 */
	public static void getChatRoomDetail(String chatrootId) {
		String url = "chatrooms/" + chatrootId;
		String resp = Helper.doGet(url, null);
		System.out.println(resp);
	}
	
	/**
	 * 添加好友
	 * 
	 * @param ownerUsername
	 * @param friendUsername
	 */
	public static void addFriend(String ownerUsername, String friendUsername) {
		String url = "users/" + ownerUsername + "/contacts/users/" + friendUsername;
		Helper.doPost(url, null);
	}
	
	/**
	 * 删除好友
	 * 
	 * @param ownerUsername
	 * @param friendUsername
	 */
	public static void deleteFriend(String ownerUsername, String friendUsername) {
		String url = "users/" + ownerUsername + "/contacts/users/" + friendUsername;
		Helper.doDelete(url, null);
	}
	
	/**
	 * 加入黑名单
	 * 
	 * @param ownerUsername
	 * @param friendUsername
	 */
	public static void addBlock(String ownerUsername, String friendUsername) {
		String url = "users/" + ownerUsername + "/blocks/users";
		Map<String, Object> params = new HashMap<>();
		String[] usernames = {friendUsername};
		params.put("usernames", usernames);
		Helper.doPost(url, params);
	}
	
	/**
	 * 从黑名单删除
	 * 
	 * @param ownerUsername
	 * @param friendUsername
	 */
	public static void deleteBlock(String ownerUsername, String friendUsername) {
		String url = "users/" + ownerUsername + "/blocks/users/" + friendUsername;
		Helper.doDelete(url, null);
	}
}
