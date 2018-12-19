package com.hai.mapper;

import java.util.List;

public interface ChatMsgMapperCustom {
	
	public void batchUpdateMsgStatus(List<String> idsList);
}
