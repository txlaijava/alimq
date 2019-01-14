package com.shopping.starter.alimq.consumer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.shopping.starter.alimq.event.MessageEvent;
import com.shopping.starter.alimq.utils.BeanUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.SerializationUtils;

import java.util.Map;

/**
 * 顺序消息（单线程）
 * @desc 消息监听者需要继承该抽象类，实现handle方法，消息消费逻辑处理(如果抛出异常，则重新入队列)
 */
@Log4j2
public abstract class AbstractMessageOrderListener<T> implements MessageOrderListener {

    public abstract void handle(T body) throws Exception;

    @Override
    public OrderAction consume(Message message, ConsumeOrderContext context) {
        log.info("接收顺序消息:[topic: {}, tag: {}, msgId: {}, startDeliverTime: {}]", message.getTopic(), message.getTag(), message.getMsgID(), message.getStartDeliverTime());
        try {
            if(SerializationUtils.deserialize(message.getBody()) instanceof Map){
                Map map = (Map) SerializationUtils.deserialize(message.getBody());
                MessageEvent event = BeanUtil.map2Bean(map, MessageEvent.class);
                handle((T)event);
            }else{
                handle((T)SerializationUtils.deserialize(message.getBody()));
            }
            log.info("handle message success. message id:"+message.getMsgID());
            return OrderAction.Success;
        } catch (Exception e) {
            //消费失败
            log.warn("handle message fail, requeue it. message id:"+message.getMsgID(), e);
            return OrderAction.Suspend;
        }
    }
}
