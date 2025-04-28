package com.example.demo.interfaces.rest;

import com.example.demo.application.command.AddOrderItemCommand;
import com.example.demo.application.command.CreateOrderCommand;
import com.example.demo.application.dto.OrderDTO;
import com.example.demo.application.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 订单REST接口控制器
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderApplicationService orderApplicationService;

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        OrderDTO order = orderApplicationService.createOrder(command);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * 添加订单项
     */
    @PostMapping("/items")
    public ResponseEntity<OrderDTO> addOrderItem(@Valid @RequestBody AddOrderItemCommand command) {
        OrderDTO order = orderApplicationService.addOrderItem(command);
        return ResponseEntity.ok(order);
    }

    /**
     * 确认订单
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable String orderId) {
        OrderDTO order = orderApplicationService.confirmOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * 支付订单
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderDTO> payOrder(@PathVariable String orderId) {
        OrderDTO order = orderApplicationService.payOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable String orderId) {
        OrderDTO order = orderApplicationService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable String orderId) {
        OrderDTO order = orderApplicationService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * 获取客户的所有订单
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable String customerId) {
        List<OrderDTO> orders = orderApplicationService.getCustomerOrders(customerId);
        return ResponseEntity.ok(orders);
    }
}
