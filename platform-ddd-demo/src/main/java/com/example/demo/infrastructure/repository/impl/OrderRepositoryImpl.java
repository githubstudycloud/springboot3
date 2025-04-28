package com.example.demo.infrastructure.repository.impl;

import com.example.demo.domain.event.DomainEvent;
import com.example.demo.domain.event.DomainEventPublisher;
import com.example.demo.domain.model.order.*;
import com.example.demo.domain.repository.OrderRepository;
import com.example.demo.infrastructure.entity.OrderEntity;
import com.example.demo.infrastructure.entity.OrderItemEntity;
import com.example.demo.infrastructure.repository.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单仓储实现
 */
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public void save(Order order) {
        OrderEntity entity = toEntity(order);
        orderJpaRepository.save(entity);

        // 发布所有未发布的领域事件
        List<DomainEvent> events = order.getAndClearDomainEvents();
        for (DomainEvent event : events) {
            eventPublisher.publish(event);
        }
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository.findById(orderId.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return orderJpaRepository.findByCustomerId(customerId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void remove(OrderId orderId) {
        orderJpaRepository.deleteById(orderId.getValue());
    }

    /**
     * 将领域对象转换为持久化实体
     */
    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId().getValue());
        entity.setCustomerId(order.getCustomerId().getValue());
        entity.setStatus(order.getStatus());
        entity.setTotalAmount(order.calculateTotalAmount());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());

        // 清除旧的订单项
        entity.getItems().clear();

        // 添加新的订单项
        for (OrderItem item : order.getOrderItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setProductId(item.getProductId().getValue());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPrice(item.getUnitPrice());
            entity.addItem(itemEntity);
        }

        return entity;
    }

    /**
     * 将持久化实体转换为领域对象
     */
    private Order toDomain(OrderEntity entity) {
        // 由于Order类的构造函数是私有的，我们需要使用反射或提供一个特殊的工厂方法来重建对象
        // 这里为了演示简单，直接使用反射创建对象，实际项目中应该提供更好的解决方案
        try {
            // 创建Order对象
            OrderId orderId = new OrderId(entity.getId());
            CustomerId customerId = new CustomerId(entity.getCustomerId());
            Order order = Order.create(customerId); // 使用工厂方法创建

            // 使用Java反射设置私有字段
            java.lang.reflect.Field idField = Order.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, orderId);

            java.lang.reflect.Field statusField = Order.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(order, entity.getStatus());

            java.lang.reflect.Field createdAtField = Order.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(order, entity.getCreatedAt());

            java.lang.reflect.Field updatedAtField = Order.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(order, entity.getUpdatedAt());

            java.lang.reflect.Field orderItemsField = Order.class.getDeclaredField("orderItems");
            orderItemsField.setAccessible(true);
            List<OrderItem> orderItems = new ArrayList<>();

            // 添加订单项
            for (OrderItemEntity itemEntity : entity.getItems()) {
                ProductId productId = new ProductId(itemEntity.getProductId());
                OrderItem item = new OrderItem(productId, itemEntity.getQuantity(), itemEntity.getUnitPrice());
                orderItems.add(item);
            }

            orderItemsField.set(order, orderItems);

            // 清除加载的领域对象的所有领域事件，因为这些事件已经被处理过了
            order.getAndClearDomainEvents();

            return order;
        } catch (Exception e) {
            throw new RuntimeException("无法将实体转换为领域对象", e);
        }
    }
}
