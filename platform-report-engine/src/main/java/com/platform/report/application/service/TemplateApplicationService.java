package com.platform.report.application.service;

import com.platform.report.application.dto.template.*;

import java.util.List;
import java.util.Map;

/**
 * 模板应用服务接口
 * 提供模板相关功能的应用层服务
 */
public interface TemplateApplicationService {
    
    /**
     * 创建报表模板
     *
     * @param command 创建命令
     * @return 模板ID
     */
    String createTemplate(CreateTemplateCommand command);
    
    /**
     * 获取模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    TemplateDetailDTO getTemplateDetail(String id);
    
    /**
     * 获取模板列表
     *
     * @param query 查询参数
     * @return 模板列表
     */
    List<TemplateDTO> getTemplateList(TemplateQueryDTO query);
    
    /**
     * 更新模板基本信息
     *
     * @param command 更新命令
     * @return 是否成功
     */
    boolean updateTemplateBasicInfo(UpdateTemplateBasicInfoCommand command);
    
    /**
     * 更新模板布局
     *
     * @param command 更新命令
     * @return 是否成功
     */
    boolean updateTemplateLayout(UpdateTemplateLayoutCommand command);
    
    /**
     * 更新模板样式
     *
     * @param command 更新命令
     * @return 是否成功
     */
    boolean updateTemplateStyle(UpdateTemplateStyleCommand command);
    
    /**
     * 添加模板组件
     *
     * @param command 添加命令
     * @return 组件ID
     */
    String addTemplateComponent(AddTemplateComponentCommand command);
    
    /**
     * 更新模板组件
     *
     * @param command 更新命令
     * @return 是否成功
     */
    boolean updateTemplateComponent(UpdateTemplateComponentCommand command);
    
    /**
     * 删除模板组件
     *
     * @param templateId 模板ID
     * @param componentId 组件ID
     * @return 是否成功
     */
    boolean deleteTemplateComponent(String templateId, String componentId);
    
    /**
     * 发布模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    boolean publishTemplate(String templateId);
    
    /**
     * 归档模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    boolean archiveTemplate(String templateId);
    
    /**
     * 删除模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    boolean deleteTemplate(String templateId);
    
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
     * @param data 模板数据
     * @param createdBy 创建者
     * @return 导入的模板ID
     */
    String importTemplate(byte[] data, String createdBy);
    
    /**
     * 复制模板
     *
     * @param templateId 源模板ID
     * @param newName 新模板名称
     * @param createdBy 创建者
     * @return 新模板ID
     */
    String copyTemplate(String templateId, String newName, String createdBy);
    
    /**
     * 预览模板
     *
     * @param templateId 模板ID
     * @param parameters 预览参数
     * @return 预览内容
     */
    byte[] previewTemplate(String templateId, Map<String, Object> parameters);
    
    /**
     * 获取模板参数列表
     *
     * @param templateId 模板ID
     * @return 参数列表
     */
    List<TemplateParameterDTO> getTemplateParameters(String templateId);
}
