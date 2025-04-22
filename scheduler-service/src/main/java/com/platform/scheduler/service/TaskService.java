package com.platform.scheduler.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.Task;

/**
 * 任务服务接口
 * 
 * @author platform
 */
public interface TaskService {
    
    /**
     * 创建任务
     * 
     * @param task 任务对象
     * @return 创建后的任务
     */
    Task createTask(Task task);
    
    /**
     * 更新任务
     * 
     * @param task 任务对象
     * @return 更新后的任务
     */
    Task updateTask(Task task);
    
    /**
     * 根据ID查询任务
     * 
     * @param taskId 任务ID
     * @return 任务对象
     */
    Task getTaskById(Long taskId);
    
    /**
     * 删除任务
     * 
     * @param taskId 任务ID
     * @return 是否删除成功
     */
    boolean deleteTask(Long taskId);
    
    /**
     * 启用任务
     * 
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    Task enableTask(Long taskId);
    
    /**
     * 禁用任务
     * 
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    Task disableTask(Long taskId);
    
    /**
     * 暂停任务
     * 
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    Task pauseTask(Long taskId);
    
    /**
     * 恢复任务
     * 
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    Task resumeTask(Long taskId);
    
    /**
     * 手动触发任务
     * 
     * @param taskId 任务ID
     * @return 执行ID
     */
    Long triggerTask(Long taskId);
    
    /**
     * 分页查询任务
     * 
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findTasks(Pageable pageable);
    
    /**
     * 根据状态分页查询任务
     * 
     * @param status 任务状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findTasksByStatus(String status, Pageable pageable);
    
    /**
     * 根据名称模糊查询任务
     * 
     * @param name 任务名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findTasksByName(String name, Pageable pageable);
    
    /**
     * 根据状态和名称模糊查询任务
     * 
     * @param status 任务状态
     * @param name 任务名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findTasksByStatusAndName(String status, String name, Pageable pageable);
    
    /**
     * 根据类型查询任务
     * 
     * @param type 任务类型
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findTasksByType(String type, Pageable pageable);
    
    /**
     * 根据状态和类型查询任务
     * 
     * @param status 任务状态
     * @param type 任务类型
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findTasksByStatusAndType(String status, String type, Pageable pageable);
    
    /**
     * 根据状态、类型和名称模糊查询任务
     * 
     * @param status 任务状态
     * @param type 任务类型
     * @param name 任务名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findTasksByStatusAndTypeAndName(String status, String type, String name, Pageable pageable);
    
    /**
     * 查询待执行的任务
     * 
     * @return 待执行的任务列表
     */
    List<Task> findTasksDueForExecution();
    
    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 任务状态
     * @return 是否更新成功
     */
    boolean updateTaskStatus(Long taskId, String status);
    
    /**
     * 更新任务下次执行时间
     * 
     * @param taskId 任务ID
     * @param nextExecutionTime 下次执行时间
     * @return 是否更新成功
     */
    boolean updateNextExecutionTime(Long taskId, Date nextExecutionTime);
    
    /**
     * 计算任务下次执行时间
     * 
     * @param task 任务对象
     * @return 下次执行时间
     */
    Date calculateNextExecutionTime(Task task);
}
