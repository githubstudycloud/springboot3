package com.platform.report.infrastructure.persistence.repository;

import com.platform.report.infrastructure.persistence.entity.ReportScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表计划JPA仓储
 */
@Repository
public interface ReportScheduleJpaRepository extends JpaRepository<ReportScheduleEntity, String>, 
                                                   JpaSpecificationExecutor<ReportScheduleEntity> {
    
    /**
     * 根据模板ID查找
     */
    List<ReportScheduleEntity> findByTemplateId(String templateId);
    
    /**
     * 根据创建者查找
     */
    List<ReportScheduleEntity> findByCreatedBy(String createdBy);
    
    /**
     * 根据状态查找
     */
    List<ReportScheduleEntity> findByStatus(ReportScheduleEntity.ScheduleStatusEnum status);
    
    /**
     * 根据状态和下次执行时间查找
     */
    List<ReportScheduleEntity> findByStatusAndNextExecutionTimeBefore(
            ReportScheduleEntity.ScheduleStatusEnum status, LocalDateTime time);
    
    /**
     * 根据名称模糊查找
     */
    List<ReportScheduleEntity> findByNameContainingIgnoreCase(String nameLike);
}
