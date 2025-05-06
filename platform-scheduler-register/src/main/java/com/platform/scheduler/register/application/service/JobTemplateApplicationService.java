package com.platform.scheduler.register.application.service;

import com.platform.scheduler.register.application.command.CreateJobTemplateCommand;
import com.platform.scheduler.register.application.command.UpdateJobTemplateCommand;
import com.platform.scheduler.register.application.dto.JobTemplateDTO;
import com.platform.scheduler.register.domain.model.template.JobTemplateId;
import com.platform.scheduler.register.domain.model.template.TemplateCategory;

import java.util.List;

/**
 * 作业模板应用服务接口
 * 定义作业模板相关的业务操作
 * 
 * @author platform
 */
public interface JobTemplateApplicationService {
    
    /**
     * 创建作业模板
     * 
     * @param command 创建模板命令
     * @return 创建的模板DTO
     */
    JobTemplateDTO createTemplate(CreateJobTemplateCommand command);
    
    /**
     * 更新作业模板
     * 
     * @param templateId 模板ID
     * @param command 更新模板命令
     * @return 更新后的模板DTO
     */
    JobTemplateDTO updateTemplate(String templateId, UpdateJobTemplateCommand command);
    
    /**
     * 获取作业模板详情
     * 
     * @param templateId 模板ID
     * @return 模板DTO
     */
    JobTemplateDTO getTemplate(String templateId);
    
    /**
     * 查找所有可用的作业模板
     * 
     * @return 可用模板DTO列表
     */
    List<JobTemplateDTO> findAllAvailableTemplates();
    
    /**
     * 根据分类查找作业模板
     * 
     * @param category 模板分类
     * @return 指定分类的模板DTO列表
     */
    List<JobTemplateDTO> findTemplatesByCategory(TemplateCategory category);
    
    /**
     * 根据标签查找作业模板
     * 
     * @param label 模板标签
     * @return 包含指定标签的模板DTO列表
     */
    List<JobTemplateDTO> findTemplatesByLabel(String label);
    
    /**
     * 发布作业模板
     * 
     * @param templateId 模板ID
     * @param operator 操作者
     * @return 更新后的模板DTO
     */
    JobTemplateDTO publishTemplate(String templateId, String operator);
    
    /**
     * 禁用作业模板
     * 
     * @param templateId 模板ID
     * @param operator 操作者
     * @return 更新后的模板DTO
     */
    JobTemplateDTO disableTemplate(String templateId, String operator);
    
    /**
     * 删除作业模板
     * 
     * @param templateId 模板ID
     * @return 是否成功删除
     */
    boolean deleteTemplate(String templateId);
    
    /**
     * 复制作业模板
     * 
     * @param sourceTemplateId 源模板ID
     * @param newName 新模板名称
     * @param operator 操作者
     * @return 复制的新模板DTO
     */
    JobTemplateDTO copyTemplate(String sourceTemplateId, String newName, String operator);
    
    /**
     * 分页查询作业模板
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 模板DTO列表
     */
    List<JobTemplateDTO> findAllTemplates(int page, int size);
    
    /**
     * 获取模板总数
     * 
     * @return 模板总数
     */
    long countTemplates();
    
    /**
     * 搜索作业模板
     * 
     * @param keyword 关键字
     * @return 符合条件的模板DTO列表
     */
    List<JobTemplateDTO> searchTemplates(String keyword);
    
    /**
     * 从作业模板创建作业
     * 
     * @param templateId 模板ID
     * @param jobName 作业名称
     * @param operator 操作者
     * @return 创建的作业ID
     */
    String createJobFromTemplate(String templateId, String jobName, String operator);
}
