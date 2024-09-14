package com.geeke.config.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.geeke.common.IActionSaver;

/**
 * 日志写入配置类
 * @author lys
 * version: 2021/11/16
 */

@Configuration
public class ActionSaverConfig {
	
	@Value("${spring.rabbitmq.enable: false}")
	private boolean enableMq;		// 是否启用mq

	/**
	 * spring.rabbitmq.enable=true时，启用消息队列入库；=false时，直接入库
	 * @return
	 */
	@Bean(name = "actionSaver")
	public IActionSaver actionSaverBean() {
//		if(enableMq) {
//			return new MqActionSaver();
//		}

		return new DbActionSaver();
	}


	public boolean isEnableMq() {
		return enableMq;
	}


	public void setEnableMq(boolean enableMq) {
		this.enableMq = enableMq;
	}
	
}
