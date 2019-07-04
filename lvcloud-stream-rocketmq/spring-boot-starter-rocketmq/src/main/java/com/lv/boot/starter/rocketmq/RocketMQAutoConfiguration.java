package com.lv.boot.starter.rocketmq;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import com.lv.boot.starter.rocketmq.annotation.RocketMQMessageListener;
import com.lv.boot.starter.rocketmq.listener.DefaultRocketMQListenerContainer;
import com.lv.boot.starter.rocketmq.listener.config.ContainerProperties;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lv.boot.starter.rocketmq.core.RocketmqTemplate;
import com.lv.boot.starter.rocketmq.listener.RocketMQListener;


/**
 * RocketMqAutoConfiguration
 * @author xiaoyulin
 *
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@ConditionalOnClass(MQClientAPIImpl.class)
@Order
public class RocketMQAutoConfiguration {

    @Bean
    @ConditionalOnClass(DefaultMQProducer.class)
    @ConditionalOnMissingBean(DefaultMQProducer.class)
    @ConditionalOnProperty(prefix = "spring.rocketmq", value = {"nameServer", "producer.group"})
    public DefaultMQProducer mqProducer(RocketMQProperties rocketMQProperties) {

        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        String groupName = producerConfig.getGroup();
        Assert.hasText(groupName, "[spring.rocketmq.producer.group] must not be null");

        DefaultMQProducer producer = new DefaultMQProducer(producerConfig.getGroup());
        producer.setNamesrvAddr(rocketMQProperties.getNameServer());
        producer.setSendMsgTimeout(producerConfig.getSendMsgTimeout());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMsgBodyOverHowmuch());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryAnotherBrokerWhenNotStoreOk());

        return producer;
    }

    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnMissingBean(name = "rocketMQMessageObjectMapper")
    public ObjectMapper rocketMQMessageObjectMapper() {
        return new ObjectMapper();
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnBean(DefaultMQProducer.class)
    @ConditionalOnMissingBean(name = "rocketMQTemplate")
    public RocketmqTemplate rocketMQTemplate(DefaultMQProducer mqProducer,
        @Autowired(required = false)
        @Qualifier("rocketMQMessageObjectMapper")
            ObjectMapper objectMapper) {
    	RocketmqTemplate rocketMQTemplate = new RocketmqTemplate(mqProducer);
        if (Objects.nonNull(objectMapper)) {
            rocketMQTemplate.setObjectMapper(objectMapper);
        }

        return rocketMQTemplate;
    }

    @Configuration
    @ConditionalOnClass(DefaultMQPushConsumer.class)
    @EnableConfigurationProperties(RocketMQProperties.class)
    @ConditionalOnProperty(prefix = "spring.rocketmq", value = "nameServer")
    @Order
    public static class ListenerContainerConfiguration implements ApplicationContextAware, InitializingBean {
    	private final Logger log = LoggerFactory.getLogger(ListenerContainerConfiguration.class);
        private ConfigurableApplicationContext applicationContext;

        private AtomicLong counter = new AtomicLong(0);

        @Resource
        private StandardEnvironment environment;

        @Resource
        private RocketMQProperties rocketMQProperties;

        private ObjectMapper objectMapper;

        public ListenerContainerConfiguration() {
        }

        @Autowired(required = false)
        public ListenerContainerConfiguration(
            @Qualifier("rocketMQMessageObjectMapper") ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(RocketMQMessageListener.class);

            if (Objects.nonNull(beans)) {
                beans.forEach(this::registerContainer);
            }
        }
        
        private ContainerProperties createContainerProperties(RocketMQMessageListener annotation){
    		ContainerProperties containerProperties = new ContainerProperties();
    		containerProperties.setConsumerGroup(environment.resolvePlaceholders(annotation.consumerGroup()));
    		containerProperties.setNamesrvAddr(rocketMQProperties.getNameServer());
    		containerProperties.setConsumeThreadMin(annotation.consumeThreadMin());
    		containerProperties.setConsumeThreadMax(annotation.consumeThreadMax());
    		containerProperties.setTopic(environment.resolvePlaceholders(annotation.topic()));
    		containerProperties.setSelectorExpress(environment.resolvePlaceholders(annotation.selectorExpress()));
    		return containerProperties;
    	}

        private void registerContainer(String beanName, Object bean) {
            Class<?> clazz = AopUtils.getTargetClass(bean);

            if (!RocketMQListener.class.isAssignableFrom(bean.getClass())) {
                throw new IllegalStateException(clazz + " is not instance of " + RocketMQListener.class.getName());
            }

            RocketMQListener rocketMQListener = (RocketMQListener) bean;
            RocketMQMessageListener annotation = clazz.getAnnotation(RocketMQMessageListener.class);
            BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(DefaultRocketMQListenerContainer.class);
            ContainerProperties containerProperties = this.createContainerProperties(annotation);
            beanBuilder.addPropertyValue("consumeMode", annotation.consumeMode());
            beanBuilder.addPropertyValue("messageModel", annotation.messageModel());
            beanBuilder.addPropertyValue("selectorExpress", environment.resolvePlaceholders(annotation.selectorExpress()));
            beanBuilder.addPropertyValue("selectorType", annotation.selectorType());
            beanBuilder.addPropertyValue("rocketMQListener", rocketMQListener);
            beanBuilder.addPropertyValue("containerProperties", containerProperties);
            if (Objects.nonNull(objectMapper)) {
                beanBuilder.addPropertyValue("objectMapper", objectMapper);
            }
            beanBuilder.setDestroyMethodName("destroy");

            String containerBeanName = String.format("%s_%s", DefaultRocketMQListenerContainer.class.getName(), counter.incrementAndGet());
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
            beanFactory.registerBeanDefinition(containerBeanName, beanBuilder.getBeanDefinition());

            DefaultRocketMQListenerContainer container = beanFactory.getBean(containerBeanName, DefaultRocketMQListenerContainer.class);

            if (!container.isStarted()) {
                try {
                    container.start();
                } catch (Exception e) {
                    log.error("started container failed. {}", container, e);
                    throw new RuntimeException(e);
                }
            }

            log.info("register rocketMQ listener to container, listenerBeanName:{}, containerBeanName:{}", beanName, containerBeanName);
        }
    }
}
