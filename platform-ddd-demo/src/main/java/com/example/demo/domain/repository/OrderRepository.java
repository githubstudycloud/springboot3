package com.example.demo.domain.repository;

import com.example.demo.domain.model.order.CustomerId;
import com.example.demo.domain.model.order.Order;
import com.example.demo.domain.model.order.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口
 * 定义在领域层，实现在基础设施层
 */
public interface OrderRepository {
    /**
     * 保存订单
     */
    void save(Order order);
    
    /**
     * 根据订单ID查找订单
     */
    Optional<Order> findById(OrderId orderId);
    
    /**
     * 根据客户ID查找所有订单
     */
    List<Order> findByCustomerId(CustomerId customerId);
    
    /**
     * 删除订单
     */
    void remove(OrderId orderId);
}
