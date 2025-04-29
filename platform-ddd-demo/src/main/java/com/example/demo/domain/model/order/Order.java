package com.example.demo.domain.model.order;

import com.example.demo.domain.event.OrderCreatedEvent;
import com.example.demo.domain.event.OrderStatusChangedEvent;
import com.example.demo.domain.model.common.AggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 订单聚合根
 */
@Getter
public class Order extends AggregateRoot<OrderId> {
    // 订单ID (实体标识)
    private final OrderId id;
    // 订单创建时间
    private final LocalDateTime createdAt;
    // 客户ID (值对象)
    private CustomerId customerId;
    // 订单项列表
    private List<OrderItem> items;
    // 订单状态
    private OrderStatus status;
    // 订单最后更新时间
    private LocalDateTime updatedAt;

    // 私有构造函数，确保通过工厂方法创建
    private Order(OrderId id, CustomerId customerId) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        // 添加订单创建事件
        this.registerEvent(new OrderCreatedEvent(this.id));
    }

    /**
     * 创建新订单的工厂方法
     */
    public static Order create(CustomerId customerId) {
        return new Order(new OrderId(UUID.randomUUID().toString()), customerId);
    }

    /**
     * 获取实体的唯一标识
     */
    @Override
    public OrderId getId() {
        return this.id;
    }

    /**
     * 添加订单项
     */
    public void addItem(ProductId productId, int quantity, BigDecimal unitPrice) {
        // 领域逻辑验证
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        // 检查是否已有相同产品的订单项，如有则更新数量
        for (OrderItem item : items) {
            if (item.getProductId().equals(productId)) {
                item.updateQuantity(item.getQuantity() + quantity);
                this.updatedAt = LocalDateTime.now();
                return;
            }
        }

        // 添加新的订单项
        OrderItem newItem = new OrderItem(productId, quantity, unitPrice);
        items.add(newItem);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 移除订单项
     */
    public void removeItem(ProductId productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 计算订单总金额
     */
    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    /**
     * 确认订单
     */
    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("订单不能为空");
        }

        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有新创建的订单才能确认");
        }

        OrderStatus oldStatus = this.status;
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();

        // 注册订单状态变更事件
        this.registerEvent(new OrderStatusChangedEvent(this.id, oldStatus, this.status));
    }

    /**
     * 支付订单
     */
    public void pay() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("只有已确认的订单才能支付");
        }

        OrderStatus oldStatus = this.status;
        this.status = OrderStatus.PAID;
        this.updatedAt = LocalDateTime.now();

        // 注册订单状态变更事件
        this.registerEvent(new OrderStatusChangedEvent(this.id, oldStatus, this.status));
    }

    /**
     * 取消订单
     */
    public void cancel() {
        if (status == OrderStatus.DELIVERED || status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("已交付或已完成的订单不能取消");
        }

        OrderStatus oldStatus = this.status;
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();

        // 注册订单状态变更事件
        this.registerEvent(new OrderStatusChangedEvent(this.id, oldStatus, this.status));
    }

    /**
     * 发货
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("只有已支付的订单才能发货");
        }

        OrderStatus oldStatus = this.status;
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = LocalDateTime.now();

        // 注册订单状态变更事件
        this.registerEvent(new OrderStatusChangedEvent(this.id, oldStatus, this.status));
    }

    /**
     * 交付订单
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("只有已发货的订单才能交付");
        }

        OrderStatus oldStatus = this.status;
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();

        // 注册订单状态变更事件
        this.registerEvent(new OrderStatusChangedEvent(this.id, oldStatus, this.status));
    }

    /**
     * 完成订单
     */
    public void complete() {
        if (status != OrderStatus.DELIVERED) {
            throw new IllegalStateException("只有已交付的订单才能完成");
        }

        OrderStatus oldStatus = this.status;
        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();

        // 注册订单状态变更事件
        this.registerEvent(new OrderStatusChangedEvent(this.id, oldStatus, this.status));
    }

    /**
     * 获取订单项的不可变列表
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}