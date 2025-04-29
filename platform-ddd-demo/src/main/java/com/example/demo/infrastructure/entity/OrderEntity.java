package com.example.demo.infrastructure.entity;

import com.example.demo.domain.model.order.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单持久化实体
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class OrderEntity {
    @Id
    private String id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    /**
     * 获取订单ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * 设置订单ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * 获取客户ID
     */
    public String getCustomerId() {
        return customerId;
    }
    
    /**
     * 设置客户ID
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    /**
     * 获取订单状态
     */
    public OrderStatus getStatus() {
        return status;
    }
    
    /**
     * 设置订单状态
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    /**
     * 获取订单总金额
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    /**
     * 设置订单总金额
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    /**
     * 获取创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * 设置创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * 获取更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * 设置更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 获取订单项列表
     */
    public List<OrderItemEntity> getItems() {
        return items;
    }
    
    /**
     * 设置订单项列表
     */
    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }

    /**
     * 添加订单项
     */
    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * 移除订单项
     */
    public void removeItem(OrderItemEntity item) {
        items.remove(item);
        item.setOrder(null);
    }
}