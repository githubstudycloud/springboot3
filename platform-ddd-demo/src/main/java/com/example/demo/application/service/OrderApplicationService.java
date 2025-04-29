package com.example.demo.application.service;

import com.example.demo.application.command.AddOrderItemCommand;
import com.example.demo.application.command.CreateOrderCommand;
import com.example.demo.application.dto.OrderDTO;
import com.example.demo.domain.model.order.CustomerId;
import com.example.demo.domain.model.order.Order;
import com.example.demo.domain.model.order.OrderId;
import com.example.demo.domain.model.order.ProductId;
import com.example.demo.domain.repository.OrderRepository;
import com.example.demo.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单应用服务
 * 协调领域对象完成用户用例
 */
@Service
@RequiredArgsConstructor
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;

    /**
     * 创建订单
     */
    @Transactional
    public OrderDTO createOrder(CreateOrderCommand command) {
        CustomerId customerId = new CustomerId(command.getCustomerId());
        Order order = orderDomainService.createOrder(customerId);
        orderRepository.save(order);
        return convertToDTO(order);
    }

    /**
     * 添加订单项
     */
    @Transactional
    public OrderDTO addOrderItem(AddOrderItemCommand command) {
        OrderId orderId = new OrderId(command.getOrderId());
        ProductId productId = new ProductId(command.getProductId());

        orderDomainService.addOrderItem(orderId, productId, command.getQuantity(), command.getUnitPrice());

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        return convertToDTO(order);
    }

    /**
     * 确认订单
     */
    @Transactional
    public OrderDTO confirmOrder(String orderId) {
        OrderId id = new OrderId(orderId);
        orderDomainService.confirmOrder(id);

        Optional<Order> orderOpt = orderRepository.findById(id);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        return convertToDTO(order);
    }

    /**
     * 支付订单
     */
    @Transactional
    public OrderDTO payOrder(String orderId) {
        OrderId id = new OrderId(orderId);
        orderDomainService.payOrder(id);

        Optional<Order> orderOpt = orderRepository.findById(id);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        return convertToDTO(order);
    }

    /**
     * 取消订单
     */
    @Transactional
    public OrderDTO cancelOrder(String orderId) {
        OrderId id = new OrderId(orderId);
        orderDomainService.cancelOrder(id);

        Optional<Order> orderOpt = orderRepository.findById(id);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        return convertToDTO(order);
    }

    /**
     * 查询订单
     */
    public OrderDTO getOrder(String orderId) {
        OrderId id = new OrderId(orderId);
        Optional<Order> orderOpt = orderRepository.findById(id);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        return convertToDTO(order);
    }

    /**
     * 查询客户的所有订单
     */
    public List<OrderDTO> getCustomerOrders(String customerId) {
        CustomerId id = new CustomerId(customerId);
        List<Order> orders = orderRepository.findByCustomerId(id);

        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将领域对象转换为DTO
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderDTO.OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderDTO.OrderItemDTO.builder()
                        .productId(item.getProductId().getValue())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.calculateSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .items(itemDTOs)
                .status(order.getStatus())
                .totalAmount(order.calculateTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
