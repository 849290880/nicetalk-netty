package com.hai.service;

import com.hai.pojo.Users;

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
}
