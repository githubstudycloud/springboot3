package com.platform.report.domain.repository;

import com.platform.report.domain.model.schedule.ReportSchedule;
import com.platform.report.domain.model.schedule.ScheduleStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 报表计划仓储接口
 * 定义报表计划的存储和检索方法
 */
public interface ReportScheduleRepository {
    
    /**
     * 保存报表计划
     *
     * @param schedule 报表计划
     * @return 保存后的报表计划
     */
    ReportSchedule save(ReportSchedule schedule);
    
    /**
     * 根据ID查找报表计划
     *
     * @param id 计划ID
     * @return 包装的报表计划
     */
    Optional<ReportSchedule> findById(String id);
    
    /**
     * 查找所有报表计划
     *
     * @return 报表计划列表
     */
    List<ReportSchedule> findAll();
    
    /**
     * 根据模板ID查找报表计划
     *
     * @param templateId 模板ID
     * @return 报表计划列表
     */
    List<ReportSchedule> findByTemplateId(String templateId);
    
    /**
     * 根据创建者查找报表计划
     *
     * @param createdBy 创建者
     * @return 报表计划列表
     */
    List<ReportSchedule> findByCreatedBy(String createdBy);
    
    /**
     * 根据状态查找报表计划
     *
     * @param status 计划状态
     * @return 报表计划列表
     */
    List<ReportSchedule> findByStatus(ScheduleStatus status);
    
    /**
     * An example of how to find schedules by status
     *
     * @param status 计划状态
     * @return 报表计划列表
     */
    List<ReportSchedule> findByStatusAndNextExecutionTimeBefore(ScheduleStatus status, LocalDateTime time);
    
    /**
     * 根据名称模糊查找报表计划
     *
     * @param nameLike 名称关键字
     * @return 报表计划列表
     */
    List<ReportSchedule> findByNameLike(String nameLike);
    
    /**
     * 删除报表计划
     *
     * @param id 计划ID
     */
    void delete(String id);
}
