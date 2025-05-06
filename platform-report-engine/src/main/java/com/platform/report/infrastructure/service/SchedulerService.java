package com.platform.report.infrastructure.service;

import com.platform.report.domain.model.schedule.ReportSchedule;
import com.platform.report.domain.model.schedule.ScheduleStatus;
import com.platform.report.domain.repository.ReportScheduleRepository;
import com.platform.report.domain.service.ReportGenerationService;
import com.platform.report.infrastructure.exception.ScheduleExecutionException;
import com.platform.report.infrastructure.persistence.entity.ScheduleExecutionEntity;
import com.platform.report.infrastructure.persistence.repository.ScheduleExecutionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * 报表计划执行服务
 * 用于定时执行报表生成计划
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    
    private final ReportScheduleRepository scheduleRepository;
    private final ReportGenerationService reportGenerationService;
    private final ScheduleExecutionJpaRepository executionRepository;
    
    /**
     * 检查并执行到期的计划
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndExecuteSchedules() {
        log.info("Checking for due schedules...");
        LocalDateTime now = LocalDateTime.now();
        
        List<ReportSchedule> dueSchedules = scheduleRepository.findByStatusAndNextExecutionTimeBefore(
                ScheduleStatus.ACTIVE, now);
        
        log.info("Found {} due schedules", dueSchedules.size());
        
        for (ReportSchedule schedule : dueSchedules) {
            try {
                executeSchedule(schedule);
            } catch (Exception e) {
                log.error("Failed to execute schedule {}: {}", schedule.getId(), e.getMessage(), e);
                // 记录执行失败
                saveExecutionRecord(schedule, null, e.getMessage(), true);
            }
        }
    }
    
    /**
     * 执行单个计划
     */
    @Transactional
    public String executeSchedule(ReportSchedule schedule) {
        log.info("Executing schedule: {}", schedule.getId());
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            // 生成报表
            String reportId = reportGenerationService.createReport(
                    schedule.getName() + " - " + startTime.toString(),
                    schedule.getTemplateId(),
                    schedule.getCreatedBy(),
                    schedule.getParameters());
            
            // 更新计划执行信息
            schedule.updateExecution(startTime, reportId);
            scheduleRepository.save(schedule);
            
            // 保存执行记录
            saveExecutionRecord(schedule, reportId, null, false);
            
            log.info("Schedule executed successfully: {}, reportId: {}", schedule.getId(), reportId);
            return reportId;
        } catch (Exception e) {
            log.error("Failed to execute schedule {}", schedule.getId(), e);
            throw new ScheduleExecutionException("Failed to execute schedule: " + e.getMessage(), e);
        }
    }
    
    /**
     * 保存执行记录
     */
    private void saveExecutionRecord(ReportSchedule schedule, String reportId, String errorMessage, boolean failed) {
        ScheduleExecutionEntity execution = new ScheduleExecutionEntity();
        execution.setId(UUID.randomUUID().toString());
        execution.setScheduleId(schedule.getId());
        execution.setExecutionTime(LocalDateTime.now());
        execution.setStatus(failed 
                ? ScheduleExecutionEntity.ExecutionStatusEnum.FAILED 
                : ScheduleExecutionEntity.ExecutionStatusEnum.SUCCESS);
        execution.setReportId(reportId);
        execution.setErrorMessage(errorMessage);
        execution.setDuration(ChronoUnit.MILLIS.between(LocalDateTime.now(), LocalDateTime.now()));
        execution.setManual(false);
        
        executionRepository.save(execution);
    }
    
    /**
     * 手动执行计划
     */
    @Transactional
    public String manualExecuteSchedule(String scheduleId, String triggeredBy) {
        ReportSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));
        
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            // 生成报表
            String reportId = reportGenerationService.createReport(
                    schedule.getName() + " - Manual - " + startTime.toString(),
                    schedule.getTemplateId(),
                    triggeredBy,
                    schedule.getParameters());
            
            // 记录上次执行，但不更新下次执行时间
            schedule.updateExecution(startTime, reportId);
            scheduleRepository.save(schedule);
            
            // 保存执行记录
            ScheduleExecutionEntity execution = new ScheduleExecutionEntity();
            execution.setId(UUID.randomUUID().toString());
            execution.setScheduleId(schedule.getId());
            execution.setExecutionTime(startTime);
            execution.setStatus(ScheduleExecutionEntity.ExecutionStatusEnum.SUCCESS);
            execution.setReportId(reportId);
            execution.setDuration(ChronoUnit.MILLIS.between(startTime, LocalDateTime.now()));
            execution.setTriggeredBy(triggeredBy);
            execution.setManual(true);
            
            executionRepository.save(execution);
            
            log.info("Schedule manually executed successfully: {}, reportId: {}", schedule.getId(), reportId);
            return reportId;
        } catch (Exception e) {
            log.error("Failed to manually execute schedule {}", schedule.getId(), e);
            
            // 保存执行记录
            ScheduleExecutionEntity execution = new ScheduleExecutionEntity();
            execution.setId(UUID.randomUUID().toString());
            execution.setScheduleId(schedule.getId());
            execution.setExecutionTime(startTime);
            execution.setStatus(ScheduleExecutionEntity.ExecutionStatusEnum.FAILED);
            execution.setErrorMessage(e.getMessage());
            execution.setDuration(ChronoUnit.MILLIS.between(startTime, LocalDateTime.now()));
            execution.setTriggeredBy(triggeredBy);
            execution.setManual(true);
            
            executionRepository.save(execution);
            
            throw new ScheduleExecutionException("Failed to execute schedule: " + e.getMessage(), e);
        }
    }
}
