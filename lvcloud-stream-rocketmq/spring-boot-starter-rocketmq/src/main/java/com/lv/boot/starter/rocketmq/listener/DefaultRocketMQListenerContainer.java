package com.lv.boot.starter.rocketmq.listener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lv.boot.starter.rocketmq.enums.ConsumeMode;
import com.lv.boot.starter.rocketmq.enums.SelectorType;
import com.lv.boot.starter.rocketmq.listener.config.ContainerProperties;

/**
 * rocketmq 消息消费容器
 * @author xiaoyulin
 *
 */
public class DefaultRocketMQListenerContainer implements InitializingBean, RocketMQListenerContainer {
	private Logger log = LoggerFactory.getLogger(DefaultRocketMQListenerContainer.class);

    private long suspendCurrentQueueTimeMillis = 1000;

    /**
     * Message consume retry strategy<br> -1,no retry,put into 0,broker control retry frequency<br>
     * >0,client control retry frequency
     */
    private int delayLevelWhenNextConsume = 0;

    private ConsumeMode consumeMode = ConsumeMode.CONCURRENTLY;

    private SelectorType selectorType = SelectorType.TAG;

    private String selectorExpress = "*";

    private MessageModel messageModel = MessageModel.CLUSTERING;

    private String charset = "UTF-8";

    private ObjectMapper objectMapper = new ObjectMapper();

    private volatile boolean started = false;

    private RocketMQListener rocketMQListener;

    private DefaultMQPushConsumer consumer;

    private Class messageType;
    
    private ContainerProperties containerProperties;
    
