package com.lv.boot.starter.rocketmq.listener;

import org.springframework.beans.factory.DisposableBean;

/**
 * RocketMQListenerContainer接口
 */
public interface RocketMQListenerContainer extends DisposableBean {

    /**
     * Setup the message listener to use. Throws an {@link IllegalArgumentException} if that message listener type is
     * not supported.
     */
    void setupMessageListener(RocketMQListener<?> messageListener);
}
