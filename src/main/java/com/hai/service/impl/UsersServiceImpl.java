package com.hai.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hai.mapper.FriendsRequestMapper;
import com.hai.mapper.MyFriendsMapper;
import com.hai.mapper.UsersMapper;
import com.hai.mapper.UsersMapperCustom;
import com.hai.pojo.FriendsRequest;
import com.hai.pojo.MyFriends;
import com.hai.pojo.Users;
import com.hai.pojo.vo.SendRequestUser;
import com.hai.service.UsersService;
import com.hai.util.FastDFSClient;
import com.hai.util.FileUtils;
import com.hai.util.JSONResult;
import com.hai.util.QRCodeUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private UsersMapperCustom usersMapperCustom;
	
	
	@Autowired
	private QRCodeUtils qrCodeUtils;
	
	@Autowired
	private FastDFSClient fastDFSClient;
	
	@Autowired
	private MyFriendsMapper myFriendsMapper;
	
	@Autowired
	private FriendsRequestMapper friendsRequestMapper;
	
	@Autowired
	private Sid sid;
	
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
		//生成QRCode
		String qrCodePath = "C:\\"+user.getId() + "qrcode.png";
		String content = "nicetalk_name:" + user.getUsername();
		qrCodeUtils.createQRCode(qrCodePath, content);
		
		//上传到文件服务器
		MultipartFile qrCodeFile =FileUtils.fileToMultipart(qrCodePath);
		String qrCodeUrl = "";
		try {
			qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		user.setQrcode(qrCodeUrl);
		
		usersMapper.insert(user);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public Users updateUserById(Users users) {
		usersMapper.updateByPrimaryKeySelective(users);
		return queryUsersById(users);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS)
	private Users queryUsersById(Users users) {
		return usersMapper.selectByPrimaryKey(users.getId());
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Users queryUsersByName(String username) {
		Example example = new Example(Users.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("username", username);
		Users users = usersMapper.selectOneByExample(example);
		return users;
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public MyFriends queryRelationShip(String userId, String friendsId) {
		Example example = new Example(MyFriends.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("myUserId", userId);
		criteria.andEqualTo("myFriendUserId", friendsId);
		MyFriends myFriends = myFriendsMapper.selectOneByExample(example);
		return myFriends;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void addRelationShip(String userId, String friendsId) {
		MyFriends myFriends = new MyFriends();
		myFriends.setId(sid.nextShort());
		myFriends.setMyUserId(userId);
		myFriends.setMyFriendUserId(friendsId);
		myFriendsMapper.insert(myFriends);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public JSONResult addRequestFriends(String userId, String firendsId) {
		//1.判断是否已经有过申请：查询有没有这条记录，有就返回已发送申请
		Example example = new Example(FriendsRequest.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("sendUserId", userId);
		criteria.andEqualTo("acceptUserId", firendsId);
		FriendsRequest result = friendsRequestMapper.selectOneByExample(example);
		if(result!=null) {
			return JSONResult.errorMap("已发发送申请啦~");
		}
		//2.没有发送过申请，并且不是好友的关系
		FriendsRequest friendsRequest = new FriendsRequest();
		friendsRequest.setId(sid.nextShort());
		friendsRequest.setAcceptUserId(firendsId);
		friendsRequest.setSendUserId(userId);
		friendsRequest.setRequestDataTime(new Date());
		friendsRequestMapper.insert(friendsRequest);
		
		return JSONResult.ok("已发送请求啦!");
	}

	@Override
	public List<SendRequestUser> queryRequestByUserId(String acceptUserId) {
		
		return usersMapperCustom.queryRequestByUserId(acceptUserId);
	}
}
