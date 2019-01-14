/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shopping.starter.alimq.annotation;

import com.shopping.starter.alimq.utils.MessageExtConst;

import java.lang.annotation.*;

/**
 * @desc 消息者监听(订阅消费内容)
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMQMessageListener {

    /**
     * Topic name
     */
    String topic();

    /**
     * tag name
     */
    String[] tag() default "*";

    /**
     * 消费者id
     */
    String consumerId() default "";

    /**
     * 广播模式消费： BROADCASTING
     * 集群模式消费： CLUSTERING
     * @return 消息模式
     */
    String messageMode() default MessageExtConst.MESSAGE_MODE_CLUSTERING;

    /**
     * 使用线程池并发消费: CONCURRENTLY("CONCURRENTLY"),
     * 单线程消费: ORDERLY("ORDERLY");
     * @return 消费模式
     */
    String consumeMode() default MessageExtConst.CONSUME_MODE_CONCURRENTLY;
}
