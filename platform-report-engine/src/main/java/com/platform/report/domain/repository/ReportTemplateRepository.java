package com.platform.report.domain.repository;

import com.platform.report.domain.model.template.ReportTemplate;
import com.platform.report.domain.model.template.TemplateStatus;

import java.util.List;
import java.util.Optional;

/**
 * 报表模板仓储接口
 * 定义报表模板的存储和检索方法
 */
public interface ReportTemplateRepository {
    
    /**
     * 保存报表模板
     *
     * @param template 报表模板
     * @return 保存后的报表模板
     */
    ReportTemplate save(ReportTemplate template);
    
    /**
     * 根据ID查找报表模板
     *
     * @param id 模板ID
     * @return 包装的报表模板
     */
    Optional<ReportTemplate> findById(String id);
    
    /**
     * 查找所有报表模板
     *
     * @return 报表模板列表
     */
    List<ReportTemplate> findAll();
    
    /**
     * 根据状态查找报表模板
     *
     * @param status 模板状态
     * @return 报表模板列表
     */
    List<ReportTemplate> findByStatus(TemplateStatus status);
    
    /**
     * 根据创建者查找报表模板
     *
     * @param createdBy 创建者
     * @return 报表模板列表
     */
    List<ReportTemplate> findByCreatedBy(String createdBy);
    
    /**
     * 根据数据源ID查找报表模板
     *
     * @param dataSourceId 数据源ID
     * @return 报表模板列表
     */
    List<ReportTemplate> findByDataSourceId(String dataSourceId);
    
    /**
     * 根据数据集ID查找报表模板
     *
     * @param dataSetId 数据集ID
     * @return 报表模板列表
     */
    List<ReportTemplate> findByDataSetId(String dataSetId);
    
    /**
     * 根据名称模糊查找报表模板
     *
     * @param nameLike 名称关键字
     * @return 报表模板列表
     */
    List<ReportTemplate> findByNameLike(String nameLike);
    
    /**
     * 删除报表模板
     *
     * @param id 模板ID
     */
    void delete(String id);
}
