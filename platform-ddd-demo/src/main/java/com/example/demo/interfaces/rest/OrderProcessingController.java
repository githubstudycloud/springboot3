package com.example.demo.interfaces.rest;

import com.example.demo.application.dto.OrderDTO;
import com.example.demo.application.service.OrderProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单处理REST接口控制器
 */
@RestController
@RequestMapping("/api/order-processing")
@RequiredArgsConstructor
public class OrderProcessingController {
    private final OrderProcessingService orderProcessingService;
    
    /**
     * 确认订单并预留库存
     */
    @PostMapping("/{orderId}/confirm-with-inventory")
    public ResponseEntity<OrderDTO> confirmOrderWithInventory(@PathVariable String orderId) {
        OrderDTO order = orderProcessingService.confirmOrderWithInventoryCheck(orderId);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 取消订单并释放库存
     */
    @PostMapping("/{orderId}/cancel-and-release")
    public ResponseEntity<OrderDTO> cancelOrderAndReleaseInventory(@PathVariable String orderId) {
        OrderDTO order = orderProcessingService.cancelOrderAndReleaseInventory(orderId);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 支付订单并完成库存预留
     */
    @PostMapping("/{orderId}/pay-and-complete")
    public ResponseEntity<OrderDTO> payOrderAndCompleteReservation(@PathVariable String orderId) {
        OrderDTO order = orderProcessingService.payOrderAndCompleteReservation(orderId);
        return ResponseEntity.ok(order);
    }
}
