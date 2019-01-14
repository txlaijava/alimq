package com.shopping.starter.alimq.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.shopping.starter.alimq.event.MessageEvent;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @desc 事务消息生产者
 */
@Log4j2
public class TransactionMessageTemplate {

    @Resource
    private TransactionProducerBean transactionProducer;
    
    /**
     * 使用前需要调用该方法设置localTransactionChecker
     * @param localTransactionChecker
     */
    public void init(TransactionChecker transactionCheck) {
    	LocalTransactionCheckerImpl checkerImpl = (LocalTransactionCheckerImpl)transactionProducer.getLocalTransactionChecker();
    	checkerImpl.init(transactionCheck);
    }

    /****
     * @Description: 同步发送事务消息
     * @Param: [event]
     */
    public SendResult send(MessageEvent event,LocalTransactionExecuter executer,Object arg) {
    	if(event == null) {
    		throw new RuntimeException("event is null.");
    	}
        log.info("start to send message. [topic: {}, tag: {}]", event.getTopic(), event.getTag());
        if (StringUtils.isEmpty(event.getTopic())  || null == event.getDomain()) {
            throw new RuntimeException("topic, or body is null.");
        }
        Message message = new Message(event.getTopic(), event.getTag(), SerializationUtils.serialize(event));
        message.setKey(event.generateTxId());
        SendResult result = this.transactionProducer.send(message, executer, arg);
        log.info("send message success. "+ result.toString());
        return result;
    }
    
    /****
     * @Description: 同步发送事务消息
     * @Param: [event]
     */
    public SendResult send(MessageEvent event,TransactionExecuter transactionExecuter,Object arg) {
    	return send(event, new LocalTransactionExecuterImpl(transactionExecuter), arg);
    }

}
