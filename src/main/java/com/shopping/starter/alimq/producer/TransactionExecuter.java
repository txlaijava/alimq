package com.shopping.starter.alimq.producer;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.shopping.starter.alimq.event.MessageEvent;


/**
 * 用作LocalTransactionExecuterImpl的构造器参数,用于执行本地事务操作
 */
@FunctionalInterface
public interface TransactionExecuter {
	TransactionStatus executer(MessageEvent messageEvent, Long hashValue, Object arg);
}
