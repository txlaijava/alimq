package com.shopping.starter.alimq;


import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import com.shopping.starter.alimq.consumer.MqConsumer;
import com.shopping.starter.alimq.producer.LocalTransactionCheckerImpl;
import com.shopping.starter.alimq.producer.OrderMessageTemplate;
import com.shopping.starter.alimq.producer.RocketMQTemplate;
import com.shopping.starter.alimq.producer.TransactionMessageTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

/**
 * @desc 初始化(生成|消费)相关配置
 */
@Log4j2
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMQAutoConfiguration {
    @Autowired
    private RocketMQProperties propConfig;


    @Bean(name = "producer",initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer",value = "enabled",havingValue = "true")
    public ProducerBean producer() {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        log.info("执行producer初始化……");
        properties.put(PropertyKeyConst.ProducerId, propConfig.getProducer().getProperty("producerId"));
        properties.put(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, propConfig.getOnsAddr());
        producerBean.setProperties(properties);
        producerBean.start();
        return producerBean;
    }
    
    @Bean(name = "orderProducer",initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer",value = "enabled",havingValue = "true")
    public OrderProducerBean orderProducer() {
    	OrderProducerBean producerBean = new OrderProducerBean();
        Properties properties = new Properties();
        log.info("执行producer初始化……");
        properties.put(PropertyKeyConst.ProducerId, propConfig.getProducer().getProperty("producerId"));
        properties.put(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, propConfig.getOnsAddr());
        producerBean.setProperties(properties);
        producerBean.start();
        return producerBean;
    }
    
    @Bean(name = "transactionProducer",initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer",value = "enabled",havingValue = "true")
    public TransactionProducerBean transactionProducer() {
    	TransactionProducerBean producerBean = new TransactionProducerBean();
        Properties properties = new Properties();
        log.info("执行producer初始化……");
        properties.put(PropertyKeyConst.ProducerId, propConfig.getProducer().getProperty("producerId"));
        properties.put(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, propConfig.getOnsAddr());
        producerBean.setProperties(properties);
        //LocalTransactionCheckerImpl必须在start方法调用前设置
        producerBean.setLocalTransactionChecker(new LocalTransactionCheckerImpl(null));
        producerBean.start();
        return producerBean;
    }


    @Bean(initMethod="start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.consumer",value = "enabled",havingValue = "true")
    public MqConsumer mqConsumer(){
        Properties properties = new Properties();
        log.info("1执行consumer初始化……");
        properties.setProperty(PropertyKeyConst.AccessKey, propConfig.getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, propConfig.getSecretKey());
        properties.setProperty(PropertyKeyConst.ONSAddr, propConfig.getOnsAddr());

        return  new MqConsumer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer",value = "enabled",havingValue = "true")
    public RocketMQTemplate rocketMQTemplate(){
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        return rocketMQTemplate;
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer",value = "enabled",havingValue = "true")
    public OrderMessageTemplate orderMessageTemplate(){
    	OrderMessageTemplate orderMessageTemplate = new OrderMessageTemplate();
        return orderMessageTemplate;
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer",value = "enabled",havingValue = "true")
    public TransactionMessageTemplate transactionMessageTemplate(){
    	TransactionMessageTemplate transactionMessageTemplate = new TransactionMessageTemplate();
        return transactionMessageTemplate;
    }
}
