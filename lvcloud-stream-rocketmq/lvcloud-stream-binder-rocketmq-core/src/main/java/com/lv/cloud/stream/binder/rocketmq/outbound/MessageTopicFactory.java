package com.lv.cloud.stream.binder.rocketmq.outbound;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class MessageTopicFactory {
	
	private Logger logger = LoggerFactory.getLogger(MessageTopicFactory.class);
	
	private Map<String, Set<String>> msgTopicMap = Collections.synchronizedMap(new HashMap<String, Set<String>>());
	
	/**
	 * key=topic:tags
	 */
	private Map<String, Integer> delayMap = Collections.synchronizedMap(new HashMap<String, Integer>());

	public void register(String topic, String tags, int delay){
		logger.info("MessageTopicFactory.register:" + topic + ":" + tags + ":" + delay);
		Set<String> set = getTagsList(topic);
		if(set == null){
			set = new HashSet<String>();
			set.add(tags);
			msgTopicMap.put(topic, set);
		}else{
			set.add(tags);
		}
		delayMap.put(topic + ":" + tags, delay);
	}
	
	public void remove(String topic, String tags){
		Set<String> tagsSet  = msgTopicMap.get(topic);
		if(!CollectionUtils.isEmpty(tagsSet)){
			tagsSet.remove(tags);
			delayMap.remove(topic + ":" + tags);
		}
	}
	
	public void remove(String topic){
		if(msgTopicMap == null){
			return;
		}
		msgTopicMap.remove(topic);
	}
	
	public Set<String> getTagsList(String topic){
		if(msgTopicMap == null){
			return null;
		}
		return msgTopicMap.get(topic);
	}

	public Map<String, Integer> getDelayMap() {
		return delayMap;
	}

	public void setDelayMap(Map<String, Integer> delayMap) {
		this.delayMap = delayMap;
	}

}
