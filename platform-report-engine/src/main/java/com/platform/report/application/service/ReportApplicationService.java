package com.platform.report.application.service;

import com.platform.report.application.dto.report.*;

import java.util.List;
import java.util.Map;

/**
 * 报表应用服务接口
 * 提供报表相关功能的应用层服务
 */
public interface ReportApplicationService {
    
    /**
     * 创建报表
     *
     * @param command 创建命令
     * @return 报表ID
     */
    String createReport(CreateReportCommand command);
    
    /**
     * 获取报表详情
     *
     * @param id 报表ID
     * @return 报表详情
     */
    ReportDetailDTO getReportDetail(String id);
    
    /**
     * 获取报表列表
     *
     * @param query 查询参数
     * @return 报表列表
     */
    List<ReportDTO> getReportList(ReportQueryDTO query);
    
    /**
     * 生成报表
     *
     * @param reportId 报表ID
     * @return 是否成功
     */
    boolean generateReport(String reportId);
    
    /**
     * 异步生成报表
     *
     * @param reportId 报表ID
     * @return 任务ID
     */
    String generateReportAsync(String reportId);
    
    /**
     * 获取生成状态
     *
     * @param reportId 报表ID
     * @return 生成状态
     */
    ReportGenerationStatusDTO getGenerationStatus(String reportId);
    
    /**
     * 重新生成报表
     *
     * @param command 重新生成命令
     * @return 是否成功
     */
    boolean regenerateReport(RegenerateReportCommand command);
    
    /**
     * 下载报表
     *
     * @param reportId 报表ID
     * @param versionId 版本ID，可选
     * @param downloadBy 下载者
     * @return 报表二进制内容及元数据
     */
    ReportContentDTO downloadReport(String reportId, String versionId, String downloadBy);
    
    /**
     * 获取报表版本列表
     *
     * @param reportId 报表ID
     * @return 版本列表
     */
    List<ReportVersionDTO> getReportVersions(String reportId);
    
    /**
     * 比较报表版本
     *
     * @param reportId 报表ID
     * @param versionId1 版本1 ID
     * @param versionId2 版本2 ID
     * @return 比较结果
     */
    ReportVersionComparisonDTO compareReportVersions(String reportId, String versionId1, String versionId2);
    
    /**
     * 删除报表
     *
     * @param reportId 报表ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean deleteReport(String reportId, String operatedBy);
    
    /**
     * 复制报表
     *
     * @param reportId 源报表ID
     * @param newName 新报表名称
     * @param createdBy 创建者
     * @return 新报表ID
     */
    String copyReport(String reportId, String newName, String createdBy);
}
