package com.hai.netty;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hai.SpringUtil;
import com.hai.enums.MsgActionEnum;
import com.hai.netty.pojo.ChannelUserIdRel;
import com.hai.netty.pojo.ChatMsgEntity;
import com.hai.netty.pojo.DataContent;
import com.hai.service.UsersService;
import com.hai.util.JsonUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 
 * @author 这是处理消息的handler
 *TextWebSocketFrame： 在Netty中，是用于为websocket专门处理文本的对象,frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

	//用于记录和管理所有客户端的channel
	private static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ct2x, TextWebSocketFrame msg) throws Exception {
		//获取客户端发送的消息
		String content = msg.text();
		
		Channel curChannel = ct2x.channel();
		
		//1.获取客户端发来的消息
		DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
		Integer action = dataContent.getAction();
		//2.判断消息类型，根据不同的类型来处理不同的业务
		
		if(action == MsgActionEnum.CONNECT.type) {
			// 2.1当websocket 第一次open的时候，初始化channel，把用户的id和channel关联起来
			String senderId = dataContent.getChatMsgEntity().getSenderId();
			// 将用户id和当前的channel关联起来
			ChannelUserIdRel.put(senderId, curChannel);
			
			//打印users所有的channel
			for(Channel c: users) {
				System.out.println("channelId为:" + c.id().asLongText());
			}
			
			//测试打印 map中的内容
			ChannelUserIdRel.output();
			
		}else if(action == MsgActionEnum.CHAT.type) {
			// 2.2聊天的消息保存在数据库当中，同时标记消息的签收状态，标记为[未签收]
			ChatMsgEntity chatMsgEntity = dataContent.getChatMsgEntity();
			
			UsersService userService = SpringUtil.getBean("usersServiceImpl", UsersService.class);
			String msgId = userService.saveMsg(chatMsgEntity);
			chatMsgEntity.setMsgId(msgId);
			
			//消息转发或者推送
			Channel channel = ChannelUserIdRel.get(chatMsgEntity.getReceiverId());
			if(channel!=null) {
				Channel findChannel = users.find(channel.id());
				if(findChannel!=null) {
					findChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(chatMsgEntity)));
				}else {
					//用户和netty服务没有连接，消息推送
					
				}
			}else {
				//该用户没有和没有注册到map表上，等于没有上线，消息推送( JPush,个推，小米 )
			}
		}else if(action == MsgActionEnum.SIGNED.type) {
			// 2.3签收消息的类型，根据不同的消息进行签收，修改数据库的内容为[已签收]
			String ids = dataContent.getExtand();
			String[] idsArray = ids.split(",");
			List<String> idsList = new ArrayList<>();
			
			for (String idMsg : idsArray) {
				if(StringUtils.isNotBlank(idMsg)) {
					idsList.add(idMsg);
				}
			}
			
			System.out.println(idsList);
			
			if(idsList!=null && !idsList.isEmpty()&& idsList.size() > 0) {
				UsersService userService = SpringUtil.getBean("usersServiceImpl", UsersService.class);
				userService.batchSignChatMsg(idsList);
				System.err.println(userService.getClass().getName());
			}
			
		}else if(action == MsgActionEnum.KEEPALIVE.type) {
			// 2.5心跳机制的消息
			
		}
	}

	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channel，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		users.add(channel);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		//当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
		users.remove(ctx.channel());
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
		users.remove(ctx.channel());
	}

}
