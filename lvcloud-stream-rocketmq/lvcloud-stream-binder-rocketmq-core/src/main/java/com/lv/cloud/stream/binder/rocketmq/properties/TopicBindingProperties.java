package com.lv.cloud.stream.binder.rocketmq.properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lv.cloud.stream.binder.rocketmq.outbound.vo.TagsInfo;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.TreeMap;

/**
 * topic配置绑定
 * @author xiaoyulin
 *
 */
@JsonInclude(Include.NON_DEFAULT)
@Validated
public class TopicBindingProperties {
	
	private Map<String, TagsInfo> tagsInfo = new TreeMap<>(
			String.CASE_INSENSITIVE_ORDER);
	
	/**
	 * 多个用逗号','分隔
	 */
	private String tags;
	
	/**
	 * 延迟队列基本（1~18）
	 */
	private int delay;
	
	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Map<String, TagsInfo> getTagsInfo() {
		return tagsInfo;
	}

	public void setTagsInfo(Map<String, TagsInfo> tagsInfo) {
		this.tagsInfo = tagsInfo;
	}

}
