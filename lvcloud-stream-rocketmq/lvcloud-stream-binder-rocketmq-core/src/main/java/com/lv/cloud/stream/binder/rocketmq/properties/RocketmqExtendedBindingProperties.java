package com.lv.cloud.stream.binder.rocketmq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.AbstractExtendedBindingProperties;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

@ConfigurationProperties("lvcloud.stream.rocketmq")
public class RocketmqExtendedBindingProperties extends AbstractExtendedBindingProperties<RocketmqConsumerProperties, RocketmqProducerProperties, RocketmqBindingProperties>{

    private static final String DEFAULTS_PREFIX = "lvcloud.stream.rocketmq.default";

    public RocketmqExtendedBindingProperties() {
    }

    public String getDefaultsPrefix() {
        return "lvcloud.stream.rocketmq.default";
    }

    @Override
    public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
        return RocketmqBindingProperties.class;

    }
}
