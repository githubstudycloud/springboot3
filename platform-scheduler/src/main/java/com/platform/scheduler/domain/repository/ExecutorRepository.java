package com.platform.scheduler.domain.repository;

import com.platform.scheduler.domain.model.executor.Executor;
import com.platform.scheduler.domain.model.executor.ExecutorId;
import com.platform.scheduler.domain.model.executor.ExecutorStatus;
import com.platform.scheduler.domain.model.executor.ExecutorType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 执行器仓储接口
 * 定义对执行器聚合根的持久化操作
 * 
 * @author platform
 */
public interface ExecutorRepository {
    
    /**
     * 保存执行器
     *
     * @param executor 执行器实体
     * @return 保存后的执行器实体
     */
    Executor save(Executor executor);
    
    /**
     * 根据ID查找执行器
     *
     * @param executorId 执行器ID
     * @return 执行器实体，如果不存在则返回空
     */
    Optional<Executor> findById(ExecutorId executorId);
    
    /**
     * 根据名称查找执行器
     *
     * @param name 执行器名称
     * @return 执行器实体，如果不存在则返回空
     */
    Optional<Executor> findByName(String name);
    
    /**
     * 根据主机和端口查找执行器
     *
     * @param host 主机地址
     * @param port 端口号
     * @return 执行器实体，如果不存在则返回空
     */
    Optional<Executor> findByHostAndPort(String host, Integer port);
    
    /**
     * 获取所有执行器
     *
     * @return 执行器列表
     */
    List<Executor> findAll();
    
    /**
     * 根据状态查找执行器
     *
     * @param status 执行器状态
     * @return 执行器列表
     */
    List<Executor> findByStatus(ExecutorStatus status);
    
    /**
     * 根据类型查找执行器
     *
     * @param type 执行器类型
     * @return 执行器列表
     */
    List<Executor> findByType(ExecutorType type);
    
    /**
     * 根据标签查找执行器
     *
     * @param tag 标签
     * @return 执行器列表
     */
    List<Executor> findByTag(String tag);
    
    /**
     * 根据多个标签查找执行器
     *
     * @param tags 标签集合
     * @return 执行器列表
     */
    List<Executor> findByTags(Set<String> tags);
    
    /**
     * 查找可用的执行器（状态为在线）
     *
     * @return 可用执行器列表
     */
    List<Executor> findAvailableExecutors();
    
    /**
     * 查找最近一段时间内未发送心跳的执行器
     *
     * @param lastHeartbeatBefore 心跳时间阈值
     * @return 可能离线的执行器列表
     */
    List<Executor> findPossiblyOfflineExecutors(LocalDateTime lastHeartbeatBefore);
    
    /**
     * 根据类型查找可用的执行器
     *
     * @param type 执行器类型
     * @return 可用执行器列表
     */
    List<Executor> findAvailableExecutorsByType(ExecutorType type);
    
    /**
     * 查找负载最小的可用执行器
     *
     * @return 负载最小的执行器，如果不存在则返回空
     */
    Optional<Executor> findLeastLoadedExecutor();
    
    /**
     * 查找特定类型中负载最小的可用执行器
     *
     * @param type 执行器类型
     * @return 特定类型中负载最小的执行器，如果不存在则返回空
     */
    Optional<Executor> findLeastLoadedExecutorByType(ExecutorType type);
    
    /**
     * 删除执行器
     *
     * @param executorId 执行器ID
     */
    void delete(ExecutorId executorId);
    
    /**
     * 检查执行器名称是否已存在
     *
     * @param name 执行器名称
     * @return 如果名称已存在，则返回true
     */
    boolean existsByName(String name);
    
    /**
     * 检查指定主机和端口的执行器是否已存在
     *
     * @param host 主机地址
     * @param port 端口号
     * @return 如果已存在，则返回true
     */
    boolean existsByHostAndPort(String host, Integer port);
}
