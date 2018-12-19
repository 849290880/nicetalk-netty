package com.hai.mapper;

import java.util.List;

import com.hai.pojo.Users;
import com.hai.pojo.vo.FriendsVO;
import com.hai.pojo.vo.SendRequestUser;
import com.hai.util.MyMapper;

public interface UsersMapperCustom extends MyMapper<Users> {

	/**
	 * 
	 * @param sendUserId
	 * @return
	 * @Description: 根据acceptUserId查询的发送添加申请人列表
	 */
	public List<SendRequestUser> queryRequestByUserId(String acceptUserId);
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @Description: 根据userId查询FriendsVO，返回list
	 */
	public List<FriendsVO> selectFriendsVOById(String userId);
}