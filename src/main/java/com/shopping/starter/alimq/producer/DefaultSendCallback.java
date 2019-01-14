package com.shopping.starter.alimq.producer;

import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import lombok.extern.log4j.Log4j2;

/**
 * @desc 消息者监听(订阅消费内容)
 */
@Log4j2
public class DefaultSendCallback implements SendCallback {

    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("消息发送成功:  topic=" + sendResult.getTopic() + ", msgId=" + sendResult.getMessageId());
    }

    @Override
    public void onException(OnExceptionContext context) {
        log.warn("消息发送失败: topic=" + context.getTopic() + ", msgId=" + context.getMessageId(), context.getException());
    }
}
