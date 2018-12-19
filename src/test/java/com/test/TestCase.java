package com.test;

import org.junit.Test;
import org.springframework.beans.BeanUtils;

import com.hai.pojo.Users;
import com.hai.pojo.vo.UsersVO;

public class TestCase {
	
	@Test
	public void testCase1() {
		
		UsersVO usersVO = new UsersVO();
		usersVO.setId("1");
		
		Users users = new Users();
		users.setId("3");
		
		BeanUtils.copyProperties(users, usersVO);
		System.out.println(usersVO.getId());
	}
}
