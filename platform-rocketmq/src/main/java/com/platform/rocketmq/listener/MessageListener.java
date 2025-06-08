package com.platform.rocketmq.listener;

import com.platform.rocketmq.exception.MessageProcessException;
import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * RocketMQ消息监听器接口
 * 所有消息监听器都需要实现此接口
 *
 * @author Platform Team
 * @since 1.0.0
 * @param <T> 消息体类型
 */
public interface MessageListener<T> {
    
    /**
     * 处理消息
     *
     * @param message 消息体
     * @param messageView 原始消息视图
     * @throws MessageProcessException 消息处理异常
     */
    void onMessage(T message, MessageView messageView) throws MessageProcessException;
    
    /**
     * 获取消息体类型
     *
     * @return 消息体类型
     */
    Class<T> getMessageType();
}