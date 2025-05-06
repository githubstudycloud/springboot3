package com.platform.scheduler.register.application.service;

import com.platform.scheduler.register.application.command.CreateDependencyCommand;
import com.platform.scheduler.register.application.command.UpdateDependencyCommand;
import com.platform.scheduler.register.application.dto.DependencyDefinitionDTO;
import com.platform.scheduler.register.domain.model.dependency.DependencyType;

import java.util.List;

/**
 * 依赖关系定义应用服务接口
 * 定义依赖关系相关的业务操作
 * 
 * @author platform
 */
public interface DependencyDefinitionApplicationService {
    
    /**
     * 创建依赖关系
     * 
     * @param command 创建依赖关系命令
     * @return 创建的依赖关系DTO
     */
    DependencyDefinitionDTO createDependency(CreateDependencyCommand command);
    
    /**
     * 更新依赖关系
     * 
     * @param dependencyId 依赖关系ID
     * @param command 更新依赖关系命令
     * @return 更新后的依赖关系DTO
     */
    DependencyDefinitionDTO updateDependency(String dependencyId, UpdateDependencyCommand command);
    
    /**
     * 获取依赖关系详情
     * 
     * @param dependencyId 依赖关系ID
     * @return 依赖关系DTO
     */
    DependencyDefinitionDTO getDependency(String dependencyId);
    
    /**
     * 查找源作业的所有依赖关系
     * 
     * @param sourceJobId 源作业ID
     * @return 依赖关系DTO列表
     */
    List<DependencyDefinitionDTO> findDependenciesBySourceJobId(String sourceJobId);
    
    /**
     * 查找目标作业的所有依赖关系
     * 
     * @param targetJobId 目标作业ID
     * @return 依赖关系DTO列表
     */
    List<DependencyDefinitionDTO> findDependenciesByTargetJobId(String targetJobId);
    
    /**
     * 根据依赖类型查找依赖关系
     * 
     * @param type 依赖类型
     * @return 指定类型的依赖关系DTO列表
     */
    List<DependencyDefinitionDTO> findDependenciesByType(DependencyType type);
    
    /**
     * 启用依赖关系
     * 
     * @param dependencyId 依赖关系ID
     * @param operator 操作者
     * @return 更新后的依赖关系DTO
     */
    DependencyDefinitionDTO enableDependency(String dependencyId, String operator);
    
    /**
     * 禁用依赖关系
     * 
     * @param dependencyId 依赖关系ID
     * @param operator 操作者
     * @return 更新后的依赖关系DTO
     */
    DependencyDefinitionDTO disableDependency(String dependencyId, String operator);
    
    /**
     * 删除依赖关系
     * 
     * @param dependencyId 依赖关系ID
     * @return 是否成功删除
     */
    boolean deleteDependency(String dependencyId);
    
    /**
     * 验证作业依赖关系是否存在循环
     * 
     * @param jobId 作业ID
     * @return 如果存在循环则返回true
     */
    boolean validateCyclicDependency(String jobId);
    
    /**
     * 查找直接依赖于指定作业的所有作业
     * 
     * @param jobId 作业ID
     * @return 依赖于指定作业的作业信息列表
     */
    List<DependencyDefinitionDTO> findDependentJobs(String jobId);
    
    /**
     * 查找指定作业直接依赖的所有作业
     * 
     * @param jobId 作业ID
     * @return 指定作业依赖的作业信息列表
     */
    List<DependencyDefinitionDTO> findDependencyJobs(String jobId);
    
    /**
     * 分页查询依赖关系定义
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 依赖关系DTO列表
     */
    List<DependencyDefinitionDTO> findAllDependencies(int page, int size);
    
    /**
     * 获取依赖关系总数
     * 
     * @return 依赖关系总数
     */
    long countDependencies();
    
    /**
     * 获取作业的依赖关系图
     * 
     * @param jobId 作业ID
     * @param depth 图的深度，最大为5
     * @return 依赖关系图的JSON字符串
     */
    String getDependencyGraph(String jobId, int depth);
}
