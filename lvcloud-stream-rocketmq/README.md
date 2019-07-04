# lvmm-stream-rocketmq

#### 项目介绍
spring cloud stream 子项目rocketmq binder

#### 软件架构
软件架构说明


#### 安装教程

1. 依赖lvstream-starter-rocketmq
<dependency>
     <groupId>com.lvmama.cloud</groupId>
     <artifactId>lvstream-starter-rocketmq</artifactId>
     <version>0.0.1-SNAPSHOT</version>
</dependency>
2. 在application.properties中添加如下配置
spring.cloud.stream.default-binder=rocketmq
spring.cloud.stream.bindings.ORDER.group=order_group
spring.cloud.stream.bindings.ORDER.destination=topic1
lvstream.rocketmq.binder.namesrvAddr=ip1:port1;ip2:port2;ip3:port3;
lvstream.rocketmq.binder.autoCreateTopics=true
lvstream.rocketmq.binder.producerGroup=LvmmProducer
lvstream.rocketmq.binder.topics.order_create.tags=lvmm_order_schedule,test111
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