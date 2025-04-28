package com.example.demo.infrastructure.repository.jpa;

import com.example.demo.infrastructure.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单JPA仓储接口
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    
    /**
     * 根据客户ID查找订单
     */
    List<OrderEntity> findByCustomerId(String customerId);
}
