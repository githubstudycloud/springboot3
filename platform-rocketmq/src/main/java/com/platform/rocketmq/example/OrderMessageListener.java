package com.platform.rocketmq.example;

import com.platform.rocketmq.annotation.RocketMQMessageListener;
import com.platform.rocketmq.exception.MessageProcessException;
import com.platform.rocketmq.listener.MessageListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.stereotype.Component;

/**
 * 订单消息监听器示例
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RocketMQMessageListener(
    consumerGroup = "${rocketmq.consumer.order.group:order-consumer-group}",
    topic = "${rocketmq.consumer.order.topic:order-topic}",
    tag = "ORDER_CREATED || ORDER_PAID",
    maxRetryTimes = 5,
    consumeTimeout = 30000
)
public class OrderMessageListener implements MessageListener<OrderMessageListener.OrderMessage> {
    
    @Override
    public void onMessage(OrderMessage message, MessageView messageView) throws MessageProcessException {
        log.info("Received order message: {}", message);
        
        try {
            // 验证消息
            validateMessage(message);
            
            // 处理订单消息
            processOrder(message);
            
            log.info("Order message processed successfully: orderId={}", message.getOrderId());
            
        } catch (IllegalArgumentException e) {
            // 参数异常，不需要重试
            throw MessageProcessException.nonRetryable("INVALID_PARAM", e.getMessage(), e);
        } catch (IllegalStateException e) {
            // 状态异常，不需要重试
            throw MessageProcessException.nonRetryable("INVALID_STATE", e.getMessage(), e);
        } catch (Exception e) {
            // 其他异常，需要重试
            throw MessageProcessException.retryable("PROCESS_ERROR", 
                "Failed to process order: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Class<OrderMessage> getMessageType() {
        return OrderMessage.class;
    }
    
    /**
     * 验证消息
     */
    private void validateMessage(OrderMessage message) {
        if (message.getOrderId() == null || message.getOrderId().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }
        
        if (message.getUserId() == null || message.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        if (message.getAmount() == null || message.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid order amount");
        }
    }
    
    /**
     * 处理订单
     */
    private void processOrder(OrderMessage message) {
        // 这里是实际的业务处理逻辑
        // 例如：保存到数据库、调用其他服务、发送通知等
        
        if (message.getStatus().equals("PAID")) {
            // 处理已支付订单
            log.info("Processing paid order: {}", message.getOrderId());
            // TODO: 实际的业务逻辑
        } else if (message.getStatus().equals("CREATED")) {
            // 处理新创建订单
            log.info("Processing created order: {}", message.getOrderId());
            // TODO: 实际的业务逻辑
        }
    }
    
    /**
     * 订单消息
     */
    @Data
    public static class OrderMessage {
        private String orderId;
        private Long userId;
        private Double amount;
        private String status;
        private Long createTime;
        private String remark;
    }
}