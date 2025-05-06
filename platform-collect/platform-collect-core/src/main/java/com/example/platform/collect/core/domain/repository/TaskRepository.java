package com.example.platform.collect.core.domain.repository;

import com.example.platform.collect.core.domain.model.CollectTask;
import com.example.platform.collect.core.domain.model.CollectMode;
import com.example.platform.collect.core.domain.model.TaskStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 采集任务仓储接口
 * 定义采集任务的持久化操作
 */
public interface TaskRepository {
    
    /**
     * 保存采集任务
     *
     * @param task 采集任务
     * @return 保存后的采集任务
     */
    CollectTask save(CollectTask task);
    
    /**
     * 根据ID查找采集任务
     *
     * @param id 任务ID
     * @return 采集任务，如果不存在则返回空
     */
    Optional<CollectTask> findById(String id);
    
    /**
     * 根据名称查找采集任务
     *
     * @param name 任务名称
     * @return 采集任务，如果不存在则返回空
     */
    Optional<CollectTask> findByName(String name);
    
    /**
     * 查找所有采集任务
     *
     * @return 采集任务列表
     */
    List<CollectTask> findAll();
    
    /**
     * 根据条件查询采集任务
     *
     * @param criteria 查询条件
     * @return 采集任务列表
     */
    List<CollectTask> findByCriteria(Map<String, Object> criteria);
    
    /**
     * 根据数据源ID查找采集任务
     *
     * @param dataSourceId 数据源ID
     * @return 采集任务列表
     */
    List<CollectTask> findByDataSourceId(String dataSourceId);
    
    /**
     * 根据标签查找采集任务
     *
     * @param tagKey 标签键
     * @param tagValue 标签值
     * @return 采集任务列表
     */
    List<CollectTask> findByTag(String tagKey, String tagValue);
    
    /**
     * 根据状态查找采集任务
     *
     * @param status 任务状态
     * @return 采集任务列表
     */
    List<CollectTask> findByStatus(TaskStatus status);
    
    /**
     * 根据采集模式查找采集任务
     *
     * @param collectMode 采集模式
     * @return 采集任务列表
     */
    List<CollectTask> findByCollectMode(CollectMode collectMode);
    
    /**
     * 根据创建人查找采集任务
     *
     * @param createdBy 创建人
     * @return 采集任务列表
     */
    List<CollectTask> findByCreatedBy(String createdBy);
    
    /**
     * 删除采集任务
     *
     * @param id 任务ID
     */
    void deleteById(String id);
    
    /**
     * 检查采集任务名称是否已存在
     *
     * @param name 任务名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 获取采集任务计数
     *
     * @return 采集任务总数
     */
    long count();
    
    /**
     * 根据条件获取采集任务计数
     *
     * @param criteria 查询条件
     * @return 符合条件的采集任务数量
     */
    long countByCriteria(Map<String, Object> criteria);
    
    /**
     * 查找依赖于指定任务的任务列表
     *
     * @param taskId 任务ID
     * @return 依赖任务列表
     */
    List<CollectTask> findDependentTasks(String taskId);
    
    /**
     * 分页查询采集任务
     *
     * @param criteria 查询条件
     * @param page 页码
     * @param size 每页大小
     * @return 采集任务列表
     */
    List<CollectTask> findByCriteriaPaged(Map<String, Object> criteria, int page, int size);
}
