package com.platform.report.domain.service;

import com.platform.report.domain.model.report.Report;

import java.util.Map;

/**
 * 报表生成服务接口
 * 定义报表生成的领域服务方法
 */
public interface ReportGenerationService {
    
    /**
     * 创建新的报表实例
     *
     * @param name 报表名称
     * @param templateId 模板ID
     * @param createdBy 创建者
     * @param parameters 报表参数
     * @return 创建的报表实例
     */
    Report createReport(String name, String templateId, String createdBy, Map<String, Object> parameters);
    
    /**
     * 生成报表内容
     *
     * @param reportId 报表ID
     * @return 生成的报表
     */
    Report generateReport(String reportId);
    
    /**
     * 异步生成报表内容
     *
     * @param reportId 报表ID
     * @return 初始化的报表
     */
    Report generateReportAsync(String reportId);
    
    /**
     * 获取报表生成状态
     *
     * @param reportId 报表ID
     * @return 报表状态
     */
    String getReportGenerationStatus(String reportId);
    
    /**
     * 重新生成报表
     *
     * @param reportId 报表ID
     * @param parameters 新的参数
     * @return 更新的报表
     */
    Report regenerateReport(String reportId, Map<String, Object> parameters);
    
    /**
     * 下载报表内容
     *
     * @param reportId 报表ID
     * @param versionId 版本ID，可选，为空使用当前版本
     * @return 报表二进制内容
     */
    byte[] downloadReport(String reportId, String versionId);
    
    /**
     * 归档报表
     *
     * @param reportId 报表ID
     * @return 更新的报表
     */
    Report archiveReport(String reportId);
    
    /**
     * 删除报表
     *
     * @param reportId 报表ID
     */
    void deleteReport(String reportId);
    
    /**
     * 比较报表版本
     *
     * @param reportId 报表ID
     * @param versionId1 版本1 ID
     * @param versionId2 版本2 ID
     * @return 比较结果
     */
    String compareReportVersions(String reportId, String versionId1, String versionId2);
    
    /**
     * 复制报表
     *
     * @param reportId 源报表ID
     * @param newName 新报表名称
     * @param createdBy 创建者
     * @return 复制的报表
     */
    Report copyReport(String reportId, String newName, String createdBy);
}
