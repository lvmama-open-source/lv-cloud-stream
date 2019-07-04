package com.lv.cloud.stream.binder.activemq.outbound;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties.Packages;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

public class ActiveMQConnectionFactoryFactory {

    private static final String DEFAULT_EMBEDDED_BROKER_URL = "vm://localhost?broker.persistent=false";

    private static final String DEFAULT_NETWORK_BROKER_URL = "tcp://localhost:61616";

    private final ActiveMQProperties properties;

    private final List<ActiveMQConnectionFactoryCustomizer> factoryCustomizers;

    public ActiveMQConnectionFactoryFactory(ActiveMQProperties properties,
                                            List<ActiveMQConnectionFactoryCustomizer> factoryCustomizers) {
        Assert.notNull(properties, "Properties must not be null");
        this.properties = properties;
        this.factoryCustomizers = (factoryCustomizers != null) ? factoryCustomizers
                : Collections.emptyList();
    }

    public <T extends ActiveMQConnectionFactory> T createConnectionFactory(
            Class<T> factoryClass) {
        try {
            return doCreateConnectionFactory(factoryClass);
        }
        catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to create " + "ActiveMQConnectionFactory", ex);
        }
    }

    private <T extends ActiveMQConnectionFactory> T doCreateConnectionFactory(
            Class<T> factoryClass) throws Exception {
        T factory = createConnectionFactoryInstance(factoryClass);
        if (this.properties.getCloseTimeout() != null) {
            factory.setCloseTimeout((int) this.properties.getCloseTimeout().toMillis());
        }
        factory.setNonBlockingRedelivery(this.properties.isNonBlockingRedelivery());
        if (this.properties.getSendTimeout() != null) {
            factory.setSendTimeout((int) this.properties.getSendTimeout().toMillis());
        }
        Packages packages = this.properties.getPackages();
        if (packages.getTrustAll() != null) {
            factory.setTrustAllPackages(packages.getTrustAll());
        }
        if (!packages.getTrusted().isEmpty()) {
            factory.setTrustedPackages(packages.getTrusted());
        }
        customize(factory);
        return factory;
    }

    private <T extends ActiveMQConnectionFactory> T createConnectionFactoryInstance(
            Class<T> factoryClass) throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        String brokerUrl = determineBrokerUrl();
        String user = this.properties.getUser();
        String password = this.properties.getPassword();
        if (StringUtils.hasLength(user) && StringUtils.hasLength(password)) {
            return factoryClass.getConstructor(String.class, String.class, String.class)
                    .newInstance(user, password, brokerUrl);
        }
        return factoryClass.getConstructor(String.class).newInstance(brokerUrl);
    }

    private void customize(ActiveMQConnectionFactory connectionFactory) {
        for (ActiveMQConnectionFactoryCustomizer factoryCustomizer : this.factoryCustomizers) {
            factoryCustomizer.customize(connectionFactory);
        }
    }

    String determineBrokerUrl() {
        if (this.properties.getBrokerUrl() != null) {
            return this.properties.getBrokerUrl();
        }
        if (this.properties.isInMemory()) {
            return DEFAULT_EMBEDDED_BROKER_URL;
        }
        return DEFAULT_NETWORK_BROKER_URL;
    }
}
