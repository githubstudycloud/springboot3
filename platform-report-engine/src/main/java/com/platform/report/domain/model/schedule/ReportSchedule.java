package com.platform.report.domain.model.schedule;

import com.platform.report.domain.model.common.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 报表计划聚合根
 * 定义报表的定时生成计划
 */
@Getter
public class ReportSchedule implements AggregateRoot<String> {
    
    private final String id;
    private String name;
    private String description;
    private String templateId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ScheduleRecurrence recurrence;
    private Map<String, Object> parameters;
    private ScheduleStatus status;
    private LocalDateTime nextExecutionTime;
    private LocalDateTime lastExecutionTime;
    private String lastReportId;
    private boolean sendNotification;
    
    /**
     * 创建新的报表计划
     */
    public static ReportSchedule create(String name, String description, String templateId, 
                                      String createdBy, ScheduleRecurrence recurrence, 
                                      Map<String, Object> parameters, boolean sendNotification) {
        ReportSchedule schedule = new ReportSchedule();
        schedule.name = name;
        schedule.description = description;
        schedule.templateId = templateId;
        schedule.createdBy = createdBy;
        schedule.createdAt = LocalDateTime.now();
        schedule.updatedAt = LocalDateTime.now();
        schedule.recurrence = recurrence;
        schedule.parameters = parameters != null ? parameters : new HashMap<>();
        schedule.status = ScheduleStatus.ACTIVE;
        schedule.nextExecutionTime = recurrence.calculateNextExecutionTime(LocalDateTime.now());
        schedule.sendNotification = sendNotification;
        
        return schedule;
    }
    
    /**
     * 更新计划执行信息
     */
    public void updateExecution(LocalDateTime executionTime, String reportId) {
        this.lastExecutionTime = executionTime;
        this.lastReportId = reportId;
        this.nextExecutionTime = this.recurrence.calculateNextExecutionTime(executionTime);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 暂停计划
     */
    public void pause() {
        if (this.status == ScheduleStatus.ACTIVE) {
            this.status = ScheduleStatus.PAUSED;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 恢复计划
     */
    public void resume() {
        if (this.status == ScheduleStatus.PAUSED) {
            this.status = ScheduleStatus.ACTIVE;
            this.nextExecutionTime = this.recurrence.calculateNextExecutionTime(LocalDateTime.now());
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 取消计划
     */
    public void cancel() {
        this.status = ScheduleStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新计划配置
     */
    public void updateConfiguration(String name, String description, 
                                  ScheduleRecurrence recurrence, 
                                  Map<String, Object> parameters,
                                  boolean sendNotification) {
        this.name = name;
        this.description = description;
        this.recurrence = recurrence;
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.nextExecutionTime = recurrence.calculateNextExecutionTime(LocalDateTime.now());
        this.sendNotification = sendNotification;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    // 私有构造函数
    private ReportSchedule() {
        this.id = UUID.randomUUID().toString();
        this.parameters = new HashMap<>();
    }
}
