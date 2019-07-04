package com.lv.cloud.stream.binder.rocketmq.core;

import java.util.Map;

import com.lv.cloud.stream.binder.rocketmq.outbound.MessageTopicFactory;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.Lifecycle;

/**
 * rocketmq默认生产者
 * @author xiaoyulin
 *
 */
public class DefaultRocketmqProducerFactory implements ProducerFactory, Lifecycle, DisposableBean{
	
	private Logger logger = LoggerFactory.getLogger(DefaultRocketmqProducerFactory.class);
	
	private final Map<String, Object> configs;
	
	private DefaultMQProducer producer;
	
	private volatile boolean running;
	
	private MessageTopicFactory messageTopicFactory;
	
	public DefaultRocketmqProducerFactory(Map<String, Object> configs){
		this.configs = configs;
	}
	
	@Override
	public DefaultMQProducer createProducer() {
		if(producer != null){
			return producer;
		}
		try {
			// 生产者的组名
			producer = new DefaultMQProducer((String) configs.get("producerGroup"));

			// 指定NameServer地址，多个地址以 ; 隔开
			producer.setNamesrvAddr((String) configs.get("namesrvAddr"));
			/**
			 * Producer对象在使用之前必须要调用start初始化，初始化一次即可 注意：切记不可以在每次发送消息时，都调用start方法
			 */
			producer.start();
		} catch (MQClientException e) {
			logger.error("rocketmq producer start error:{}", e);
		}
		return producer;
	}

	@Override
	public void destroy() throws Exception {
		if(producer != null)
			producer.shutdown();
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void start() {
		this.running = true;
	}

	@Override
	public void stop() {
		try {
			destroy();
		}
		catch (Exception e) {
			logger.error("Exception while closing producer", e);
		}
	}

	public DefaultMQProducer getProducer() {
		return producer;
	}

	public void setProducer(DefaultMQProducer producer) {
		this.producer = producer;
	}

	@Override
	public MessageTopicFactory getMessageTopicFactory() {
		return messageTopicFactory;
	}

	public void setMessageTopicFactory(MessageTopicFactory messageTopicFactory) {
		this.messageTopicFactory = messageTopicFactory;
	}

}
