package com.hai.enums;

public enum RequestOperationType {
	
	AGREE(1,"通过请求"),
	IGNORE(-1,"忽略申请");
	
	public Integer operType;
	public String msg;
	
	RequestOperationType(Integer operType,String msg) {
		this.msg = msg;
		this.operType = operType;
	}
	
	public Integer getOperType() {
		return operType;
	}
	
	public static String getMsgByKey(Integer operType) {
		for (RequestOperationType type : RequestOperationType.values()) {
			if(type.getOperType()==operType) {
				return type.msg;
			}
		}
		return null;
	}
}
