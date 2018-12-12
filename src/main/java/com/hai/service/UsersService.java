package com.hai.service;

import java.util.List;

import com.hai.pojo.MyFriends;
import com.hai.pojo.Users;
import com.hai.pojo.vo.SendRequestUser;
import com.hai.util.JSONResult;

public interface UsersService {
	
	/**
	 * 
	 * @return
	 * @Description: 根据用户名查询用户
	 */
	public boolean queryUsersByUsersName(String username);
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @Description: 根据用户名和密码查询用户
	 */
	public Users queryUsersForLogin(String username,String password);
	
	/**
	 * 
	 * @param user
	 * @Description: 根据用户对象注册用户
	 */
	public void registUser(Users user);
	
	/**
	 * 
	 * @return
	 * @Description: 通过id更新user的BigImage和image
	 */
	public Users updateUserById(Users users);
	
	/**
	 * 
	 * @return Users
	 * @Description: 根据用户名查询用户
	 */
	public Users queryUsersByName(String username);
	
	/**
	 * @param userId
	 * @param friendsId
	 * @return
	 * @Description: 根据用户id和他查询的好友的id查找关系
	 */
	public MyFriends queryRelationShip(String userId,String friendsId);
	
	/**
	 * @param userId
	 * @param friendsId
	 * @return
	 * @Description: 根据用户id和他查询的好友的id添加好友关系
	 */
	public void addRelationShip(String userId,String friendsId);
	
	/**
	 * 
	 * @param userId
	 * @param firendsId
	 * @Description: 根据用户id和他查询的好友的id添加发送好友申请
	 */
	public JSONResult addRequestFriends(String userId,String firendsId);
	
	/**
	 * 
	 * @param acceptUserId
	 * @return
	 * @Description: 根據用戶Id查詢得到請求添加好友的列表
	 */
	public List<SendRequestUser> queryRequestByUserId(String acceptUserId);
	
}
