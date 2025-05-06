package com.platform.report.application.service;

import com.platform.report.application.dto.schedule.*;

import java.util.List;

/**
 * 报表计划应用服务接口
 * 提供报表计划相关功能的应用层服务
 */
public interface ScheduleApplicationService {
    
    /**
     * 创建报表计划
     *
     * @param command 创建命令
     * @return 计划ID
     */
    String createSchedule(CreateScheduleCommand command);
    
    /**
     * 获取计划详情
     *
     * @param id 计划ID
     * @return 计划详情
     */
    ScheduleDetailDTO getScheduleDetail(String id);
    
    /**
     * 获取计划列表
     *
     * @param query 查询参数
     * @return 计划列表
     */
    List<ScheduleDTO> getScheduleList(ScheduleQueryDTO query);
    
    /**
     * 更新计划配置
     *
     * @param command 更新命令
     * @return 是否成功
     */
    boolean updateSchedule(UpdateScheduleCommand command);
    
    /**
     * 暂停计划
     *
     * @param scheduleId 计划ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean pauseSchedule(String scheduleId, String operatedBy);
    
    /**
     * 恢复计划
     *
     * @param scheduleId 计划ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean resumeSchedule(String scheduleId, String operatedBy);
    
    /**
     * 取消计划
     *
     * @param scheduleId 计划ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean cancelSchedule(String scheduleId, String operatedBy);
    
    /**
     * 手动触发计划执行
     *
     * @param scheduleId 计划ID
     * @param operatedBy 操作者
     * @return 创建的报表ID
     */
    String triggerScheduleExecution(String scheduleId, String operatedBy);
    
    /**
     * 获取计划执行历史
     *
     * @param scheduleId 计划ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 执行历史列表
     */
    List<ScheduleExecutionDTO> getScheduleExecutionHistory(String scheduleId, int pageNum, int pageSize);
    
    /**
     * 获取最近执行历史
     *
     * @param count 数量
     * @return 执行历史列表
     */
    List<ScheduleExecutionDTO> getRecentExecutions(int count);
    
    /**
     * 删除计划
     *
     * @param scheduleId 计划ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean deleteSchedule(String scheduleId, String operatedBy);
    
    /**
     * 复制计划
     *
     * @param scheduleId 源计划ID
     * @param newName 新计划名称
     * @param createdBy 创建者
     * @return 新计划ID
     */
    String copySchedule(String scheduleId, String newName, String createdBy);
}
