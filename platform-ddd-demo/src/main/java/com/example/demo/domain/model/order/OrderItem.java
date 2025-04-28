package com.example.demo.domain.model.order;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * 订单项实体
 */
@Getter
public class OrderItem {
    private final ProductId productId;
    private final BigDecimal unitPrice;
    private int quantity;

    public OrderItem(ProductId productId, int quantity, BigDecimal unitPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("产品ID不能为空");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("单价必须大于0");
        }

        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * 更新数量
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        this.quantity = newQuantity;
    }

    /**
     * 计算小计
     */
    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
