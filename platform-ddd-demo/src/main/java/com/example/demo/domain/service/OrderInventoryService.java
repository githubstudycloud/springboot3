package com.example.demo.domain.service;

import com.example.demo.domain.event.DomainEventPublisher;
import com.example.demo.domain.model.inventory.Inventory;
import com.example.demo.domain.model.order.Order;
import com.example.demo.domain.model.order.OrderId;
import com.example.demo.domain.model.order.OrderItem;
import com.example.demo.domain.model.order.ProductId;
import com.example.demo.domain.repository.InventoryRepository;
import com.example.demo.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 订单库存协调服务
 * 跨领域服务，协调订单和库存之间的交互
 */
@Service
@RequiredArgsConstructor
public class OrderInventoryService {
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * 确认订单并预留库存
     */
    @Transactional
    public void confirmOrderAndReserveInventory(OrderId orderId) {
        // 获取订单
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 检查库存并预留
        boolean allProductsAvailable = true;
        StringBuilder unavailableProducts = new StringBuilder();

        for (OrderItem item : order.getOrderItems()) {
            ProductId productId = item.getProductId();
            int quantity = item.getQuantity();

            // 查找产品库存
            Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);

            if (inventoryOpt.isPresent()) {
                Inventory inventory = inventoryOpt.get();

                // 检查是否有足够库存
                if (!inventory.hasEnoughStock(quantity)) {
                    allProductsAvailable = false;
                    unavailableProducts.append(productId.getValue()).append(", ");
                }
            } else {
                allProductsAvailable = false;
                unavailableProducts.append(productId.getValue()).append(", ");
            }
        }

        // 如果所有产品都有足够库存，则确认订单并预留库存
        if (allProductsAvailable) {
            // 先预留库存
            for (OrderItem item : order.getOrderItems()) {
                ProductId productId = item.getProductId();
                int quantity = item.getQuantity();

                Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
                Inventory inventory = inventoryOpt.get(); // 已经检查过存在性

                // 预留库存
                inventory.reserve(quantity);
                inventoryRepository.save(inventory);

                // 发布领域事件
                eventPublisher.publish(inventory.popDomainEvents());
            }

            // 确认订单
            order.confirm();
            orderRepository.save(order);

            // 发布领域事件
            eventPublisher.publish(order.popDomainEvents());
        } else {
            // 库存不足，抛出异常
            String unavailableProductsStr = unavailableProducts.toString();
            if (unavailableProductsStr.endsWith(", ")) {
                unavailableProductsStr = unavailableProductsStr.substring(0, unavailableProductsStr.length() - 2);
            }

            throw new IllegalStateException("以下产品库存不足: " + unavailableProductsStr);
        }
    }

    /**
     * 取消订单并释放库存
     */
    @Transactional
    public void cancelOrderAndReleaseInventory(OrderId orderId) {
        // 获取订单
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 只处理已确认但未支付的订单
        if (order.getStatus() == com.example.demo.domain.model.order.OrderStatus.CONFIRMED) {
            // 取消订单
            order.cancel();
            orderRepository.save(order);

            // 释放库存
            for (OrderItem item : order.getOrderItems()) {
                ProductId productId = item.getProductId();
                int quantity = item.getQuantity();

                Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);

                if (inventoryOpt.isPresent()) {
                    Inventory inventory = inventoryOpt.get();

                    // 取消预留
                    inventory.cancelReservation(quantity);
                    inventoryRepository.save(inventory);

                    // 发布领域事件
                    eventPublisher.publish(inventory.popDomainEvents());
                }
            }

            // 发布领域事件
            eventPublisher.publish(order.popDomainEvents());
        } else {
            throw new IllegalStateException("只有已确认但未支付的订单才能取消并释放库存");
        }
    }

    /**
     * 支付订单并完成库存预留
     */
    @Transactional
    public void payOrderAndCompleteReservation(OrderId orderId) {
        // 获取订单
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 只处理已确认的订单
        if (order.getStatus() == com.example.demo.domain.model.order.OrderStatus.CONFIRMED) {
            // 支付订单
            order.pay();
            orderRepository.save(order);

            // 完成库存预留（从预留转为实际扣减）
            for (OrderItem item : order.getOrderItems()) {
                ProductId productId = item.getProductId();
                int quantity = item.getQuantity();

                Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);

                if (inventoryOpt.isPresent()) {
                    Inventory inventory = inventoryOpt.get();

                    // 确认预留
                    inventory.confirmReservation(quantity);
                    inventoryRepository.save(inventory);

                    // 发布领域事件
                    eventPublisher.publish(inventory.popDomainEvents());
                }
            }

            // 发布领域事件
            eventPublisher.publish(order.popDomainEvents());
        } else {
            throw new IllegalStateException("只有已确认的订单才能支付并完成库存预留");
        }
    }
}
