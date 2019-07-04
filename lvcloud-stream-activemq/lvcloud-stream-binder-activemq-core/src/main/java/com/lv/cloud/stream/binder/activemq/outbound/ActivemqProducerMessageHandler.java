package com.lv.cloud.stream.binder.activemq.outbound;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

import java.util.concurrent.Future;

public class ActivemqProducerMessageHandler extends AbstractReplyProducingMessageHandler {

    private final JmsTemplate jmsTemplate;

    private Expression destinationExpression;

    private EvaluationContext evaluationContext;

    private Expression topicExpression;

    private MessageChannel sendFailureChannel;

    public ActivemqProducerMessageHandler(JmsTemplate jmsTemplate){
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    protected Object handleRequestMessage(Message<?> message) {
        MessageHeaders messageHeaders = message.getHeaders();
        String destination = this.destinationExpression != null ?
                this.destinationExpression.getValue(this.evaluationContext, message, String.class)
                : messageHeaders.get("destination", String.class);

        if(destination == null){
            throw new IllegalStateException("ActiveMQ message destination is null.");
        }
        jmsTemplate.convertAndSend(destination, message.getPayload());

        return processReplyFuture();
    }

    private Future<?> processReplyFuture(){
        // TODO:?
        return null;
    }

    protected void doInit() {
        this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(this.getBeanFactory());
    }


    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public Expression getTopicExpression() {
        return topicExpression;
    }

    public void setTopicExpression(Expression topicExpression) {
        this.topicExpression = topicExpression;
    }

    public Expression getDestinationExpression() {
        return destinationExpression;
    }

    public void setDestinationExpression(Expression destinationExpression) {
        this.destinationExpression = destinationExpression;
    }

    public MessageChannel getSendFailureChannel() {
        return sendFailureChannel;
    }

    public void setSendFailureChannel(MessageChannel sendFailureChannel) {
        this.sendFailureChannel = sendFailureChannel;
    }
}
