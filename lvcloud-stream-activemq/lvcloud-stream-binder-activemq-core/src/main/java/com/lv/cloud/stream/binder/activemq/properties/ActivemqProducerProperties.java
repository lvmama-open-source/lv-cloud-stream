package com.lv.cloud.stream.binder.activemq.properties;

public class ActivemqProducerProperties {

    private long deliveryDelay = -1;

    public long getDeliveryDelay() {
        return deliveryDelay;
    }

    public void setDeliveryDelay(long deliveryDelay) {
        this.deliveryDelay = deliveryDelay;
    }
}
