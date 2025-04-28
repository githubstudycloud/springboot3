package com.example.demo.domain.event;

import com.example.demo.domain.model.order.OrderId;
import lombok.Getter;

/**
 * 订单创建事件
 */
@Getter
public class OrderCreatedEvent extends DomainEvent {
    private final OrderId orderId;
    
    public OrderCreatedEvent(OrderId orderId) {
        super();
        this.orderId = orderId;
    }
}
