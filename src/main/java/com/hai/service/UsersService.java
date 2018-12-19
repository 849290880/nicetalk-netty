package com.hai.service;

import java.util.List;

import com.hai.netty.pojo.ChatMsgEntity;
import com.hai.pojo.MyFriends;
import com.hai.pojo.Users;
import com.hai.pojo.vo.FriendsVO;
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
	
	/**
	 * @param friendsId
	 * @param userId
	 * @Description: 忽略好友申请
	 */
	public void ignoreRequest(String friendsId, String userId);
	
	/**
	 * 
	 * @param friendsId
	 * @param userId
	 * @Description: 通过好友申请
	 */
	public void agree(String friendsId, String userId);

	/**
	 * 
	 * @param userId
	 * @return
	 * @Description: 根据用户id查询通讯录里需要的内容，返回list
	 */
	public List<FriendsVO> queryFriendsById(String userId);
	
	/**
	 * @param chatMsgEntity
	 * @Description: 保存用户聊天消息到数据库,返回消息的id
	 */
	public String saveMsg(ChatMsgEntity chatMsgEntity);

	/**
	 * @param idsList
	 * @Description: 根据list里面的MsgId批量修改数据库的签收状态
	 */
	public void batchSignChatMsg(List<String> idsList);
	
}
