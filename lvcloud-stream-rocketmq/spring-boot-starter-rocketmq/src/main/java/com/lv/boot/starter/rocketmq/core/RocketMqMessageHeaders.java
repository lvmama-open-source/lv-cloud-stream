package com.lv.boot.starter.rocketmq.core;

import java.util.Map;

import org.springframework.messaging.MessageHeaders;

/**
 * rocketmq message headers
 * @author xiaoyulin
 *
 */
@SuppressWarnings("serial")
public class RocketMqMessageHeaders extends MessageHeaders {

	public RocketMqMessageHeaders(Map<String, Object> headers) {
		super(headers);
	}

	@Override
	public Map<String, Object> getRawHeaders() { //NOSONAR - not useless, widening to public
		return super.getRawHeaders();
	}
	
	@Override
	public Object put(String key, Object value) {
		return this.getRawHeaders().put(key, value);
	}

}
