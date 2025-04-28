package com.example.demo.domain.event;

import com.example.demo.domain.model.order.OrderId;
import com.example.demo.domain.model.order.OrderStatus;
import lombok.Getter;

/**
 * 订单状态变更事件
 */
@Getter
public class OrderStatusChangedEvent extends DomainEvent {
    private final OrderId orderId;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;

    public OrderStatusChangedEvent(OrderId orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        super();
        this.orderId = orderId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
