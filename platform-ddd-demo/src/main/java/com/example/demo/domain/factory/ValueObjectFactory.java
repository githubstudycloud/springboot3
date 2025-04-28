package com.example.demo.domain.factory;

import com.example.demo.domain.model.order.CustomerId;
import com.example.demo.domain.model.order.OrderId;
import com.example.demo.domain.model.order.ProductId;
import org.springframework.stereotype.Component;

/**
 * 值对象工厂
 * 集中管理值对象的创建逻辑
 */
@Component
public class ValueObjectFactory {
    
    /**
     * 创建订单ID值对象
     */
    public OrderId createOrderId(String value) {
        return new OrderId(value);
    }
    
    /**
     * 创建客户ID值对象
     */
    public CustomerId createCustomerId(String value) {
        return new CustomerId(value);
    }
    
    /**
     * 创建产品ID值对象
     */
    public ProductId createProductId(String value) {
        return new ProductId(value);
    }
}
