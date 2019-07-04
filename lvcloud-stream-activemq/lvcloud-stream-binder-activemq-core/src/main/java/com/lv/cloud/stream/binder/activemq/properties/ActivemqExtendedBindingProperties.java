package com.lv.cloud.stream.binder.activemq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.AbstractExtendedBindingProperties;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

@ConfigurationProperties("lvcloud.stream.activemq")
public class ActivemqExtendedBindingProperties
	extends AbstractExtendedBindingProperties<ActivemqConsumerProperties, ActivemqProducerProperties, ActivemqBindingProperties> {

	private static final String DEFAULTS_PREFIX = "lvcloud.stream.activemq.default";

	public ActivemqExtendedBindingProperties() {
	}

	public String getDefaultsPrefix() {
		return "lvcloud.stream.activemq.default";
	}

	@Override
	public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
		return ActivemqBindingProperties.class;

	}
}
