package com.lv.cloud.stream.binder.rocketmq.properties;

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

public class RocketmqBindingProperties implements BinderSpecificPropertiesProvider {
	
	private RocketmqConsumerProperties consumer = new RocketmqConsumerProperties();

	private RocketmqProducerProperties producer = new RocketmqProducerProperties();

	@Override
	public RocketmqConsumerProperties getConsumer() {
		return consumer;
	}

	public void setConsumer(RocketmqConsumerProperties consumer) {
		this.consumer = consumer;
	}

	@Override
	public RocketmqProducerProperties getProducer() {
		return producer;
	}

	public void setProducer(RocketmqProducerProperties producer) {
		this.producer = producer;
	}
	
}
