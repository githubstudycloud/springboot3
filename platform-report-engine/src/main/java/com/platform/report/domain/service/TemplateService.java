package com.platform.report.domain.service;

import com.platform.report.domain.model.template.ReportTemplate;
import com.platform.report.domain.model.template.TemplateComponent;
import com.platform.report.domain.model.template.TemplateLayout;
import com.platform.report.domain.model.template.TemplateStyle;

import java.util.List;
import java.util.Map;

/**
 * 模板服务接口
 * 定义模板相关的领域服务方法
 */
public interface TemplateService {
    
    /**
     * 创建新模板
     *
     * @param name 模板名称
     * @param description 模板描述
     * @param createdBy 创建者
     * @param templateType 模板类型
     * @param layout 布局配置
     * @param style 样式配置
     * @param dataSourceId 数据源ID
     * @param dataSetId 数据集ID
     * @return 创建的模板
     */
    ReportTemplate createTemplate(String name, String description, String createdBy,
                               String templateType, TemplateLayout layout, 
                               TemplateStyle style, String dataSourceId, String dataSetId);
    
    /**
     * 添加组件到模板
     *
     * @param templateId 模板ID
     * @param componentName 组件名称
     * @param componentType 组件类型
     * @param x 位置X坐标
     * @param y 位置Y坐标
     * @param width 宽度
     * @param height 高度
     * @param properties 组件属性
     * @param dataBinding 数据绑定表达式
     * @return 更新后的模板
     */
    ReportTemplate addComponent(String templateId, String componentName, String componentType,
                            int x, int y, int width, int height,
                            Map<String, Object> properties, String dataBinding);
    
    /**
     * 移除模板组件
     *
     * @param templateId 模板ID
     * @param componentId 组件ID
     * @return 更新后的模板
     */
    ReportTemplate removeComponent(String templateId, String componentId);
    
    /**
     * 更新模板布局
     *
     * @param templateId 模板ID
     * @param layout 布局配置
     * @return 更新后的模板
     */
    ReportTemplate updateLayout(String templateId, TemplateLayout layout);
    
    /**
     * 更新模板样式
     *
     * @param templateId 模板ID
     * @param style 样式配置
     * @return 更新后的模板
     */
    ReportTemplate updateStyle(String templateId, TemplateStyle style);
    
    /**
     * 更新数据源和数据集
     *
     * @param templateId 模板ID
     * @param dataSourceId 数据源ID
     * @param dataSetId 数据集ID
     * @return 更新后的模板
     */
    ReportTemplate updateDataBinding(String templateId, String dataSourceId, String dataSetId);
    
    /**
     * 发布模板
     *
     * @param templateId 模板ID
     * @return 更新后的模板
     */
    ReportTemplate publishTemplate(String templateId);
    
    /**
     * 导出模板
     *
     * @param templateId 模板ID
     * @return 导出的模板数据
     */
    byte[] exportTemplate(String templateId);
    
    /**
     * 导入模板
     *
     * @param templateData 模板数据
     * @param createdBy 创建者
     * @return 导入的模板
     */
    ReportTemplate importTemplate(byte[] templateData, String createdBy);
    
    /**
     * 复制模板
     *
     * @param templateId 源模板ID
     * @param newName 新模板名称
     * @param createdBy 创建者
     * @return 复制的模板
     */
    ReportTemplate copyTemplate(String templateId, String newName, String createdBy);
    
    /**
     * 归档模板
     *
     * @param templateId 模板ID
     * @return 更新后的模板
     */
    ReportTemplate archiveTemplate(String templateId);
    
    /**
     * 验证模板有效性
     *
     * @param templateId 模板ID
     * @return 验证结果，true为有效
     */
    boolean validateTemplate(String templateId);
    
    /**
     * 预览模板
     *
     * @param templateId 模板ID
     * @param parameters 预览参数
     * @return 预览内容
     */
    byte[] previewTemplate(String templateId, Map<String, Object> parameters);
}
