package com.platform.report.infrastructure.persistence.repository;

import com.platform.report.infrastructure.persistence.entity.ReportDistributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表分发JPA仓储
 */
@Repository
public interface ReportDistributionJpaRepository extends JpaRepository<ReportDistributionEntity, String>, 
                                                        JpaSpecificationExecutor<ReportDistributionEntity> {
    
    /**
     * 根据报表ID查找
     */
    List<ReportDistributionEntity> findByReportId(String reportId);
    
    /**
     * 根据报表版本ID查找
     */
    List<ReportDistributionEntity> findByReportVersionId(String reportVersionId);
    
    /**
     * 根据创建者查找
     */
    List<ReportDistributionEntity> findByCreatedBy(String createdBy);
    
    /**
     * 根据状态查找
     */
    List<ReportDistributionEntity> findByStatus(ReportDistributionEntity.DistributionStatusEnum status);
    
    /**
     * 根据创建时间范围查找
     */
    List<ReportDistributionEntity> findByCreatedAtBetween(LocalDateTime fromTime, LocalDateTime toTime);
    
    /**
     * 根据最后分发时间范围查找
     */
    List<ReportDistributionEntity> findByLastDistributionTimeBetween(LocalDateTime fromTime, LocalDateTime toTime);
    
    /**
     * 根据名称模糊查找
     */
    List<ReportDistributionEntity> findByNameContainingIgnoreCase(String nameLike);
}
