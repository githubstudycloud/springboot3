package com.platform.report.infrastructure.persistence.repository;

import com.platform.report.infrastructure.persistence.entity.ScheduleExecutionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 计划执行记录JPA仓储
 */
@Repository
public interface ScheduleExecutionJpaRepository extends JpaRepository<ScheduleExecutionEntity, String>, 
                                                       JpaSpecificationExecutor<ScheduleExecutionEntity> {
    
    /**
     * 根据计划ID查找
     */
    List<ScheduleExecutionEntity> findByScheduleId(String scheduleId);
    
    /**
     * 根据计划ID分页查找
     */
    List<ScheduleExecutionEntity> findByScheduleIdOrderByExecutionTimeDesc(String scheduleId, Pageable pageable);
    
    /**
     * 根据执行时间范围查找
     */
    List<ScheduleExecutionEntity> findByExecutionTimeBetween(LocalDateTime fromTime, LocalDateTime toTime);
    
    /**
     * 根据状态查找
     */
    List<ScheduleExecutionEntity> findByStatus(ScheduleExecutionEntity.ExecutionStatusEnum status);
    
    /**
     * 根据报表ID查找
     */
    List<ScheduleExecutionEntity> findByReportId(String reportId);
    
    /**
     * 查找最近的执行记录
     */
    List<ScheduleExecutionEntity> findAllByOrderByExecutionTimeDesc(Pageable pageable);
    
    /**
     * 统计成功执行次数
     */
    long countByScheduleIdAndStatus(String scheduleId, ScheduleExecutionEntity.ExecutionStatusEnum status);
}
