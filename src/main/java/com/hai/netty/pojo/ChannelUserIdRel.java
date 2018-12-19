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
}
