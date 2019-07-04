package com.lv.cloud.stream.binder.activemq.config;

import com.lv.cloud.stream.binder.activemq.ActivemqMessageChannelBinder;
import com.lv.cloud.stream.binder.activemq.properties.ActivemqBinderConfigurationProperties;
import com.lv.cloud.stream.binder.activemq.properties.ActivemqExtendedBindingProperties;
import com.lv.cloud.stream.binder.activemq.provisioning.ActivemqTopicProvisioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xiaoyulin
 * @description Activemq Binder Configuration
 * @date 2019-06-26
 */
@Configuration
@ConditionalOnMissingBean(Binder.class)
@Import({ JmsAutoConfiguration.class })
@EnableConfigurationProperties({ActivemqExtendedBindingProperties.class, ActivemqBinderConfigurationProperties.class })
public class ActivemqBinderConfiguration {

    @Autowired
    private JmsProperties jmsProperties;

    @Autowired
    private ActivemqExtendedBindingProperties activemqExtendedBindingProperties;

    @Bean
    ActivemqBinderConfigurationProperties configurationProperties(JmsProperties jmsProperties) {
        return new ActivemqBinderConfigurationProperties(jmsProperties);
    }

    @Bean
    ActivemqTopicProvisioner provisioningProvider(ActivemqBinderConfigurationProperties configurationProperties) {
        return new ActivemqTopicProvisioner(configurationProperties, this.jmsProperties);
    }

    @Bean
    ActivemqMessageChannelBinder activemqMessageChannelBinder(ActivemqBinderConfigurationProperties configurationProperties, ActivemqTopicProvisioner provisioningProvider){
        ActivemqMessageChannelBinder activemqMessageChannelBinder = new ActivemqMessageChannelBinder(configurationProperties, provisioningProvider);
        activemqMessageChannelBinder.setExtendedBindingProperties(activemqExtendedBindingProperties);

        return activemqMessageChannelBinder;
    }
}
