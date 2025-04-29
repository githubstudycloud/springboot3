package com.example.demo.application.dto;

import com.example.demo.domain.model.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String id;
    private String customerId;
    private List<OrderItemDTO> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 获取订单ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置订单ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取客户ID
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * 设置客户ID
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * 获取订单项列表
     */
    public List<OrderItemDTO> getItems() {
        return items;
    }

    /**
     * 设置订单项列表
     */
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    /**
     * 获取订单状态
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 设置订单状态
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 获取订单总金额
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置订单总金额
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 订单项数据传输对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private String productId;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        /**
         * 获取产品ID
         */
        public String getProductId() {
            return productId;
        }

        /**
         * 设置产品ID
         */
        public void setProductId(String productId) {
            this.productId = productId;
        }

        /**
         * 获取数量
         */
        public int getQuantity() {
            return quantity;
        }

        /**
         * 设置数量
         */
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        /**
         * 获取单价
         */
        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        /**
         * 设置单价
         */
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        /**
         * 获取小计
         */
        public BigDecimal getSubtotal() {
            return subtotal;
        }

        /**
         * 设置小计
         */
        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        /**
         * 构建器静态方法
         */
        public static OrderItemDTOBuilder builder() {
            return new OrderItemDTOBuilder();
        }
    }

    /**
     * 构建器静态方法
     */
    public static OrderDTOBuilder builder() {
        return new OrderDTOBuilder();
    }
}