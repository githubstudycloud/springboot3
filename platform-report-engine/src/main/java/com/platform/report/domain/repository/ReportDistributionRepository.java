package com.platform.report.domain.repository;

import com.platform.report.domain.model.distribution.DistributionStatus;
import com.platform.report.domain.model.distribution.ReportDistribution;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 报表分发仓储接口
 * 定义报表分发的存储和检索方法
 */
public interface ReportDistributionRepository {
    
    /**
     * 保存报表分发
     *
     * @param distribution 报表分发
     * @return 保存后的报表分发
     */
    ReportDistribution save(ReportDistribution distribution);
    
    /**
     * 根据ID查找报表分发
     *
     * @param id 分发ID
     * @return 包装的报表分发
     */
    Optional<ReportDistribution> findById(String id);
    
    /**
     * 查找所有报表分发
     *
     * @return 报表分发列表
     */
    List<ReportDistribution> findAll();
    
    /**
     * 根据报表ID查找报表分发
     *
     * @param reportId 报表ID
     * @return 报表分发列表
     */
    List<ReportDistribution> findByReportId(String reportId);
    
    /**
     * 根据报表版本ID查找报表分发
     *
     * @param reportVersionId 报表版本ID
     * @return 报表分发列表
     */
    List<ReportDistribution> findByReportVersionId(String reportVersionId);
    
    /**
     * 根据创建者查找报表分发
     *
     * @param createdBy 创建者
     * @return 报表分发列表
     */
    List<ReportDistribution> findByCreatedBy(String createdBy);
    
    /**
     * 根据状态查找报表分发
     *
     * @param status 分发状态
     * @return 报表分发列表
     */
    List<ReportDistribution> findByStatus(DistributionStatus status);
    
    /**
     * 根据创建时间范围查找报表分发
     *
     * @param fromTime 开始时间
     * @param toTime 结束时间
     * @return 报表分发列表
     */
    List<ReportDistribution> findByCreatedAtBetween(LocalDateTime fromTime, LocalDateTime toTime);
    
    /**
     * 根据最后分发时间范围查找报表分发
     *
     * @param fromTime 开始时间
     * @param toTime 结束时间
     * @return 报表分发列表
     */
    List<ReportDistribution> findByLastDistributionTimeBetween(LocalDateTime fromTime, LocalDateTime toTime);
    
    /**
     * 根据名称模糊查找报表分发
     *
     * @param nameLike 名称关键字
     * @return 报表分发列表
     */
    List<ReportDistribution> findByNameLike(String nameLike);
    
    /**
     * 删除报表分发
     *
     * @param id 分发ID
     */
    void delete(String id);
}
