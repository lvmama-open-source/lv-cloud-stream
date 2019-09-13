# lvcloud-stream
（Sping Cloud Stream子项目）Lv Cloud Stream is a framework for building highly scalable event-driven microservices connected with shared messaging systems.

## 1、lvcloud-stream-activemq：spring cloud stream集成activemq
   ### 1.1、引入说明：
      1）pom依赖：
      <dependency>
          <groupId>com.lv.cloud</groupId>
          <artifactId>lvcloud-stream-starter-activemq</artifactId>
          <version>1.0.0-SNAPSHOT</version>
      </dependency>

      2）加注解@EnableBinding，具体使用参照demo：lvcloud-stream-binder-activemq-test
      
      3）配置：
      ### lvcloud.stream.activemq ###
      spring.cloud.stream.binders.activemq.type=activemq
      lvcloud.stream.activemq.binder.broker=tcp://10.200.4.83:61616?wireFormat.maxInactivityDuration=0
      
## 2、lvcloud-stream-rocketmq：spring cloud stream集成rocketmq

   ### 2.1、引入说明：
      1）pom依赖：
      <dependency>
          <groupId>com.lv.cloud</groupId>
          <artifactId>lvcloud-stream-starter-rocketmq</artifactId>
          <version>1.0.0-SNAPSHOT</version>
      </dependency>
      
      2）加注解@EnableBinding，具体使用参照demo：lvcloud-stream-binder-rocketmq-test
      
      3）配置：
      spring.cloud.stream.binders.rocketmq.type=rocketmq
      lvcloud.stream.rocketmq.binder.namesrvAddr=10.113.9.17:9876;10.113.9.16:9876;10.113.9.19:9876
      lvcloud.stream.rocketmq.binder.autoCreateTopics=true
