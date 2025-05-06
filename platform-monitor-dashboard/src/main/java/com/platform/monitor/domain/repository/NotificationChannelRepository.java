package com.platform.monitor.domain.repository;

import com.platform.monitor.domain.model.NotificationChannel;
import com.platform.monitor.domain.model.NotificationChannelType;

import java.util.List;
import java.util.Optional;

/**
 * 通知渠道仓储接口
 */
public interface NotificationChannelRepository {
    
    /**
     * 保存通知渠道
     *
     * @param notificationChannel 通知渠道
     * @return 保存后的通知渠道
     */
    NotificationChannel save(NotificationChannel notificationChannel);
    
    /**
     * 根据ID查找通知渠道
     *
     * @param id 通知渠道ID
     * @return 通知渠道可选结果
     */
    Optional<NotificationChannel> findById(String id);
    
    /**
     * 根据类型查找通知渠道
     *
     * @param type 通知渠道类型
     * @return 通知渠道列表
     */
    List<NotificationChannel> findByType(NotificationChannelType type);
    
    /**
     * 获取所有通知渠道
     *
     * @return 所有通知渠道列表
     */
    List<NotificationChannel> findAll();
    
    /**
     * 获取所有启用的通知渠道
     *
     * @return 启用的通知渠道列表
     */
    List<NotificationChannel> findAllEnabled();
    
    /**
     * 删除通知渠道
     *
     * @param id 通知渠道ID
     */
    void deleteById(String id);
    
    /**
     * 根据名称查找通知渠道
     *
     * @param name 通知渠道名称
     * @return 通知渠道可选结果
     */
    Optional<NotificationChannel> findByName(String name);
    
    /**
     * 根据类型获取启用的通知渠道
     *
     * @param type 通知渠道类型
     * @return 启用的通知渠道列表
     */
    List<NotificationChannel> findEnabledByType(NotificationChannelType type);
    
    /**
     * 获取通知渠道数量
     *
     * @return 通知渠道数量
     */
    long count();
    
    /**
     * 获取启用的通知渠道数量
     *
     * @return 启用的通知渠道数量
     */
    long countEnabled();
    
    /**
     * 根据多个ID批量查找通知渠道
     *
     * @param ids ID集合
     * @return 通知渠道列表
     */
    List<NotificationChannel> findByIds(List<String> ids);
}
