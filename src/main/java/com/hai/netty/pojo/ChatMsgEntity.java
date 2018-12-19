package com.hai.netty.pojo;

import java.io.Serializable;

public class ChatMsgEntity implements Serializable {

	private static final long serialVersionUID = 1560990072106426934L;
	
	private String senderId;	//发送至的用户id
	private String receiverId;	//接收者的用户id
	private String msg;			//聊天内容
	private String msgId;		//用于消息的签收
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
