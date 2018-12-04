package com.hai;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.hai.netty.WSServer;

@Component
public class NettyBoot implements ApplicationListener<ContextRefreshedEvent>{

	/**
	 * 在SpringBoot启动完毕之后才启动Netty
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null) {
			try {
				WSServer.getInstance().start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
