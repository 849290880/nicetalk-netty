package com.hai.netty.pojo;

import java.io.Serializable;


public class DataContent implements Serializable{

	private static final long serialVersionUID = 4876401037362742451L;

	private Integer action; 	// 动作类型
	private ChatMsgEntity chatMsgEntity;	// 用户的聊天内容entity
	private String extand;		//扩展字段
	
	
	public ChatMsgEntity getChatMsgEntity() {
		return chatMsgEntity;
	}
	public void setChatMsgEntity(ChatMsgEntity chatMsgEntity) {
		this.chatMsgEntity = chatMsgEntity;
	}
	public Integer getAction() {
		return action;
	}
	public void setAction(Integer action) {
		this.action = action;
	}
	public String getExtand() {
		return extand;
	}
	public void setExtand(String extand) {
		this.extand = extand;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
