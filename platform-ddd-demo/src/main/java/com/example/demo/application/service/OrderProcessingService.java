package com.example.demo.application.service;

import com.example.demo.application.dto.OrderDTO;
import com.example.demo.domain.model.order.OrderId;
import com.example.demo.domain.repository.OrderRepository;
import com.example.demo.domain.service.OrderInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 订单处理应用服务
 * 封装跨领域服务的调用
 */
@Service
@RequiredArgsConstructor
public class OrderProcessingService {
    private final OrderRepository orderRepository;
    private final OrderInventoryService orderInventoryService;
    private final OrderApplicationService orderApplicationService;
    
    /**
     * 确认订单并预留库存
     */
    @Transactional
    public OrderDTO confirmOrderWithInventoryCheck(String orderId) {
        OrderId id = new OrderId(orderId);
        
        // 调用跨领域服务进行订单确认和库存预留
        orderInventoryService.confirmOrderAndReserveInventory(id);
        
        // 返回更新后的订单
        return orderApplicationService.getOrder(orderId);
    }
    
    /**
     * 取消订单并释放库存
     */
    @Transactional
    public OrderDTO cancelOrderAndReleaseInventory(String orderId) {
        OrderId id = new OrderId(orderId);
        
        // 调用跨领域服务进行订单取消和库存释放
        orderInventoryService.cancelOrderAndReleaseInventory(id);
        
        // 返回更新后的订单
        return orderApplicationService.getOrder(orderId);
    }
    
    /**
     * 支付订单并完成库存预留
     */
    @Transactional
    public OrderDTO payOrderAndCompleteReservation(String orderId) {
        OrderId id = new OrderId(orderId);
        
        // 调用跨领域服务进行订单支付和库存预留完成
        orderInventoryService.payOrderAndCompleteReservation(id);
        
        // 返回更新后的订单
        return orderApplicationService.getOrder(orderId);
    }
}
