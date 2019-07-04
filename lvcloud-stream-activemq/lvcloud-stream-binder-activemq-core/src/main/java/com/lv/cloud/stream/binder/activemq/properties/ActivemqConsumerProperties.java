
package com.lv.cloud.stream.binder.activemq.properties;

public class ActivemqConsumerProperties {

    public static final int CACHE_AUTO = 4;

    private String concurrency;

    private boolean sessionTransacted = false;

    private int cacheLevel = CACHE_AUTO;

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }

    public boolean isSessionTransacted() {
        return sessionTransacted;
    }

    public void setSessionTransacted(boolean sessionTransacted) {
        this.sessionTransacted = sessionTransacted;
    }

    public int getCacheLevel() {
        return cacheLevel;
    }

    public void setCacheLevel(int cacheLevel) {
        this.cacheLevel = cacheLevel;
    }
}
