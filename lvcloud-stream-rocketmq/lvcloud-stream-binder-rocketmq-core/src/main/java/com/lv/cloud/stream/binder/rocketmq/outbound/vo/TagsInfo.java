package com.lv.cloud.stream.binder.rocketmq.outbound.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.validation.annotation.Validated;

@JsonInclude(Include.NON_DEFAULT)
@Validated
public class TagsInfo {

	private String name;
	
	/**
	 * 延迟队列基本（1~18）
	 */
	private int delay;
	
	public TagsInfo(){}
		
	public TagsInfo(String name, int delay){
		this.name = name;
		this.setDelay(delay);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
