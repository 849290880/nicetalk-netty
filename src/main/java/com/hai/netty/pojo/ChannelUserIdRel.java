package com.hai.netty.pojo;

import java.util.HashMap;

import io.netty.channel.Channel;

public class ChannelUserIdRel {
	
	public static HashMap<String,Channel> map = new HashMap<>();
	
	public static void put(String userId, Channel channel) {
		map.put(userId, channel);
	}
	
	public static Channel get(String userId) {
		return map.get(userId);
	}
	
	/**
	 * @Description: 用于测试，查看map中的数据
	 */
	public static void output() {
		for(HashMap.Entry<String,Channel> entry: map.entrySet()) {
			System.out.println("用户的id" + entry.getKey() 
						+"\nchannel的id" + entry.getValue().id().asLongText());
		}
	}
}
