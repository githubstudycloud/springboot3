package com.example.demo.application.query;

import com.example.demo.application.dto.OrderDTO;
import com.example.demo.domain.model.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单查询服务
 * 实现CQRS模式中的查询部分，直接从数据库读取，不经过领域模型
 */
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final JdbcTemplate jdbcTemplate;

    /**
     * 查询所有订单简要信息
     */
    public List<OrderSummaryDTO> findAllOrderSummaries() {
        String sql = "SELECT o.id, o.customer_id, o.status, o.total_amount, o.created_at " +
                "FROM orders o ORDER BY o.created_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OrderSummaryDTO summary = new OrderSummaryDTO();
            summary.setId(rs.getString("id"));
            summary.setCustomerId(rs.getString("customer_id"));
            summary.setStatus(OrderStatus.valueOf(rs.getString("status")));
            summary.setTotalAmount(rs.getBigDecimal("total_amount"));
            summary.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            return summary;
        });
    }

    /**
     * 根据状态查询订单
     */
    public List<OrderSummaryDTO> findOrdersByStatus(OrderStatus status) {
        String sql = "SELECT o.id, o.customer_id, o.status, o.total_amount, o.created_at " +
                "FROM orders o WHERE o.status = ? ORDER BY o.created_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OrderSummaryDTO summary = new OrderSummaryDTO();
            summary.setId(rs.getString("id"));
            summary.setCustomerId(rs.getString("customer_id"));
            summary.setStatus(OrderStatus.valueOf(rs.getString("status")));
            summary.setTotalAmount(rs.getBigDecimal("total_amount"));
            summary.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            return summary;
        }, status.name());
    }

    /**
     * 获取订单详情及订单项
     */
    public OrderDTO findOrderDetails(String orderId) {
        // 查询订单基本信息
        String orderSql = "SELECT o.id, o.customer_id, o.status, o.total_amount, o.created_at, o.updated_at " +
                "FROM orders o WHERE o.id = ?";

        OrderDTO order = jdbcTemplate.queryForObject(orderSql, (rs, rowNum) -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(rs.getString("id"));
            dto.setCustomerId(rs.getString("customer_id"));
            dto.setStatus(OrderStatus.valueOf(rs.getString("status")));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            dto.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            return dto;
        }, orderId);

        if (order == null) {
            return null;
        }

        // 查询订单项
        String itemsSql = "SELECT i.product_id, i.quantity, i.unit_price " +
                "FROM order_items i WHERE i.order_id = ?";

        List<OrderDTO.OrderItemDTO> items = jdbcTemplate.query(itemsSql, (rs, rowNum) -> {
            OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
            item.setProductId(rs.getString("product_id"));
            item.setQuantity(rs.getInt("quantity"));
            item.setUnitPrice(rs.getBigDecimal("unit_price"));
            item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return item;
        }, orderId);

        order.setItems(items);

        return order;
    }

    /**
     * 订单摘要DTO
     */
    public static class OrderSummaryDTO {
        private String id;
        private String customerId;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}
