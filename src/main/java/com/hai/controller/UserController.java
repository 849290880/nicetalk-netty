package com.hai.controller;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hai.pojo.Users;
import com.hai.pojo.vo.UsersVO;
import com.hai.service.UsersService;
import com.hai.util.JSONResult;
import com.hai.util.MD5Utils;

@RestController
@RequestMapping("/u")
public class UserController {
	
	@Autowired
	private UsersService usersService;

	@Autowired
	private Sid sid;
	
	@PostMapping("/loginOrRegist")
	public JSONResult loginOrRegist(@RequestBody Users users) throws Exception {
		
		String username = users.getUsername();
		
		String password = users.getPassword();
		
		// 1. 用户名和密码不能为空
		if(StringUtils.isBlank(username)||StringUtils.isBlank(password)) {
			return JSONResult.errorMsg("用户名或密码不能为空~");
		}
		
		// 2.查询用户名是否存在，不存在就注册
		boolean exist = usersService.queryUsersByUsersName(username);
		
		Users  result = null;
		if(exist) {
			//登录判断
			//根据用户名和密码查询用户是否存在
			result = usersService.queryUsersForLogin(username,MD5Utils.getMD5Str(password));
			if(result==null) {
				return JSONResult.errorMsg("用户名或者密码为空");
			}
			
		}else {
			result=new Users();
			result.setUsername(username);
			result.setPassword(MD5Utils.getMD5Str(password));
			result.setId(sid.nextShort());
			result.setFaceImage("");
			result.setFaceImageBig("");
			result.setNickname(username);
			result.setCid(users.getCid());
			//生成QRCode
			result.setQrcode("");
			//注册
			usersService.registUser(result);
		}
		
		UsersVO usersVO  = new UsersVO();
		
		BeanUtils.copyProperties(result, usersVO);
		
		return JSONResult.ok(usersVO);
	}
}
