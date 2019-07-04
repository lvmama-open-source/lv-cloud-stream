package com.lv.cloud.stream.binder.activemq;

import com.lv.cloud.stream.binder.activemq.outbound.ActiveMQConnectionFactoryFactory;
import com.lv.cloud.stream.binder.activemq.outbound.ActivemqMsgDrivenChannelAdapter;
import com.lv.cloud.stream.binder.activemq.outbound.ActivemqProducerMessageHandler;
import com.lv.cloud.stream.binder.activemq.properties.ActivemqBinderConfigurationProperties;
import com.lv.cloud.stream.binder.activemq.properties.ActivemqConsumerProperties;
import com.lv.cloud.stream.binder.activemq.properties.ActivemqExtendedBindingProperties;
import com.lv.cloud.stream.binder.activemq.properties.ActivemqProducerProperties;
import com.lv.cloud.stream.binder.activemq.provisioning.ActivemqTopicProvisioner;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.cloud.stream.binder.*;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.context.Lifecycle;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.channel.ChannelInterceptorAware;
import org.springframework.integration.core.MessageProducer;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.jms.ConnectionFactory;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiaoyulin
 * @description Activemq Binder
 * @date 2019-06-21
 */
public class ActivemqMessageChannelBinder extends AbstractMessageChannelBinder<ExtendedConsumerProperties<ActivemqConsumerProperties>,
        ExtendedProducerProperties<ActivemqProducerProperties>, ActivemqTopicProvisioner>
        implements ExtendedPropertiesBinder<MessageChannel, ActivemqConsumerProperties, ActivemqProducerProperties> {

    private final ActivemqBinderConfigurationProperties configurationProperties;

    private ActivemqExtendedBindingProperties extendedBindingProperties = new ActivemqExtendedBindingProperties();

    private static final ThreadLocal<String> bindingNameHolder = new ThreadLocal<>();

    public ActivemqMessageChannelBinder(ActivemqBinderConfigurationProperties configurationProperties, ActivemqTopicProvisioner provisioningProvider) {
        super(headersToMap(configurationProperties), provisioningProvider);
        this.configurationProperties = configurationProperties;
    }

    private static String[] headersToMap(ActivemqBinderConfigurationProperties configurationProperties) {
        String[] headersToMap;
        if (ObjectUtils.isEmpty(configurationProperties.getHeaders())) {
            headersToMap = BinderHeaders.STANDARD_HEADERS;
        }
        else {
            String[] combinedHeadersToMap = Arrays.copyOfRange(BinderHeaders.STANDARD_HEADERS, 0,
                    BinderHeaders.STANDARD_HEADERS.length + configurationProperties.getHeaders().length);
            System.arraycopy(configurationProperties.getHeaders(), 0, combinedHeadersToMap,
                    BinderHeaders.STANDARD_HEADERS.length,
                    configurationProperties.getHeaders().length);
            headersToMap = combinedHeadersToMap;
        }
        return headersToMap;
    }

    private ConnectionFactory creatConnectionFactory() {
        JmsProperties.Cache cacheProperties = this.configurationProperties.getCache();
        if(cacheProperties != null && cacheProperties.isEnabled()){
            return newCachingJmsConnectionFactory(cacheProperties);
        }
        return newJmsConnectionFactory();
    }

    private ConnectionFactory newCachingJmsConnectionFactory(JmsProperties.Cache cacheProperties){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                newJmsConnectionFactory());
        connectionFactory.setCacheConsumers(cacheProperties.isConsumers());
        connectionFactory.setCacheProducers(cacheProperties.isProducers());
        connectionFactory.setSessionCacheSize(cacheProperties.getSessionCacheSize());
        return connectionFactory;
    }

    private ConnectionFactory newJmsConnectionFactory(){
        ActiveMQProperties properties = new ActiveMQProperties();
        properties.setBrokerUrl(this.configurationProperties.getBroker());
        properties.setUser(this.configurationProperties.getUser());
        properties.setPassword(this.configurationProperties.getPassword());

        return new ActiveMQConnectionFactoryFactory(properties,
                null)
                .createConnectionFactory(ActiveMQConnectionFactory.class);
    }

    @Override
    protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ExtendedProducerProperties<ActivemqProducerProperties> extendedProducerProperties, MessageChannel errorChannel) throws Exception {
        throw new IllegalStateException("The abstract binder should not call this method");
    }

    @Override
    protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ExtendedProducerProperties<ActivemqProducerProperties> extendedProducerProperties,
                                                          MessageChannel channel, MessageChannel errorChannel) throws Exception {
        long deliveryDelay = -1;
        if(extendedProducerProperties != null && extendedProducerProperties.getExtension() != null){
            deliveryDelay = extendedProducerProperties.getExtension().getDeliveryDelay();
        }

//        List<ChannelInterceptor> interceptors = ((ChannelInterceptorAware) channel).getChannelInterceptors();

        ConnectionFactory connectionFactory = creatConnectionFactory();
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(new SimpleMessageConverter());
        jmsTemplate.setDeliveryDelay(deliveryDelay);
        ProducerConfigurationMessageHandler handler = new ProducerConfigurationMessageHandler(jmsTemplate, destination.getName());
        if (errorChannel != null) {
            handler.setSendFailureChannel(errorChannel);
        }
        return handler;
    }

    @Override
    protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
                                                     ExtendedConsumerProperties<ActivemqConsumerProperties> extendedConsumerProperties) throws Exception {
        ActivemqConsumerProperties consumerProperties = extendedConsumerProperties.getExtension();

        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(creatConnectionFactory());
        messageListenerContainer.setDestinationName(destination.getName());
        if(!StringUtils.isEmpty(consumerProperties.getConcurrency())) {
            messageListenerContainer.setConcurrency(consumerProperties.getConcurrency());
        }
        messageListenerContainer.setSessionTransacted(consumerProperties.isSessionTransacted());
        messageListenerContainer.setCacheLevel(consumerProperties.getCacheLevel());
        messageListenerContainer.setMessageConverter(new SimpleMessageConverter());

        ActivemqMsgDrivenChannelAdapter activemqMsgDrivenChannelAdapter = new ActivemqMsgDrivenChannelAdapter(messageListenerContainer);
        // TODO：RecoveryCallback

        return activemqMsgDrivenChannelAdapter;
    }

    @Override
    public ActivemqConsumerProperties getExtendedConsumerProperties(String channelName) {
        bindingNameHolder.set(channelName);
        return this.extendedBindingProperties.getExtendedConsumerProperties(channelName);
    }

    @Override
    public ActivemqProducerProperties getExtendedProducerProperties(String channelName) {
        bindingNameHolder.set(channelName);
        return this.extendedBindingProperties.getExtendedProducerProperties(channelName);
    }

    public ActivemqExtendedBindingProperties getExtendedBindingProperties() {
        return extendedBindingProperties;
    }

    public void setExtendedBindingProperties(ActivemqExtendedBindingProperties extendedBindingProperties) {
        this.extendedBindingProperties = extendedBindingProperties;
    }

    @Override
    public String getDefaultsPrefix() {
        return this.extendedBindingProperties.getDefaultsPrefix();
    }

    @Override
    public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
        return this.extendedBindingProperties.getExtendedPropertiesEntryClass();
    }

    private final class ProducerConfigurationMessageHandler extends ActivemqProducerMessageHandler
            implements Lifecycle {

        private boolean running = true;

        public ProducerConfigurationMessageHandler(JmsTemplate jmsTemplate, String destination) {
            super(jmsTemplate);
            setBeanFactory(ActivemqMessageChannelBinder.this.getBeanFactory());
            if(!StringUtils.isEmpty(destination)) {
                setDestinationExpression(new LiteralExpression(destination));
            }
        }

        @Override
        public void start() {
            try {
                super.onInit();
            }
            catch (Exception ex) {
                this.logger.error("Initialization errors: ", ex);
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void stop() {
            this.running = false;
        }

        @Override
        public boolean isRunning() {
            return running;
        }
    }
}
