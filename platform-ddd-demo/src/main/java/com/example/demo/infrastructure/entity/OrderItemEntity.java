package com.example.demo.infrastructure.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 订单项持久化实体
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
    
    /**
     * 设置订单
     */
    public void setOrder(OrderEntity order) {
        this.order = order;
    }
    
    /**
     * 获取订单
     */
    public OrderEntity getOrder() {
        return order;
    }
    
    /**
     * 获取产品ID
     */
    public String getProductId() {
        return productId;
    }
    
    /**
     * 设置产品ID
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    /**
     * 获取数量
     */
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * 设置数量
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    /**
     * 获取单价
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    /**
     * 设置单价
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}