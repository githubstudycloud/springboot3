package com.example.demo.domain.factory;

import com.example.demo.domain.model.order.CustomerId;
import com.example.demo.domain.model.order.Order;
import org.springframework.stereotype.Component;

/**
 * 订单聚合工厂
 * 封装订单聚合根的创建逻辑
 */
@Component
public class OrderFactory {
    
    /**
     * 创建新订单
     */
    public Order createOrder(CustomerId customerId) {
        return Order.create(customerId);
    }
    
    /**
     * 从存储数据重建订单（可用于仓储实现中）
     * 注意：此方法在实际项目中应该使用反射或其他机制实现，这里简化处理
     */
    public Order reconstitute(Object... data) {
        // 从存储数据中重建聚合根的逻辑
        // 实际项目中可以使用工厂方法、反射等机制
        throw new UnsupportedOperationException("方法未实现");
    }
}
