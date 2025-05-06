package com.platform.scheduler.register.domain.repository;

import com.platform.scheduler.register.domain.model.template.JobTemplate;
import com.platform.scheduler.register.domain.model.template.JobTemplateId;
import com.platform.scheduler.register.domain.model.template.TemplateCategory;

import java.util.List;
import java.util.Optional;

/**
 * 作业模板仓储接口
 * 定义作业模板的持久化操作
 * 
 * @author platform
 */
public interface JobTemplateRepository {
    
    /**
     * 保存作业模板
     * 
     * @param template 要保存的作业模板
     * @return 保存后的作业模板
     */
    JobTemplate save(JobTemplate template);
    
    /**
     * 根据ID查找作业模板
     * 
     * @param id 模板ID
     * @return 包含查找结果的Optional
     */
    Optional<JobTemplate> findById(JobTemplateId id);
    
    /**
     * 根据模板名称查找作业模板
     * 
     * @param name 模板名称
     * @return 包含查找结果的Optional
     */
    Optional<JobTemplate> findByName(String name);
    
    /**
     * 查找所有可用的作业模板
     * 
     * @return 可用模板列表
     */
    List<JobTemplate> findAllAvailable();
    
    /**
     * 根据分类查找作业模板
     * 
     * @param category 模板分类
     * @return 指定分类的模板列表
     */
    List<JobTemplate> findByCategory(TemplateCategory category);
    
    /**
     * 根据标签查找作业模板
     * 
     * @param label 模板标签
     * @return 包含指定标签的模板列表
     */
    List<JobTemplate> findByLabel(String label);
    
    /**
     * 根据作业类型查找作业模板
     * 
     * @param jobType 作业类型
     * @return 指定作业类型的模板列表
     */
    List<JobTemplate> findByJobType(String jobType);
    
    /**
     * 删除作业模板
     * 
     * @param id 要删除的模板ID
     * @return 是否成功删除
     */
    boolean delete(JobTemplateId id);
    
    /**
     * 检查模板名称是否已存在
     * 
     * @param name 模板名称
     * @return 如果已存在则返回true
     */
    boolean existsByName(String name);
    
    /**
     * 分页查询作业模板
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 模板列表
     */
    List<JobTemplate> findAll(int page, int size);
    
    /**
     * 获取作业模板总数
     * 
     * @return 模板总数
     */
    long count();
    
    /**
     * 搜索作业模板
     * 
     * @param keyword 关键字
     * @return 符合条件的模板列表
     */
    List<JobTemplate> search(String keyword);
}
