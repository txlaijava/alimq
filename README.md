# spring boot starter for aliYunMQ [![Build Status](https://travis-ci.org/maihaoche/rocketmq-spring-boot-starter.svg?branch=master)](https://travis-ci.org/maihaoche/rocketmq-spring-boot-starter)
<p><a href=""><img src="https://maven-badges.herokuapp.com/maven-central/com.maihaoche/spring-boot-starter-rocketmq/badge.svg" alt="Maven Central" style="max-width:100%;"></a><a href="https://github.com/txlaijava/alimq/releases"><img src="https://camo.githubusercontent.com/795f06dcbec8d5adcfadc1eb7a8ac9c7d5007fce/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f72656c656173652d646f776e6c6f61642d6f72616e67652e737667" alt="GitHub release" data-canonical-src="https://img.shields.io/badge/release-download-orange.svg" style="max-width:100%;"></a>

### 项目介绍
<a href="https://www.aliyun.com/product/rocketmq?spm=5176.8142029.search.1.e9396d3ebT5xIC">消息队列 RocketMQ </a>是阿里巴巴自研消息产品，服务于整个集团已超过 13 年，经过阿里巴巴交易核心链路反复打磨与历年双十一购物狂欢节的严苛考验，是一个真正具备低延迟、高并发、高可用、高可靠，可支撑万亿级数据洪峰的分布式消息中间件。

本模块实现了阿里云MQ的以下几个功能。

* [x] 同步发送消息
* [x] 异步发送消息
* [x] 广播发送消息
* [x] 有序发送和消费消息
* [x] 发送延时消息
* [x] 消息tag支持
* [x] 自动序列化和反序列化消息体
* [x] 发送事务消息

### 如何集成
##### 1、添加依赖
```java
<dependency>
  <groupId>com.shopping</groupId>
  <artifactId>spring-boot-starter-aliyunmq</artifactId>
  <version>1.0.0.RELEASE</version>
</dependency>
```

因为我们有自己的maven私库，所以没有po到maven公有库里。配置代码库：

```java
<repositories>
    <repository>
        <id>shopping</id>
        <url>http://maven.sucok.com/content/groups/public</url>
    </repository>
    <repository>
        <id>sonatype-nexus-staging</id>
        <name>Sonatype Nexus Staging</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

#### 2、添加配置
```java
aliyun.mq.onsAddr=<地址>
aliyun.mq.accessKey=
aliyun.mq.secretKey=
aliyun.mq.TopicIdRblc=
#为false表示不引入producer，为true则producerId必须提供
aliyun.mq.producer.enabled=true
aliyun.mq.producer.producerId=
#为false表示不引入consumer，为true则consumerId必须提供
aliyun.mq.consumer.enabled=true
```

#### 3、添加生产者
> 顺序消息

按照消息的发布顺序进行顺序消费（FIFO），支持全局顺序与分区顺序

```java
@Autowired
OrderMessageTemplate orderMessageTemplate;

//发送
orderMessageTemplate(new MessageEvent("{topic}", "{tag}", msgBody));
```

> 普通消息,定时消息,延迟消息生产者

消息可在指定的时间点(如2019/01/01 15:00:00)或延迟时间(如30分钟后)进行投递

```java
@Autowired
RocketMQTemplate rocketMQTemplate;

// 发送普通消息
rocketMQTemplate.send(new MessageEvent("{topic}", "{tag}", msgBody));
// 延时消息，单位毫秒（ms），在指定延迟时间（当前时间之后）进行投递，例如消息在 3 秒后投递
rocketMQTemplate.sendAsync(new MessageEvent("{topic}", "{tag}", msgBody),3000);
// 延时消息，指定时间进行投递。例如消息在1天之后投递
Calendar cal = Calendar.getInstance();
cal.setTime(date);
cal.add(Calendar.DATE, 1);
rocketMQTemplate.sendAsync(new MessageEvent("{topic}", "{tag}", msgBody), cal.getTime(););
```

> 事务消息

类似 X/Open XA 的分布事务功能，既可做到系统间的解耦，又能保证数据的最终一致性

```java
@Autowired
TransactionMessageTemplate messageTemplate;

// 发送事务消息
/**封装消息*/
MessageEvent event = new MessageEvent();
event.setTopic("base_sms");
event.setTag("Tag_user");
   
User user = new User();
user.setName("Paul");
user.setAdds("北京市 昌平区 龙锦苑东二区");
 /**封装任意类型领域对象*/
event.setDomain(user);
    
transactionMessageTemplate.send(event,new TransactionExecuter() {
@Override
public TransactionStatus executer(MessageEvent messageEvent, Long hashValue, Object arg) {
	String transactionId = TransactionDemo.createTransaction();
	TransactionStatus status = TransactionDemo.checker();
	return status;
}
),"参数对象,以本字符串示例,会传递给TransactionExecuter.executer");

```

#### 4、添加订阅者
> 普通消息订阅者，实现AbstractMessageListener接口

* topic：支持SpringEl风格，取配置文件中的值。(必填)  
* tag：标签。* 标识所有。  (必填)  
* consumerId：消费者Id。  (必填)  
* consumeMode：消费模式 有序（单线程）或者无序（多线程） （默认无序）


```java
@RocketMQMessageListener(topic = "${aliyun.mq.TopicIdRblc}", tag = "giveCoupon", consumerId = "CID_shopping_giveCoupon", consumeMode = MessageExtConst.CONSUME_MODE_ORDERLY)
public class GiveCouponMessageListener extends AbstractMessageListener<MessageEvent> {
    /**
     * 消息处理
     */
    @Override
    public void handle(MessageEvent messageEvent) throws Exception {
    	//TODO 业务处理
    }
}
```

> 顺序消息订阅者，实现AbstractMessageOrderListener

```java
@RocketMQMessageListener(topic = "${aliyun.mq.TopicIdRblc}", tag = "giveCoupon", consumerId = "CID_shopping_giveCoupon", consumeMode = MessageExtConst.CONSUME_MODE_ORDERLY)
public class GiveCouponMessageListener extends AbstractMessageOrderListener <MessageEvent> {
    /**
     * 消息处理
     */
    @Override
    public void handle(MessageEvent messageEvent) throws Exception {
    	//TODO 业务处理
    }
}
```

#### 5、相关参考
----
* 官网Demo地址：<https://github.com/AliwareMQ/mq-demo>