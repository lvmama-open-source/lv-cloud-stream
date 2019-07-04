package com.lv.boot.starter.rocketmq.annotation.enable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.lv.boot.starter.rocketmq.RocketMQAutoConfiguration;

/**
 * 启用JedisCluster
 * @author xiaoyulin
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RocketMQAutoConfiguration.class)
public @interface EnableRocketMQ {
	
	

}
