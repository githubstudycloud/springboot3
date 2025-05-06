package com.platform.report.infrastructure.persistence.repository;

import com.platform.report.infrastructure.persistence.entity.ReportTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 报表模板JPA仓储
 */
@Repository
public interface ReportTemplateJpaRepository extends JpaRepository<ReportTemplateEntity, String>, 
                                                    JpaSpecificationExecutor<ReportTemplateEntity> {
    
    /**
     * 根据状态查找
     */
    List<ReportTemplateEntity> findByStatus(ReportTemplateEntity.TemplateStatusEnum status);
    
    /**
     * 根据创建者查找
     */
    List<ReportTemplateEntity> findByCreatedBy(String createdBy);
    
    /**
     * 根据数据源ID查找
     */
    List<ReportTemplateEntity> findByDataSourceId(String dataSourceId);
    
    /**
     * 根据数据集ID查找
     */
    List<ReportTemplateEntity> findByDataSetId(String dataSetId);
    
    /**
     * 根据名称模糊查找
     */
    List<ReportTemplateEntity> findByNameContainingIgnoreCase(String nameLike);
}
