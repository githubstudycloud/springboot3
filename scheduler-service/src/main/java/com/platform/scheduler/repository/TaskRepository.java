package com.platform.scheduler.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.Task;
import com.platform.scheduler.repository.base.BaseRepository;

/**
 * 任务数据访问层接口
 * 
 * @author platform
 */
public interface TaskRepository extends BaseRepository<Task, Long> {
    
    /**
     * 根据状态查询任务
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    List<Task> findByStatus(String status);
    
    /**
     * 根据状态分页查询任务
     * 
     * @param status 任务状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据名称模糊查询任务
     * 
     * @param name 任务名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findByNameContaining(String name, Pageable pageable);
    
    /**
     * 根据状态和名称模糊查询任务
     * 
     * @param status 任务状态
     * @param name 任务名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findByStatusAndNameContaining(String status, String name, Pageable pageable);
    
    /**
     * 查询待执行的任务
     * 
     * @param currentTime 当前时间
     * @return 待执行的任务列表
     */
    List<Task> findTasksDueForExecution(Date currentTime);
    
    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 任务状态
     * @return 更新行数
     */
    int updateStatus(Long taskId, String status);
    
    /**
     * 更新任务下次执行时间
     * 
     * @param taskId 任务ID
     * @param nextExecutionTime 下次执行时间
     * @return 更新行数
     */
    int updateNextExecutionTime(Long taskId, Date nextExecutionTime);
    
    /**
     * 根据类型查询任务
     * 
     * @param type 任务类型
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findByType(String type, Pageable pageable);
    
    /**
     * 根据状态和类型查询任务
     * 
     * @param status 任务状态
     * @param type 任务类型
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findByStatusAndType(String status, String type, Pageable pageable);
    
    /**
     * 根据状态、类型和名称模糊查询任务
     * 
     * @param status 任务状态
     * @param type 任务类型
     * @param name 任务名称（模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Task> findByStatusAndTypeAndNameContaining(String status, String type, String name, Pageable pageable);
}
