package com.example.demo.domain.service;

import com.example.demo.domain.event.DomainEvent;
import com.example.demo.domain.event.DomainEventPublisher;
import com.example.demo.domain.model.order.CustomerId;
import com.example.demo.domain.model.order.Order;
import com.example.demo.domain.model.order.OrderId;
import com.example.demo.domain.model.order.ProductId;
import com.example.demo.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 订单领域服务
 * 包含跨实体的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class OrderDomainService {
    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;
    
    /**
     * 创建订单
     */
    public Order createOrder(CustomerId customerId) {
        Order newOrder = Order.create(customerId);
        orderRepository.save(newOrder);
        
        // 发布领域事件
        publishEvents(newOrder);
        
        return newOrder;
    }
    
    /**
     * 添加订单项
     */
    public void addOrderItem(OrderId orderId, ProductId productId, int quantity, BigDecimal unitPrice) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.addItem(productId, quantity, unitPrice);
        orderRepository.save(order);
        
        // 发布领域事件
        publishEvents(order);
    }
    
    /**
     * 确认订单
     */
    public void confirmOrder(OrderId orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.confirm();
        orderRepository.save(order);
        
        // 发布领域事件
        publishEvents(order);
    }
    
    /**
     * 取消订单
     */
    public void cancelOrder(OrderId orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.cancel();
        orderRepository.save(order);
        
        // 发布领域事件
        publishEvents(order);
    }
    
    /**
     * 支付订单
     */
    public void payOrder(OrderId orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.pay();
        orderRepository.save(order);
        
        // 发布领域事件
        publishEvents(order);
    }
    
    /**
     * 发货
     */
    public void shipOrder(OrderId orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.ship();
        orderRepository.save(order);
        
        // 发布领域事件
        publishEvents(order);
    }
    
    /**
     * 交付订单
     */
    public void deliverOrder(OrderId orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.deliver();
        orderRepository.save(order);
        
        // 发布领域事件
        publishEvents(order);
    }
    
    /**
     * 完成订单
     */
    public void completeOrder(OrderId orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.complete();
        orderRepository.save(order);
        
        // 发布领域事件
        publishEvents(order);
    }
    
    /**
     * 发布聚合根中的所有领域事件
     */
    private void publishEvents(Order order) {
        List<DomainEvent> events = order.popDomainEvents();
        events.forEach(eventPublisher::publish);
    }
}
