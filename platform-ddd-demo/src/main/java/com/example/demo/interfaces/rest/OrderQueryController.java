package com.example.demo.interfaces.rest;

import com.example.demo.application.dto.OrderDTO;
import com.example.demo.application.query.OrderQueryService;
import com.example.demo.domain.model.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单查询REST接口控制器
 * 实现CQRS模式中的查询部分
 */
@RestController
@RequestMapping("/api/queries/orders")
@RequiredArgsConstructor
public class OrderQueryController {
    private final OrderQueryService orderQueryService;
    
    /**
     * 获取所有订单摘要信息
     */
    @GetMapping
    public ResponseEntity<List<OrderQueryService.OrderSummaryDTO>> getAllOrders() {
        List<OrderQueryService.OrderSummaryDTO> orders = orderQueryService.findAllOrderSummaries();
        return ResponseEntity.ok(orders);
    }
    
    /**
     * 根据状态查询订单
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderQueryService.OrderSummaryDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderQueryService.OrderSummaryDTO> orders = orderQueryService.findOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetails(@PathVariable String orderId) {
        OrderDTO order = orderQueryService.findOrderDetails(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
}
