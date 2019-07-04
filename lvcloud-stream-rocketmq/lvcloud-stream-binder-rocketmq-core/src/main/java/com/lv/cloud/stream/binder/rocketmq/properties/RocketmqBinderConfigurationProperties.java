package com.lv.cloud.stream.binder.rocketmq.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lvcloud.stream.rocketmq.binder")
public class RocketmqBinderConfigurationProperties {
	
	private String[] headers = new String[] {};
	
	private String namesrvAddr;
	
	private String producerGroup;
	
	private Map<String, String> configuration = new HashMap<>();
	
	private boolean autoCreateTopics = true;
	
	private Map<String, TopicBindingProperties> topics = new TreeMap<>(
			String.CASE_INSENSITIVE_ORDER);
	
	public Map<String, Object> getProducerConfiguration() {
		Map<String, Object> producerConfiguration = new HashMap<>();
		producerConfiguration.put("namesrvAddr", namesrvAddr);
		producerConfiguration.put("producerGroup", producerGroup);
		
		return producerConfiguration;
	}

	public String getNamesrvAddr() {
		return namesrvAddr;
	}

	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	public String getProducerGroup() {
		return producerGroup;
	}

	public void setProducerGroup(String producerGroup) {
		this.producerGroup = producerGroup;
	}

	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public Map<String, String> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

	public boolean isAutoCreateTopics() {
		return autoCreateTopics;
	}

	public void setAutoCreateTopics(boolean autoCreateTopics) {
		this.autoCreateTopics = autoCreateTopics;
	}

	public Map<String, TopicBindingProperties> getTopics() {
		return topics;
	}

	public void setTopics(Map<String, TopicBindingProperties> topics) {
		this.topics = topics;
	}
}
