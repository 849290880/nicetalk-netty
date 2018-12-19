package com.hai.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hai.enums.RequestOperationType;
import com.hai.enums.SearchFriendStatus;
import com.hai.pojo.MyFriends;
import com.hai.pojo.Users;
import com.hai.pojo.bo.UsersBO;
import com.hai.pojo.vo.FriendsVO;
import com.hai.pojo.vo.SendRequestUser;
import com.hai.pojo.vo.UsersVO;
import com.hai.service.UsersService;
import com.hai.util.FastDFSClient;
import com.hai.util.FileUtils;
import com.hai.util.JSONResult;
import com.hai.util.MD5Utils;

@RestController
@RequestMapping("/u")
public class UserController {
	
	@Autowired
	private UsersService usersService;

	@Autowired
	private Sid sid;
	
	@Autowired
	private FastDFSClient fileClient;
	
	private static final String IMAGE_PATH_PART = "_80x80.";
	
	/**
	 * 
	 * @param users
	 * @return
	 * @throws Exception
	 * @Description: 登录注册
	 */
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
			
			result.setQrcode("");
			//注册
			usersService.registUser(result);
		}
		
		UsersVO usersVO  = new UsersVO();
		
		BeanUtils.copyProperties(result, usersVO);
		
		return JSONResult.ok(usersVO);
	}
	
	
	/**
	 * 
	 * @param userBO
	 * @return
	 * @Description: 上传头像
	 */
	@PostMapping("/uploadFace")
	public JSONResult uploadFace(@RequestBody UsersBO userBO) {
		
		//定义临时文件路径
		String temp = "c:\\" +userBO.getId() + "base64.png"; 
		
		//返回的数据
		UsersVO usersVO = null;
		
		try {
			//拿到base64Image
			FileUtils.base64ToFile(temp, userBO.getBase64Image());
			
			MultipartFile multipartFile = FileUtils.fileToMultipart(temp);
			
			//上传头像
			String faceBigImageUrl = fileClient.uploadBase64(multipartFile);
			//缩略图名称80*80
			String faceImage = imageUrlHelper(faceBigImageUrl);
			
			Users user=new Users();
			
			user.setId(userBO.getId());
			
			user.setFaceImageBig(faceBigImageUrl);
			
			user.setFaceImage(faceImage);
			
			//更新数据库
			user=usersService.updateUserById(user);
			
			usersVO  = new UsersVO();
			
			BeanUtils.copyProperties(user, usersVO);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSONResult.ok(usersVO);
	}
	
	//返回缩略图的地址
	private static String imageUrlHelper(String faceBigImageUrl) {
		
		String[] arr = faceBigImageUrl.split("\\.");
		
		String faceImage=arr[0]+IMAGE_PATH_PART+arr[1];
		
		return faceImage;
	}
	
	@PostMapping("/updateNickname")
	public JSONResult updateNickname(@RequestBody UsersBO userBO) {
		Users user=new Users();
		
		user.setId(userBO.getId());
		
		user.setNickname(userBO.getNickname());
		
		//更新数据库
		user=usersService.updateUserById(user);
		
		UsersVO usersVO  = new UsersVO();
		
		BeanUtils.copyProperties(user, usersVO);
		return JSONResult.ok(usersVO);
	}
	
	/**
	 * 
	 * @param userBO
	 * @return
	 * @Description: 用户搜索用户名查找用户
	 */
	@PostMapping(value="/searchFriends")
	public JSONResult searchFriends(String userId,String friendName) {
		
		//1.用户不存在或者为空
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(friendName)) {
			return JSONResult.errorMsg("");
		}
		//2.用户是自己
		Users friends = usersService.queryUsersByName(friendName);
		if(friends==null) {
			return JSONResult.errorMsg(SearchFriendStatus.USER_IS_NOT_EXIST.msg);
		}
		
		if(userId.equals(friends.getId())) {
			return JSONResult.errorMsg(SearchFriendStatus.USER_IS_YOURSELF.msg);
		}
		//3.该用户已经是自己的好友了
		MyFriends myFriends = usersService.queryRelationShip(userId, friends.getId());
		if(myFriends!=null) {
			return JSONResult.errorMsg(SearchFriendStatus.RELATIONSHIP_IS_EXIST.msg);
		}
		
		UsersVO usersVO  = new UsersVO();
		
		BeanUtils.copyProperties(friends, usersVO);
		
		
		return JSONResult.ok(usersVO);
	}
	
	/**
	 * 
	 * @param userId
	 * @param friendName
	 * @return
	 * @Description: 添加好友请求
	 */
	@PostMapping("/addRequest")
	public JSONResult addRequest(String userId,String friendName) {
		
		//1.用户不存在或者为空
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(friendName)) {
			return JSONResult.errorMsg("");
		}
		//2.用户是自己
		Users friends = usersService.queryUsersByName(friendName);
		if(friends==null) {
			return JSONResult.errorMsg(SearchFriendStatus.USER_IS_NOT_EXIST.msg);
		}
		
		if(userId.equals(friends.getId())) {
			return JSONResult.errorMsg(SearchFriendStatus.USER_IS_YOURSELF.msg);
		}
		//3.该用户已经是自己的好友了
		MyFriends myFriends = usersService.queryRelationShip(userId, friends.getId());
		if(myFriends!=null) {
			return JSONResult.errorMsg(SearchFriendStatus.RELATIONSHIP_IS_EXIST.msg);
		}
		
		//4.添加请求信息
		JSONResult result = usersService.addRequestFriends(userId, friends.getId());
		
		return result;
	}
	
	
	/**
	 * @return
	 * @Description: 添加好友
	 */
	@PostMapping("/addFriends")
	public JSONResult addFriends(String userId,String friendId) {
		
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(friendId)) {
			return JSONResult.errorMsg("");
		}
		//添加为好友
		usersService.addRelationShip(userId,friendId);
		return JSONResult.ok("添加成功");
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @Description: 根据用户id查询申请好友的请求
	 */
	@PostMapping("/queryRequestFriends")
	public JSONResult queryRequestFriends(String userId) {
		if(userId==null) {
			return JSONResult.errorMsg("用戶id不能為空");
		}
		List<SendRequestUser> list = usersService.queryRequestByUserId(userId);
		return JSONResult.ok(list);
	}
	
	/**
	 * 
	 * @param friendsId
	 * @param userId
	 * @param operType
	 * @return
	 * @Description: 根据操作类型通过好友申请，或者忽略好友申请，operType：-1为忽略 operType：1通过
	 */
	@PostMapping("/handlerAddRequest")
	public JSONResult handlerAddRequest(String friendsId,String userId,Integer operType) {
		//1.非法客户端接口调用
		if(StringUtils.isBlank(friendsId)||StringUtils.isBlank(friendsId)
				||operType==null||StringUtils.isBlank(RequestOperationType.getMsgByKey(operType))) {
			return JSONResult.errorMsg("");
		}
		//2.判断操作类型，-1为忽略 1为通过
		//忽略
		if(RequestOperationType.IGNORE.operType==operType) {
			usersService.ignoreRequest(friendsId,userId);
		}
		//3.通过
		if(RequestOperationType.AGREE.operType==operType) {
			usersService.agree(friendsId,userId);
		}
		//4.返回内容，更新通讯录
		List<FriendsVO> list = usersService.queryFriendsById(userId);
		return JSONResult.ok(list);
	}
	
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @Description: 根据用户ID查询所有的好友
	 */
	@PostMapping("/queryFriends")
	public JSONResult queryFriends(String userId) {
		
		if(StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("");
		}
		//查询所有的好友
		List<FriendsVO> list = usersService.queryFriendsById(userId);
		
		return JSONResult.ok(list);
	}
	
	
	
}
