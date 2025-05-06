package com.platform.report.infrastructure.persistence.repository;

import com.platform.report.infrastructure.persistence.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表JPA仓储
 */
@Repository
public interface ReportJpaRepository extends JpaRepository<ReportEntity, String>, 
                                           JpaSpecificationExecutor<ReportEntity> {
    
    /**
     * 根据模板ID查找
     */
    List<ReportEntity> findByTemplateId(String templateId);
    
    /**
     * 根据创建者查找
     */
    List<ReportEntity> findByCreatedBy(String createdBy);
    
    /**
     * 根据状态查找
     */
    List<ReportEntity> findByStatus(ReportEntity.ReportStatusEnum status);
    
    /**
     * 根据创建时间范围查找
     */
    List<ReportEntity> findByCreatedAtBetween(LocalDateTime fromTime, LocalDateTime toTime);
    
    /**
     * 根据名称模糊查找
     */
    List<ReportEntity> findByNameContainingIgnoreCase(String nameLike);
}
