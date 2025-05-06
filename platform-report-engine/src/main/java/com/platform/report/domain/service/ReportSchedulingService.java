package com.platform.report.domain.service;

import com.platform.report.domain.model.schedule.ReportSchedule;
import com.platform.report.domain.model.schedule.ScheduleRecurrence;

import java.util.List;
import java.util.Map;

/**
 * 报表计划服务接口
 * 定义报表计划的领域服务方法
 */
public interface ReportSchedulingService {
    
    /**
     * 创建新的报表计划
     *
     * @param name 计划名称
     * @param description 计划描述
     * @param templateId 模板ID
     * @param createdBy 创建者
     * @param recurrence 执行周期
     * @param parameters 报表参数
     * @param sendNotification 是否发送通知
     * @return 创建的报表计划
     */
    ReportSchedule createSchedule(String name, String description, String templateId,
                               String createdBy, ScheduleRecurrence recurrence,
                               Map<String, Object> parameters, boolean sendNotification);
    
    /**
     * 更新报表计划
     *
     * @param scheduleId 计划ID
     * @param name 计划名称
     * @param description 计划描述
     * @param recurrence 执行周期
     * @param parameters 报表参数
     * @param sendNotification 是否发送通知
     * @return 更新的报表计划
     */
    ReportSchedule updateSchedule(String scheduleId, String name, String description,
                               ScheduleRecurrence recurrence, Map<String, Object> parameters,
                               boolean sendNotification);
    
    /**
     * 暂停报表计划
     *
     * @param scheduleId 计划ID
     * @return 更新的报表计划
     */
    ReportSchedule pauseSchedule(String scheduleId);
    
    /**
     * 恢复报表计划
     *
     * @param scheduleId 计划ID
     * @return 更新的报表计划
     */
    ReportSchedule resumeSchedule(String scheduleId);
    
    /**
     * 取消报表计划
     *
     * @param scheduleId 计划ID
     * @return 更新的报表计划
     */
    ReportSchedule cancelSchedule(String scheduleId);
    
    /**
     * 手动触发计划执行
     *
     * @param scheduleId 计划ID
     * @return 创建的报表ID
     */
    String triggerScheduleExecution(String scheduleId);
    
    /**
     * 获取待执行的计划
     *
     * @return 计划列表
     */
    List<ReportSchedule> getPendingSchedules();
    
    /**
     * 处理计划执行
     *
     * @param scheduleId 计划ID
     * @return 创建的报表ID
     */
    String processScheduleExecution(String scheduleId);
    
    /**
     * 复制报表计划
     *
     * @param scheduleId 源计划ID
     * @param newName 新计划名称
     * @param createdBy 创建者
     * @return 复制的报表计划
     */
    ReportSchedule copySchedule(String scheduleId, String newName, String createdBy);
    
    /**
     * 获取计划执行历史
     *
     * @param scheduleId 计划ID
     * @return 执行历史列表
     */
    List<Map<String, Object>> getScheduleExecutionHistory(String scheduleId);
}
