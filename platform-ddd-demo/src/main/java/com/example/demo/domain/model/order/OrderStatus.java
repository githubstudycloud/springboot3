package com.example.demo.domain.model.order;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    CREATED("已创建"),
    CONFIRMED("已确认"),
    PAID("已支付"),
    SHIPPED("已发货"),
    DELIVERED("已交付"),
    COMPLETED("已完成"),
    CANCELLED("已取消");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
