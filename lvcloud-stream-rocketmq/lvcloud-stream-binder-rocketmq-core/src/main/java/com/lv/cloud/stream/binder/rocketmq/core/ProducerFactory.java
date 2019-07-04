package com.lv.cloud.stream.binder.rocketmq.core;

import com.lv.cloud.stream.binder.rocketmq.outbound.MessageTopicFactory;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

/**
 * 消息生成者
 * @author xiaoyulin
 *
 */
public interface ProducerFactory {
	
	DefaultMQProducer createProducer();
	
	MessageTopicFactory getMessageTopicFactory();
	
	default boolean transactionCapable() {
		return false;
	}
}
