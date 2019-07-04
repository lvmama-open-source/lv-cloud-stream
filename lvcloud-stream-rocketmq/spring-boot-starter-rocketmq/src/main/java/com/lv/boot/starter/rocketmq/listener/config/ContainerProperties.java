package com.lv.boot.starter.rocketmq.listener.config;

public class ContainerProperties {
	
	private String destination;
	
	private String topic; 
	
	private String selectorExpress; 
	
	 /**
     * Minimum consumer thread number
     */
    private int consumeThreadMin = 2;

    /**
     * Max consumer thread number
     */
    private int consumeThreadMax = 4;
    
    private String namesrvAddr;
	
	private String consumerGroup;
	
	private Object messageListener;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getSelectorExpress() {
		return selectorExpress;
	}

	public void setSelectorExpress(String selectorExpress) {
		this.selectorExpress = selectorExpress;
	}

	public int getConsumeThreadMin() {
		return consumeThreadMin;
	}

	public void setConsumeThreadMin(int consumeThreadMin) {
		this.consumeThreadMin = consumeThreadMin;
	}

	public int getConsumeThreadMax() {
		return consumeThreadMax;
	}

	public void setConsumeThreadMax(int consumeThreadMax) {
		this.consumeThreadMax = consumeThreadMax;
	}

	public String getNamesrvAddr() {
		return namesrvAddr;
	}

	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Object getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(Object messageListener) {
		this.messageListener = messageListener;
	}
    
    
}
