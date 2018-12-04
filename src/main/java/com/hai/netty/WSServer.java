package com.hai.netty;

import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Component
public class WSServer {
	
	private static class SingletonWSServer{
		static final WSServer instance = new WSServer();
	}
	
	public static WSServer getInstance() {
		return SingletonWSServer.instance;
	}
	
	private EventLoopGroup bossGroup;
	
	private EventLoopGroup workGroup;
	
	private ServerBootstrap server;
	
	private ChannelFuture future;
	
	public WSServer() {
		bossGroup = new NioEventLoopGroup();
		workGroup = new NioEventLoopGroup();
		server = new ServerBootstrap();
		server.group(bossGroup, workGroup)
		.channel(NioServerSocketChannel.class)
		.childHandler(new WSServerInitializer());
	}
	
	public void start() {
		future = server.bind(8088);
		System.err.println("netty websocket server 启动完毕");
	}
	
}
