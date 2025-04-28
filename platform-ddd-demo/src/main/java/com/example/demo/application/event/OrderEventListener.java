package com.example.demo.application.event;

import com.example.demo.domain.event.OrderCreatedEvent;
import com.example.demo.domain.event.OrderStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 订单事件监听器
 */
@Component
@Slf4j
public class OrderEventListener {

    /**
     * 监听订单创建事件
     */
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("订单已创建: orderId={}, eventId={}, occurredOn={}",
                event.getOrderId().getValue(), event.getEventId(), event.getOccurredOn());

        // 这里可以执行订单创建后的业务逻辑，如发送通知等
    }

    /**
     * 监听订单状态变更事件
     */
    @EventListener
    public void handleOrderStatusChangedEvent(OrderStatusChangedEvent event) {
        log.info("订单状态已变更: orderId={}, oldStatus={}, newStatus={}, eventId={}, occurredOn={}",
                event.getOrderId().getValue(), event.getOldStatus(), event.getNewStatus(),
                event.getEventId(), event.getOccurredOn());

        // 这里可以执行订单状态变更后的业务逻辑，如根据不同状态执行不同操作
        switch (event.getNewStatus()) {
            case PAID:
                // 处理支付完成逻辑
                log.info("订单支付完成，准备通知仓库发货...");
                break;
            case SHIPPED:
                // 处理发货逻辑
                log.info("订单已发货，准备通知物流...");
                break;
            case CANCELLED:
                // 处理取消逻辑
                log.info("订单已取消，准备退款处理...");
                break;
            case COMPLETED:
                // 处理完成逻辑
                log.info("订单已完成，准备统计和评价...");
                break;
            default:
                break;
        }
    }
}
