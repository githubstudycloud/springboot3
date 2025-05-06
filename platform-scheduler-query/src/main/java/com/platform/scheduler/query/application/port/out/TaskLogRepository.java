package com.platform.scheduler.query.application.port.out;

import com.platform.scheduler.domain.model.task.TaskLogEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 任务日志仓储接口
 * 作为输出端口，定义与任务日志存储层的交互契约
 * 
 * @author platform
 */
public interface TaskLogRepository {

    /**
     * 获取指定任务的日志条目
     *
     * @param taskInstanceId 任务实例ID
     * @return 日志条目列表
     */
    List<TaskLogEntry> findByTaskInstanceId(String taskInstanceId);
    
    /**
     * 批量获取多个任务的日志条目
     *
     * @param taskInstanceIds 任务实例ID集合
     * @return 日志条目映射，键为任务实例ID，值为日志条目列表
     */
    Map<String, List<TaskLogEntry>> findByTaskInstanceIds(Set<String> taskInstanceIds);
    
    /**
     * 获取指定任务的日志
     *
     * @param taskInstanceId 任务实例ID
     * @return 日志数据列表
     */
    List<Map<String, Object>> getTaskLogs(String taskInstanceId);
    
    /**
     * 按日志级别获取指定任务的日志
     *
     * @param taskInstanceId 任务实例ID
     * @param level 日志级别
     * @return 日志数据列表
     */
    List<Map<String, Object>> getTaskLogsByLevel(String taskInstanceId, String level);
    
    /**
     * 搜索任务日志
     *
     * @param taskInstanceId 任务实例ID
     * @param keyword 关键字
     * @return 匹配的日志数据列表
     */
    List<Map<String, Object>> searchTaskLogs(String taskInstanceId, String keyword);
    
    /**
     * 获取最近的错误日志
     *
     * @param limit 最大记录数
     * @return 错误日志数据列表
     */
    List<Map<String, Object>> getRecentErrorLogs(int limit);
    
    /**
     * 获取任务日志统计信息
     *
     * @param taskInstanceId 任务实例ID
     * @return 日志统计信息
     */
    Map<String, Long> getTaskLogStats(String taskInstanceId);
}