    public static String hostAddress = "";
    static{
        // 服务器ip
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            hostAddress = "UnknownHost";
        }
    }
    
    public DefaultRocketMQListenerContainer(){}
    
    public DefaultRocketMQListenerContainer(ContainerProperties containerProperties){
    	this.setContainerProperties(containerProperties);
    }

    public long getSuspendCurrentQueueTimeMillis() {
		return suspendCurrentQueueTimeMillis;
	}

	public void setSuspendCurrentQueueTimeMillis(long suspendCurrentQueueTimeMillis) {
		this.suspendCurrentQueueTimeMillis = suspendCurrentQueueTimeMillis;
	}

	public int getDelayLevelWhenNextConsume() {
		return delayLevelWhenNextConsume;
	}

	public void setDelayLevelWhenNextConsume(int delayLevelWhenNextConsume) {
		this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
	}

	public ConsumeMode getConsumeMode() {
		return consumeMode;
	}

	public void setConsumeMode(ConsumeMode consumeMode) {
		this.consumeMode = consumeMode;
	}

	public SelectorType getSelectorType() {
		return selectorType;
	}

	public void setSelectorType(SelectorType selectorType) {
		this.selectorType = selectorType;
	}

	public String getSelectorExpress() {
		return selectorExpress;
	}

	public void setSelectorExpress(String selectorExpress) {
		this.selectorExpress = selectorExpress;
	}

	public MessageModel getMessageModel() {
		return messageModel;
	}

	public void setMessageModel(MessageModel messageModel) {
		this.messageModel = messageModel;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public DefaultMQPushConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(DefaultMQPushConsumer consumer) {
		this.consumer = consumer;
	}

	public void setRocketMQListener(RocketMQListener rocketMQListener) {
		this.rocketMQListener = rocketMQListener;
	}

	public void setMessageType(Class messageType) {
		this.messageType = messageType;
	}

	public void setupMessageListener(RocketMQListener rocketMQListener) {
        this.rocketMQListener = rocketMQListener;
    }

    @Override
    public void destroy() throws Exception {
        this.setStarted(false);
        if (Objects.nonNull(consumer)) {
            consumer.shutdown();
        }
        log.info("container destroyed, {}", this.toString());
    }

    public synchronized void start() throws MQClientException {

        if (this.isStarted()) {
            throw new IllegalStateException("container already started. " + this.toString());
        }

        initRocketMQPushConsumer();

        // parse message type
        this.messageType = getMessageType();
        log.debug("msgType: {}", messageType.getName());

        consumer.start();
        this.setStarted(true);
        log.info("started container: {}", this.toString());
    }

    public class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {

        @SuppressWarnings("unchecked")
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt messageExt : msgs) {
                log.debug("received msg: {}", messageExt);
                try {
                    long now = System.currentTimeMillis();
                    rocketMQListener.onMessage(messageExt);
                    long costTime = System.currentTimeMillis() - now;
                    log.info("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
                } catch (Exception e) {
                    log.warn("consume message failed. messageExt:{}", messageExt, e);
                    context.setDelayLevelWhenNextConsume(delayLevelWhenNextConsume);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    public class DefaultMessageListenerOrderly implements MessageListenerOrderly {

        @SuppressWarnings("unchecked")
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            for (MessageExt messageExt : msgs) {
                log.debug("received msg: {}", messageExt);
                try {
                    long now = System.currentTimeMillis();
                    rocketMQListener.onMessage(messageExt);
                    long costTime = System.currentTimeMillis() - now;
                    log.info("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
                } catch (Exception e) {
                    log.warn("consume message failed. messageExt:{}", messageExt, e);
                    context.setSuspendCurrentQueueTimeMillis(suspendCurrentQueueTimeMillis);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }

            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
//        start();
    }

    @Override
    public String toString() {
        return "DefaultRocketMQListenerContainer{" +
            ", consumeMode=" + consumeMode +
            ", selectorType=" + selectorType +
            ", selectorExpress='" + selectorExpress + '\'' +
            ", messageModel=" + messageModel +
            '}';
    }

    private Class getMessageType() {
        Type[] interfaces = rocketMQListener.getClass().getGenericInterfaces();
        if (Objects.nonNull(interfaces)) {
            for (Type type : interfaces) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    if (Objects.equals(parameterizedType.getRawType(), RocketMQListener.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                            return (Class) actualTypeArguments[0];
                        } else {
                            return Object.class;
                        }
                    }
                }
            }

            return Object.class;
        } else {
            return Object.class;
        }
    }

    private void initRocketMQPushConsumer() throws MQClientException {

        Assert.notNull(rocketMQListener, "Property 'rocketMQListener' is required");
        Assert.notNull(containerProperties.getConsumerGroup(), "Property 'consumerGroup' is required");
        Assert.notNull(containerProperties.getNamesrvAddr(), "Property 'nameServer' is required");
        if(StringUtils.isEmpty(containerProperties.getDestination()) && StringUtils.isEmpty(containerProperties.getTopic())){
        	throw new IllegalArgumentException("Property 'topic' or 'destination' is required");
        }

        consumer = new DefaultMQPushConsumer(containerProperties.getConsumerGroup());
        consumer.setNamesrvAddr(containerProperties.getNamesrvAddr());
        consumer.setInstanceName(containerProperties.getConsumerGroup() + "-" + hostAddress);
        consumer.setConsumeThreadMin(containerProperties.getConsumeThreadMin());
        consumer.setConsumeThreadMax(containerProperties.getConsumeThreadMax());
        if (consumer.getConsumeThreadMax() < consumer.getConsumeThreadMin()) {
            consumer.setConsumeThreadMin(containerProperties.getConsumeThreadMax());
        }

        consumer.setMessageModel(messageModel);
        
        String topic = containerProperties.getTopic();
        String subExpression = containerProperties.getSelectorExpress();
        
        if(StringUtils.isNotEmpty(containerProperties.getDestination())){
        	 String[] tempArr = containerProperties.getDestination().split(":", 2);
             topic = tempArr[0];
             subExpression = "";
             if (tempArr.length > 1) {
             	subExpression = tempArr[1];
             }
        }
       
        
        if(StringUtils.isEmpty(subExpression)){
        	subExpression = selectorExpress;
        }
        
        switch (selectorType) {
            case TAG:
                consumer.subscribe(topic, subExpression);
                break;
            case SQL92:
                consumer.subscribe(topic, MessageSelector.bySql(subExpression));
                break;
            default:
                throw new IllegalArgumentException("Property 'selectorType' was wrong.");
        }

        switch (consumeMode) {
            case ORDERLY:
                consumer.setMessageListener(new DefaultMessageListenerOrderly());
                break;
            case CONCURRENTLY:
                consumer.setMessageListener(new DefaultMessageListenerConcurrently());
                break;
            default:
                throw new IllegalArgumentException("Property 'consumeMode' was wrong.");
        }

        // provide an entryway to custom setting RocketMQ consumer
        if (rocketMQListener instanceof RocketMQPushConsumerLifecycleListener) {
            ((RocketMQPushConsumerLifecycleListener) rocketMQListener).prepareStart(consumer);
        }

    }

	public ContainerProperties getContainerProperties() {
		return containerProperties;
	}

	public void setContainerProperties(ContainerProperties containerProperties) {
		this.containerProperties = containerProperties;
	}

}
