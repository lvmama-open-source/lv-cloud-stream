package com.lv.cloud.stream.binder.activemq.properties;

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

public class ActivemqBindingProperties implements BinderSpecificPropertiesProvider {
	
	private ActivemqConsumerProperties consumer = new ActivemqConsumerProperties();

	private ActivemqProducerProperties producer = new ActivemqProducerProperties();

	@Override
	public ActivemqConsumerProperties getConsumer() {
		return consumer;
	}

	public void setConsumer(ActivemqConsumerProperties consumer) {
		this.consumer = consumer;
	}

	@Override
	public ActivemqProducerProperties getProducer() {
		return producer;
	}

	public void setProducer(ActivemqProducerProperties producer) {
		this.producer = producer;
	}
	
}
