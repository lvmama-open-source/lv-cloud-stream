# lvmm-stream-rocketmq

#### 项目介绍
spring cloud stream 子项目activemq binder

#### 软件架构
软件架构说明


#### 安装教程

1. 依赖lvcloud-stream-starter-activemq
<dependency>
     <groupId>com.lv.cloud</groupId>
     <artifactId>lvcloud-stream-starter-activemq</artifactId>
     <version>0.0.1</version>
</dependency>
2. 在application.properties中添加如下配置
spring.cloud.stream.binders.activemq.type=activemq
lvcloud.stream.activemq.binder.broker=tcp://10.200.2.72:61616?wireFormat.maxInactivityDuration=0

spring.cloud.stream.default-binder=activemq
spring.cloud.stream.bindings.input.group=order_group
spring.cloud.stream.bindings.input.destination=order_create_test1
lvcloud.stream.activemq.bindings.input.consumer.concurrency=1-3
3. xxxx

#### 使用说明

1. xxxx
2. xxxx
3. xxxx

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)