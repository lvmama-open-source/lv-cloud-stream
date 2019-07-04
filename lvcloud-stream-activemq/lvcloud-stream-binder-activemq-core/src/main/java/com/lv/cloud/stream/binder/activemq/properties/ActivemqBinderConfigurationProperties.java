package com.lv.cloud.stream.binder.activemq.properties;

import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.autoconfigure.jms.JmsProperties.Cache;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "lvcloud.stream.activemq.binder")
public class ActivemqBinderConfigurationProperties {

    private String[] headers = new String[] {};

    private String broker;

    private Cache cache;

    /**
     * Login user of the broker.
     */
    private String user;

    /**
     * Login password of the broker.
     */
    private String password;

    private final JmsProperties jmsProperties;

    public ActivemqBinderConfigurationProperties(JmsProperties jmsProperties){
        Assert.notNull(jmsProperties, "'JmsProperties' cannot be null");
        this.jmsProperties = jmsProperties;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JmsProperties getJmsProperties() {
        return jmsProperties;
    }
}
