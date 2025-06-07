package com.platform.config.repository;

import com.platform.config.entity.ConfigAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 配置审计Repository
 * 
 * @author Platform Team
 * @since 2.0.0
 */
@Repository
public interface ConfigAuditRepository extends JpaRepository<ConfigAudit, Long> {

    /**
     * 根据应用和环境查找审计日志
     */
    Page<ConfigAudit> findByApplicationAndProfileOrderByTimestampDesc(
            String application, String profile, Pageable pageable);

    /**
     * 根据操作类型查找审计日志
     */
    Page<ConfigAudit> findByOperationOrderByTimestampDesc(String operation, Pageable pageable);

    /**
     * 根据操作人员查找审计日志
     */
    Page<ConfigAudit> findByOperatorOrderByTimestampDesc(String operator, Pageable pageable);

    /**
     * 查找指定时间范围内的审计日志
     */
    @Query("SELECT ca FROM ConfigAudit ca WHERE ca.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY ca.timestamp DESC")
    List<ConfigAudit> findAuditsByTimeRange(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 根据应用、环境和时间范围查找审计日志
     */
    @Query("SELECT ca FROM ConfigAudit ca WHERE ca.application = :application " +
           "AND ca.profile = :profile AND ca.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY ca.timestamp DESC")
    List<ConfigAudit> findAuditsByAppProfileAndTimeRange(@Param("application") String application,
                                                         @Param("profile") String profile,
                                                         @Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计特定操作的数量
     */
    long countByOperation(String operation);

    /**
     * 统计特定操作员的操作数量
     */
    long countByOperator(String operator);

    /**
     * 查找失败的操作
     */
    Page<ConfigAudit> findByResultOrderByTimestampDesc(String result, Pageable pageable);

    /**
     * 根据版本ID查找相关审计记录
     */
    List<ConfigAudit> findByVersionIdOrderByTimestampDesc(Long versionId);

    /**
     * 统计每日操作数量
     */
    @Query("SELECT DATE(ca.timestamp) as date, COUNT(ca) as count " +
           "FROM ConfigAudit ca WHERE ca.timestamp >= :startDate " +
           "GROUP BY DATE(ca.timestamp) ORDER BY DATE(ca.timestamp)")
    List<Object[]> getDailyOperationStats(@Param("startDate") LocalDateTime startDate);

    /**
     * 统计操作类型分布
     */
    @Query("SELECT ca.operation, COUNT(ca) as count FROM ConfigAudit ca " +
           "WHERE ca.timestamp >= :startDate GROUP BY ca.operation")
    List<Object[]> getOperationTypeStats(@Param("startDate") LocalDateTime startDate);

    /**
     * 根据业务标识查找审计记录
     */
    List<ConfigAudit> findByBusinessIdOrderByTimestampDesc(String businessId);
} 