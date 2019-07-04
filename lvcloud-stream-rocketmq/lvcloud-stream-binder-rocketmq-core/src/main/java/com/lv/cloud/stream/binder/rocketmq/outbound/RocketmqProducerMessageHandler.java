package com.lv.cloud.stream.binder.rocketmq.outbound;

import java.util.Set;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.util.CollectionUtils;

import com.lv.boot.starter.rocketmq.core.RocketmqTemplate;

public class RocketmqProducerMessageHandler extends AbstractReplyProducingMessageHandler {
	private Logger log = LoggerFactory.getLogger(RocketmqProducerMessageHandler.class);
	
	private final RocketmqTemplate rocketmqTemplate;
	
	private boolean sync;
	
	private Expression destinationExpression;
	
	private EvaluationContext evaluationContext;
	
	protected MessageTopicFactory messageTopicFactory;

	private MessageChannel sendFailureChannel;
	
	public RocketmqProducerMessageHandler(RocketmqTemplate rocketmqTemplate){
		this.rocketmqTemplate = rocketmqTemplate;
	}
	
	public RocketmqProducerMessageHandler(RocketmqTemplate rocketmqTemplate, MessageTopicFactory messageTopicFactory){
		this.rocketmqTemplate = rocketmqTemplate;
		this.messageTopicFactory = messageTopicFactory;
	}


	@Override
	protected Object handleRequestMessage(Message<?> message) {
		MessageHeaders messageHeaders = message.getHeaders();
		String destination = this.destinationExpression != null ?
				this.destinationExpression.getValue(this.evaluationContext, message, String.class)
				: messageHeaders.get("destination", String.class);
				
		if(destination == null){
			throw new IllegalStateException("RocketMQ message destination is null.");
		}
		
		String[] topicTags = rocketmqTemplate.convertToToicAndTags(destination);
    	if(topicTags.length < 2){
    		throw new IllegalArgumentException("‘tags’ cannot be null");
    	}
    	
    	Integer delayTimeLevel = 0;	
    	if("*".equals(topicTags[1]) && null != messageTopicFactory){
    		Set<String> tagsSet = messageTopicFactory.getTagsList(topicTags[0]);
    		if(!CollectionUtils.isEmpty(tagsSet)){
    			for(String tags : tagsSet){
    				delayTimeLevel = messageTopicFactory.getDelayMap().get(topicTags[0] + ":" + tags);
    				if (delayTimeLevel == null){
    					delayTimeLevel = 0;
    				}
    				this.sendMsg(topicTags[0], tags, message, delayTimeLevel);
    				
    			}
    		}else{
    			delayTimeLevel = messageTopicFactory.getDelayMap().get(destination);
    			if (delayTimeLevel == null){
    				delayTimeLevel = 0;
    			}
    			this.sendMsg(destination, message, delayTimeLevel);
    		}
    	}else{
			delayTimeLevel = messageTopicFactory.getDelayMap().get(destination);
			if (delayTimeLevel == null){
				delayTimeLevel = 0;
			}
			this.sendMsg(destination, message, delayTimeLevel);
    	}
		
		return null;
	}
	
	private void sendMsg(String topic, String tags, Message<?> message, int delayTimeLevel){
		org.apache.rocketmq.common.message.Message rocketMsg = rocketmqTemplate.convertToRocketMsg(topic, tags, message);
		if(delayTimeLevel  > 0){
        	rocketMsg.setDelayTimeLevel(delayTimeLevel);
		}
		this.sendMsg(rocketMsg);
	}
	
	private void sendMsg(String destination, Message<?> message, int delayTimeLevel){
		org.apache.rocketmq.common.message.Message rocketMsg = rocketmqTemplate.convertToRocketMsg(destination, message);
		if(delayTimeLevel  > 0){
        	rocketMsg.setDelayTimeLevel(delayTimeLevel);
		}
		this.sendMsg(rocketMsg);
	}
	
	private void sendMsg(org.apache.rocketmq.common.message.Message rocketMsg) {
		if(sync){
			SendResult sendResult = rocketmqTemplate.syncSend(rocketMsg);
			if(SendStatus.SEND_OK.equals(sendResult.getSendStatus())){
				log.info("RocketmqProducerMessageHandler handle success.");
			}else{
				throw new MessagingException("RocketMQ message send error.");
			}
		}else{
			rocketmqTemplate.sendOneWay(rocketMsg);
		}
	}
	
	private void sendMsg(String destination, Message<?> message) {
		if(sync){
			SendResult sendResult = rocketmqTemplate.syncSend(destination, message);
			if(SendStatus.SEND_OK.equals(sendResult.getSendStatus())){
				log.info("RocketmqProducerMessageHandler handle success.");
			}else{
				throw new MessagingException("RocketMQ message send error.");
			}
		}else{
			rocketmqTemplate.sendOneWay(destination, message);
		}
	}
	
	@Override
	protected void doInit() {
		this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(getBeanFactory());
	}

	public Expression getDestinationExpression() {
		return destinationExpression;
	}

	public void setDestinationExpression(Expression destinationExpression) {
		this.destinationExpression = destinationExpression;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public MessageChannel getSendFailureChannel() {
		return sendFailureChannel;
	}

	public void setSendFailureChannel(MessageChannel sendFailureChannel) {
		this.sendFailureChannel = sendFailureChannel;
	}

}
