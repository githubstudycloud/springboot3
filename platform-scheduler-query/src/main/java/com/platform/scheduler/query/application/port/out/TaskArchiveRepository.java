package com.platform.scheduler.query.application.port.out;

import java.time.LocalDateTime;

/**
 * 任务归档仓储接口
 * 作为输出端口，定义与任务归档存储层的交互契约
 * 
 * @author platform
 */
public interface TaskArchiveRepository {

    /**
     * 归档指定日期之前的任务
     *
     * @param cutoffTime 截止时间
     * @return 归档的记录数
     */
    int archiveTasksBeforeDate(LocalDateTime cutoffTime);
    
    /**
     * 从归档中查询任务
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param maxResults 最大结果数
     * @return 归档任务列表
     */
    java.util.List<java.util.Map<String, Object>> queryArchivedTasks(
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            int maxResults);
    
    /**
     * 清理指定日期之前的归档数据
     *
     * @param cutoffTime 截止时间
     * @return 清理的记录数
     */
    int purgeArchivedDataBeforeDate(LocalDateTime cutoffTime);
    
    /**
     * 获取归档统计信息
     *
     * @return 归档统计信息
     */
    java.util.Map<String, Object> getArchiveStatistics();
}
