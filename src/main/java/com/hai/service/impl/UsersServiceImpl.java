package com.hai.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hai.mapper.UsersMapper;
import com.hai.pojo.Users;
import com.hai.service.UsersService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UsersMapper usersMapper;
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public boolean queryUsersByUsersName(String username) {
		Users user = new Users();
		user.setUsername(username);
		
		Users result = usersMapper.selectOne(user);
		
		return result!=null;
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Users queryUsersForLogin(String username, String password) {
		
		Example example = new Example(Users.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("username", username).andEqualTo("password", password);
		Users result = usersMapper.selectOneByExample(example);
		
		return result;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void registUser(Users user) {
		
		usersMapper.insert(user);
	}

}
