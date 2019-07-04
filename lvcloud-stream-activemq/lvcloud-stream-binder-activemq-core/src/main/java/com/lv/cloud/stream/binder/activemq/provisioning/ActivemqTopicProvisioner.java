package com.lv.cloud.stream.binder.activemq.provisioning;

import com.lv.cloud.stream.binder.activemq.properties.ActivemqBinderConfigurationProperties;
import com.lv.cloud.stream.binder.activemq.properties.ActivemqConsumerProperties;
import com.lv.cloud.stream.binder.activemq.properties.ActivemqProducerProperties;
import com.lv.cloud.stream.binder.activemq.utils.ActivemqTopicUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;

public class ActivemqTopicProvisioner implements ProvisioningProvider<ExtendedConsumerProperties<ActivemqConsumerProperties>,
        ExtendedProducerProperties<ActivemqProducerProperties>>, InitializingBean {

    private final Log logger = LogFactory.getLog(this.getClass());

    private ActivemqBinderConfigurationProperties activemqBinderConfigurationProperties;

    private JmsProperties jmsProperties;

    public ActivemqTopicProvisioner(ActivemqBinderConfigurationProperties activemqBinderConfigurationProperties, JmsProperties jmsProperties){
        this.activemqBinderConfigurationProperties = activemqBinderConfigurationProperties;
        this.jmsProperties = jmsProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public ProducerDestination provisionProducerDestination(String name, ExtendedProducerProperties<ActivemqProducerProperties> properties) throws ProvisioningException {
        ActivemqTopicUtils.validateTopicName(name);
        //TODO：？

        return new ActivemqProducerDestination(name);
    }

    @Override
    public ConsumerDestination provisionConsumerDestination(String name, String group, ExtendedConsumerProperties<ActivemqConsumerProperties> properties) throws ProvisioningException {
        ActivemqTopicUtils.validateTopicName(name);
        //TODO：？

        return new ActivemqConsumerDestination(name);
    }

    private static final class ActivemqProducerDestination implements ProducerDestination {

        private final String producerDestinationName;


        ActivemqProducerDestination(String destinationName) {
            this.producerDestinationName = destinationName;
        }

        @Override
        public String getName() {
            return producerDestinationName;
        }

        @Override
        public String getNameForPartition(int partition) {
            return producerDestinationName;
        }

        @Override
        public String toString() {
            return "ActivemqProducerDestination{" +
                    "producerDestinationName='" + producerDestinationName + '\'' +
                    '}';
        }
    }

    private static final class ActivemqConsumerDestination implements ConsumerDestination {

        private final String consumerDestinationName;

        ActivemqConsumerDestination(String consumerDestinationName) {
            this(consumerDestinationName, 0, null);
        }

        ActivemqConsumerDestination(String consumerDestinationName, int partitions) {
            this(consumerDestinationName, partitions, null);
        }

        ActivemqConsumerDestination(String consumerDestinationName, Integer partitions, String dlqName) {
            this.consumerDestinationName = consumerDestinationName;
        }

        @Override
        public String getName() {
            return this.consumerDestinationName;
        }

        @Override
        public String toString() {
            return "ActivemqConsumerDestination{" +
                    "consumerDestinationName='" + consumerDestinationName + '\'' + '}';
        }
    }
}
