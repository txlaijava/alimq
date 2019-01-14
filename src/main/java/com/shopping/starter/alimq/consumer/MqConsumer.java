package com.shopping.starter.alimq.consumer;


import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.shopping.starter.alimq.annotation.RocketMQMessageListener;
import com.shopping.starter.alimq.utils.MessageExtConst;
import lombok.extern.log4j.Log4j2;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @desc 消费者
 */
@Log4j2
public class MqConsumer implements BeanPostProcessor {

    private Properties properties;
    private List<Consumer> consumers = new ArrayList<>();
    private List<OrderConsumer> orderConsumers = new ArrayList<>();

    @Autowired
    Environment environment;

    public MqConsumer(Properties properties) {
        if (properties == null
                || properties.get(PropertyKeyConst.AccessKey) == null
                || properties.get(PropertyKeyConst.SecretKey) == null
                || properties.get(PropertyKeyConst.ONSAddr) == null) {
            throw new ONSClientException("consumer properties not set properly.");
        }
        this.properties = properties;
    }

    public void start() {
        //this.consumer = ONSFactory.createConsumer(properties);
        //this.consumer.start();
    }

    public void shutdown() {
        for (Consumer consumer : this.consumers) {
            //consumer.shutdown();
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * @Description: 获取所有消费者订阅内容(Topic、Tag)
     * @Param: [bean, beanName]
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = AopUtils.getTargetClass(bean);
        RocketMQMessageListener annotation = clazz.getAnnotation(RocketMQMessageListener.class);
        if (null != annotation) {
            String subExpression = StringUtils.arrayToDelimitedString(annotation.tag(), " || ");
            // 支持springEL风格配置项解析，如存在配置项，会优先将topic解析为配置项对应的值
            String topic = environment.resolvePlaceholders(annotation.topic());
            if(!StringUtils.isEmpty(annotation.consumerId())){
                properties.setProperty(PropertyKeyConst.ConsumerId,annotation.consumerId());
                // 设置 Consumer 实例的消费模式，默认为集群消费（值：CLUSTERING）;广播消费（BROADCASTING）
                properties.setProperty(PropertyKeyConst.MessageModel,annotation.messageMode());
            }
            /**
             * 消费模式 有序（单线程）或者无序（多线程）
             */
            if(MessageExtConst.CONSUME_MODE_CONCURRENTLY.equals(annotation.consumeMode())){
                @SuppressWarnings("rawtypes")
                AbstractMessageListener listener = (AbstractMessageListener) bean;
                Consumer consumer = ONSFactory.createConsumer(properties);
                consumer.subscribe(topic, subExpression, listener);
                consumer.start();
                this.consumers.add(consumer);
            }else if(MessageExtConst.CONSUME_MODE_ORDERLY.equals(annotation.consumeMode())){
                @SuppressWarnings("rawtypes")
                AbstractMessageOrderListener listener = (AbstractMessageOrderListener) bean;
                OrderConsumer consumer = ONSFactory.createOrderedConsumer(properties);
                consumer.subscribe(topic, subExpression, listener);
                consumer.start();
                this.orderConsumers.add(consumer);
            }
        }
        return bean;
    }
}
