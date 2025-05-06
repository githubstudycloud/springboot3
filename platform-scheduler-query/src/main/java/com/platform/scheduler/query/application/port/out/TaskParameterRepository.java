package com.platform.scheduler.query.application.port.out;

import java.util.Map;
import java.util.Set;

/**
 * 任务参数仓储接口
 * 作为输出端口，定义与任务参数存储层的交互契约
 * 
 * @author platform
 */
public interface TaskParameterRepository {

    /**
     * 获取指定任务的参数
     *
     * @param taskInstanceId 任务实例ID
     * @return 参数映射，键为参数名，值为参数值
     */
    Map<String, String> findByTaskInstanceId(String taskInstanceId);
    
    /**
     * 批量获取多个任务的参数
     *
     * @param taskInstanceIds 任务实例ID集合
     * @return 参数映射，键为任务实例ID，值为参数映射
     */
    Map<String, Map<String, String>> findByTaskInstanceIds(Set<String> taskInstanceIds);
    
    /**
     * 按参数值搜索任务
     *
     * @param paramName 参数名
     * @param paramValue 参数值
     * @param maxResults 最大结果数
     * @return 任务ID列表
     */
    java.util.List<String> findTasksByParamValue(String paramName, String paramValue, int maxResults);
    
    /**
     * 分析参数使用情况
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 参数使用统计
     */
    Map<String, Object> analyzeParamUsage(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
}
