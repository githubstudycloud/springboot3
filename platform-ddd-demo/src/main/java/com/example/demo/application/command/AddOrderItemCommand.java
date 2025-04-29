package com.example.demo.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 添加订单项命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderItemCommand {
    @NotEmpty(message = "订单ID不能为空")
    private String orderId;

    @NotEmpty(message = "产品ID不能为空")
    private String productId;

    @Min(value = 1, message = "数量必须大于0")
    private int quantity;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;
    
    /**
     * 获取订单ID
     */
    public String getOrderId() {
        return orderId;
    }
    
    /**
     * 设置订单ID
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
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