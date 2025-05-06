package com.example.platform.config.infrastructure.persistence.repository;

import com.example.platform.config.infrastructure.persistence.entity.ConfigChangeEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配置变更事件JPA仓储接口
 */
@Repository
public interface ConfigChangeEventJpaRepository extends JpaRepository<ConfigChangeEventEntity, Long> {

    /**
     * 查询特定配置的变更事件
     */
    List<ConfigChangeEventEntity> findByDataIdAndGroupAndEnvironmentOrderByOccurredTimeDesc(
            String dataId, String group, String environment);

    /**
     * 查询最近的变更事件
     */
    @Query("SELECT e FROM ConfigChangeEventEntity e ORDER BY e.occurredTime DESC")
    List<ConfigChangeEventEntity> findRecentLimited(@Param("limit") int limit);

    /**
     * 按环境查询最近的变更事件
     */
    @Query("SELECT e FROM ConfigChangeEventEntity e WHERE e.environment = :environment " +
           "ORDER BY e.occurredTime DESC")
    List<ConfigChangeEventEntity> findRecentByEnvironmentLimited(
            @Param("environment") String environment, @Param("limit") int limit);
}
