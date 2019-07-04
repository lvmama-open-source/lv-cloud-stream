package com.lv.cloud.stream.binder.rocketmq.outbound;

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.context.OrderlyShutdownCapable;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.endpoint.Pausable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.support.RetryTemplate;

import com.lv.boot.starter.rocketmq.listener.DefaultRocketMQListenerContainer;
import com.lv.boot.starter.rocketmq.listener.RocketMQListener;

public class RocketmqMsgDrivenChannelAdapter extends MessageProducerSupport implements OrderlyShutdownCapable, Pausable {
	
	private Logger log = LoggerFactory.getLogger(RocketmqMsgDrivenChannelAdapter.class);
	
	private final IntegrationRecordMessageListener recordListener = new IntegrationRecordMessageListener();
	
	private final DefaultRocketMQListenerContainer messageListenerContainer;
	
	private RecoveryCallback<? extends Object> recoveryCallback;
	
	public RocketmqMsgDrivenChannelAdapter(DefaultRocketMQListenerContainer messageListenerContainer){
		this.messageListenerContainer = messageListenerContainer;
	}
	
	@Override
	protected void onInit() {
		super.onInit();
		this.messageListenerContainer.setupMessageListener(recordListener);
		this.messageListenerContainer.getContainerProperties().setMessageListener(recordListener);
	}
	
	/**
	 * A {@link RecoveryCallback} instance for retry operation;
	 * if null, the exception will be thrown to the container after retries are exhausted
	 * (unless an error channel is configured).
	 * Does not make sense if {@link #setRetryTemplate(RetryTemplate)} isn't specified.
	 * @param recoveryCallback the recovery callback.
	 * @since 2.0.1
	 */
	public void setRecoveryCallback(RecoveryCallback<? extends Object> recoveryCallback) {
		this.recoveryCallback = recoveryCallback;
	}

	@Override
	protected void doStart() {
		try {
			this.messageListenerContainer.start();
		} catch (MQClientException e) {
			log.error("messageListenerContainer start error,{}", e);
		}
	}

	@Override
	protected void doStop() {
		try {
			this.messageListenerContainer.destroy();
		} catch (Exception e) {
			log.error("messageListenerContainer stop error,{}", e);
		}
	}

	@Override
	public void pause() {
		// do nothing
		log.info("messageListenerContainer pause do nothing");
	}

	@Override
	public void resume() {
		// do nothing
		log.info("messageListenerContainer resume do nothing");
	}

	@Override
	public int beforeShutdown() {
		this.doStop();
		return getPhase();
	}

	@Override
	public int afterShutdown() {
		return getPhase();
	}
	
	private class IntegrationRecordMessageListener implements RocketMQListener<MessageExt>{
    	
    	public IntegrationRecordMessageListener(){ 		
    	}

		@Override
		public void onMessage(MessageExt record) {
			// 转化消息
			Message<?> message = doConvertMessage(record);
			
			// 发送至channel
			if (message != null) {
				try {
					sendMessage(message);
				}
				finally {
					
				}
			}
			
		}
		
		 private Message doConvertMessage(MessageExt messageExt) {
			 byte[] payload = messageExt.getBody();
			 Map<String, Object> headersMap = new HashMap<>();
			 headersMap.put("topic", messageExt.getTopic());
			 headersMap.put("tags", messageExt.getTags());
			 headersMap.put("keys", messageExt.getKeys());
			 MessageHeaders headers = new MessageHeaders(headersMap);
//			 headers.put(MessageConst.PROPERTY_KEYS, messageExt.getKeys());
			 Message message = new GenericMessage(payload, headers);
			 return message;
		 }
    }
	
	@Override
	public String getComponentType() {
		return "rocketmq:message-driven-channel-adapter";
	}

}
