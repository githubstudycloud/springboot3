package com.platform.report.domain.model.template;

import com.platform.report.domain.model.common.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 报表模板聚合根
 * 定义报表的基本结构、布局和样式
 */
@Getter
public class ReportTemplate implements AggregateRoot<String> {
    
    private final String id;
    private String name;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TemplateType templateType;
    private TemplateStatus status;
    private TemplateLayout layout;
    private TemplateStyle style;
    private final List<TemplateComponent> components = new ArrayList<>();
    private String dataSourceId; // 关联数据可视化模块的数据源ID
    private String dataSetId;    // 关联数据可视化模块的数据集ID

    /**
     * 创建新的报表模板
     */
    public static ReportTemplate create(String name, String description, String createdBy, 
                                       TemplateType templateType, TemplateLayout layout, 
                                       TemplateStyle style, String dataSourceId, String dataSetId) {
        ReportTemplate template = new ReportTemplate();
        template.name = name;
        template.description = description;
        template.createdBy = createdBy;
        template.createdAt = LocalDateTime.now();
        template.updatedAt = LocalDateTime.now();
        template.templateType = templateType;
        template.status = TemplateStatus.DRAFT;
        template.layout = layout;
        template.style = style;
        template.dataSourceId = dataSourceId;
        template.dataSetId = dataSetId;
        
        return template;
    }
    
    /**
     * 添加组件到模板
     */
    public void addComponent(TemplateComponent component) {
        this.components.add(component);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 移除组件
     */
    public void removeComponent(String componentId) {
        this.components.removeIf(component -> component.getId().equals(componentId));
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 发布模板
     */
    public void publish() {
        if (this.status == TemplateStatus.DRAFT) {
            this.status = TemplateStatus.PUBLISHED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("只有草稿状态的模板可以发布");
        }
    }
    
    /**
     * 更新模板基本信息
     */
    public void updateBasicInfo(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新模板布局
     */
    public void updateLayout(TemplateLayout layout) {
        this.layout = layout;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新模板样式
     */
    public void updateStyle(TemplateStyle style) {
        this.style = style;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新数据源和数据集
     */
    public void updateDataBinding(String dataSourceId, String dataSetId) {
        this.dataSourceId = dataSourceId;
        this.dataSetId = dataSetId;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String getId() {
        return id;
    }
    
    // 私有构造函数，强制通过静态工厂方法创建
    private ReportTemplate() {
        this.id = UUID.randomUUID().toString();
    }
}
