package com.lv.cloud.stream.binder.rocketmq.streams;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.StreamListenerSetupMethodOrchestrator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RocketmqStreamListenerSetupMethodOrchestrator implements StreamListenerSetupMethodOrchestrator, ApplicationContextAware {

	@Override
	public boolean supports(Method method) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void orchestrateStreamListenerSetupMethod(StreamListener streamListener, Method method, Object bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		
	}

}
