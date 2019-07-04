package com.lv.cloud.stream.binder.rocketmq.provisioning;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.lv.cloud.stream.binder.rocketmq.properties.RocketmqBinderConfigurationProperties;
import com.lv.cloud.stream.binder.rocketmq.properties.RocketmqProducerProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.util.CollectionUtils;

import com.lv.cloud.stream.binder.rocketmq.outbound.MessageTopicFactory;
import com.lv.cloud.stream.binder.rocketmq.outbound.vo.TagsInfo;
import com.lv.cloud.stream.binder.rocketmq.properties.RocketmqConsumerProperties;
import com.lv.cloud.stream.binder.rocketmq.properties.TopicBindingProperties;
import com.lv.cloud.stream.binder.rocketmq.utils.RocketmqTopicUtils;

public class RocketmqTopicProvisioner implements ProvisioningProvider<ExtendedConsumerProperties<RocketmqConsumerProperties>,
	ExtendedProducerProperties<RocketmqProducerProperties>>, InitializingBean {
	
	private Logger logger = LoggerFactory.getLogger(RocketmqTopicProvisioner.class);
	
	private static final String COMMA = ",";
	
	private final RocketmqBinderConfigurationProperties configurationProperties;
	
	private MessageTopicFactory messageTopicFactory;
	
	public RocketmqTopicProvisioner(RocketmqBinderConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
		//TODO:绑定管理控制台
	}

	@Override
	public ProducerDestination provisionProducerDestination(String name,
			ExtendedProducerProperties<RocketmqProducerProperties> properties) throws ProvisioningException {
		RocketmqTopicUtils.validateTopicName(name);
		
		
		return new RocketmqProducerDestination(name);
	}

	@Override
	public ConsumerDestination provisionConsumerDestination(String name, String group,
			ExtendedConsumerProperties<RocketmqConsumerProperties> properties) throws ProvisioningException {
		RocketmqTopicUtils.validateTopicName(name);
		ConsumerDestination consumerDestination = new RocketmqConsumerDestination(name);
		
		if(this.configurationProperties.isAutoCreateTopics()){
			//TODO:创建主题
			
		}
		
		return consumerDestination;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			this.initMessageTopicFactory();
		} catch (Exception e) {
			logger.error("initMessageTopicFactory exception:{}", e);
		}
	}
	
	public MessageTopicFactory getMessageTopicFactory() {
		return messageTopicFactory;
	}

	public void setMessageTopicFactory(MessageTopicFactory messageTopicFactory) {
		this.messageTopicFactory = messageTopicFactory;
	}
	
	private void initMessageTopicFactory(){
		messageTopicFactory = new MessageTopicFactory();
		Map<String, TopicBindingProperties>  topicMap = configurationProperties.getTopics();
		if(topicMap != null){
			Iterator<Entry<String, TopicBindingProperties>> it = topicMap.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, TopicBindingProperties> entry = it.next();
				String topic = entry.getKey();
				TopicBindingProperties value = entry.getValue();
				messageTopicFactory.getDelayMap().put(topic, getDefaultDelay(value));
				messageTopicFactory.getDelayMap().put(topic + ":*", getDefaultDelay(value));
				
				String tagsArr = value.getTags();
				if(!StringUtils.isEmpty(tagsArr)){
					String[] tempArr = tagsArr.split(COMMA);
					if(tempArr.length > 0){
						int delay = getDefaultDelay(value);
						Map<String, TagsInfo> tagsInfoMap = value.getTagsInfo();
						for(String tags : tempArr){
							if(!CollectionUtils.isEmpty(tagsInfoMap) && null != tagsInfoMap.get(tags)){
								delay = tagsInfoMap.get(tags).getDelay();
							}else{
								delay = getDefaultDelay(value);
							}
							messageTopicFactory.register(topic, tags, delay);
						}
					}
				}
			}
		}
	}
	
	private int getDefaultDelay(TopicBindingProperties topicBindingProperties){
		int delay = 0;
		if(topicBindingProperties.getDelay() > 0){
			delay = topicBindingProperties.getDelay();
		}
		return delay;
	}
	
	private static final class RocketmqProducerDestination implements ProducerDestination {

		private final String producerDestinationName;


		RocketmqProducerDestination(String destinationName) {
			this.producerDestinationName = destinationName;
		}

		@Override
		public String getName() {
			return producerDestinationName;
		}

		@Override
		public String getNameForPartition(int partition) {
			return producerDestinationName;
		}

		@Override
		public String toString() {
			return "KafkaProducerDestination{" +
					"producerDestinationName='" + producerDestinationName + '\'' +
					'}';
		}
	}
	
	private static final class RocketmqConsumerDestination implements ConsumerDestination {

		private final String consumerDestinationName;
		
		RocketmqConsumerDestination(String consumerDestinationName) {
			this(consumerDestinationName, 0, null);
		}

		RocketmqConsumerDestination(String consumerDestinationName, int partitions) {
			this(consumerDestinationName, partitions, null);
		}

		RocketmqConsumerDestination(String consumerDestinationName, Integer partitions, String dlqName) {
			this.consumerDestinationName = consumerDestinationName;
		}

		@Override
		public String getName() {
			return this.consumerDestinationName;
		}

		@Override
		public String toString() {
			return "KafkaConsumerDestination{" +
					"consumerDestinationName='" + consumerDestinationName + '\'' + '}';
		}
	}
}
