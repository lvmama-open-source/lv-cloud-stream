package com.lv.cloud.stream.binder.activemq.outbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.context.OrderlyShutdownCapable;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.endpoint.Pausable;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.support.GenericMessage;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @author xiaoyulin
 * @description 目前只支持Queue模式
 * @date 2019-06-25
 */
public class ActivemqMsgDrivenChannelAdapter extends MessageProducerSupport implements OrderlyShutdownCapable, Pausable {

    private Logger log = LoggerFactory.getLogger(ActivemqMsgDrivenChannelAdapter.class);

    private DefaultMessageListenerContainer messageListenerContainer;

    private IntegrationRecordMessageListener messageListener;

    private ConnectionFactory connectionFactory;

    private String group;

    public ActivemqMsgDrivenChannelAdapter(DefaultMessageListenerContainer messageListenerContainer){
        this.messageListenerContainer = messageListenerContainer;
        this.messageListener = new IntegrationRecordMessageListener();
    }

    @Override
    protected void onInit() {
        super.onInit();
        this.messageListenerContainer.setupMessageListener(messageListener);
        this.messageListenerContainer.afterPropertiesSet();
    }

    public ConnectionFactory getConnectionFactory() {
        return messageListenerContainer.getConnectionFactory();
    }

    protected void doStart() {
        messageListenerContainer.start();
    }

    protected void doStop() {
        messageListenerContainer.stop();
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

    public class IntegrationRecordMessageListener implements MessageListener {

        @Override
        public void onMessage(Message record) {
            try {
                // 转化消息
                org.springframework.messaging.Message<?> message = doConvertMessage(record);

                // 发送至channel
                if (message != null) {
                    sendMessage(message);
                }
            }catch (Exception e){

            }
            finally {

            }
        }

        private org.springframework.messaging.Message doConvertMessage(Message record) throws JMSException {
            Object payload = messageListenerContainer.getMessageConverter().fromMessage(record);
            org.springframework.messaging.Message message = new GenericMessage(payload);
            return message;
        }
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
