package com.platform.report.domain.repository;

import com.platform.report.domain.model.report.Report;
import com.platform.report.domain.model.report.ReportStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 报表实例仓储接口
 * 定义报表实例的存储和检索方法
 */
public interface ReportRepository {
    
    /**
     * 保存报表实例
     *
     * @param report 报表实例
     * @return 保存后的报表实例
     */
    Report save(Report report);
    
    /**
     * 根据ID查找报表实例
     *
     * @param id 实例ID
     * @return 包装的报表实例
     */
    Optional<Report> findById(String id);
    
    /**
     * 查找所有报表实例
     *
     * @return 报表实例列表
     */
    List<Report> findAll();
    
    /**
     * 根据模板ID查找报表实例
     *
     * @param templateId 模板ID
     * @return 报表实例列表
     */
    List<Report> findByTemplateId(String templateId);
    
    /**
     * 根据创建者查找报表实例
     *
     * @param createdBy 创建者
     * @return 报表实例列表
     */
    List<Report> findByCreatedBy(String createdBy);
    
    /**
     * 根据状态查找报表实例
     *
     * @param status 报表状态
     * @return 报表实例列表
     */
    List<Report> findByStatus(ReportStatus status);
    
    /**
     * 根据创建时间范围查找报表实例
     *
     * @param fromTime 开始时间
     * @param toTime 结束时间
     * @return 报表实例列表
     */
    List<Report> findByCreatedAtBetween(LocalDateTime fromTime, LocalDateTime toTime);
    
    /**
     * 根据名称模糊查找报表实例
     *
     * @param nameLike 名称关键字
     * @return 报表实例列表
     */
    List<Report> findByNameLike(String nameLike);
    
    /**
     * 删除报表实例
     *
     * @param id 实例ID
     */
    void delete(String id);
}
