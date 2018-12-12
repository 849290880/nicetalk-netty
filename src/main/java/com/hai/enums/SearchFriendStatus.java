package com.hai.enums;

/**
 * 
 * @description 搜索好友出现的状态
 *	
 */
public enum SearchFriendStatus {
	
	SUCESS(0,"成功"),
	USER_IS_NOT_EXIST(1,"用户不存在"),
	USER_IS_YOURSELF(2,"不能添加自己"),
	RELATIONSHIP_IS_EXIST(3,"已经是好友了");
	
	public final Integer status;
	public final String msg;
	
	SearchFriendStatus(Integer status,String msg){
		this.status = status;
		this.msg = msg;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	
	public static String getMsgByKey(Integer status) {
		for (SearchFriendStatus type : SearchFriendStatus.values()) {
			if(type.getStatus()==status) {
				return type.msg;
			}
		}
		return null;
	}
}
