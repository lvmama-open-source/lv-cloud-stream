package com.lv.boot.starter.rocketmq.listener;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

/**
 * RocketMQ PushConsumer Lifecycle Listener.
 */
public interface RocketMQPushConsumerLifecycleListener extends RocketMQConsumerLifecycleListener<DefaultMQPushConsumer> {
}
