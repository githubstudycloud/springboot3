package com.platform.monitor.application.service;

import com.platform.monitor.application.dto.NotificationChannelDTO;

import java.util.List;
import java.util.Map;

/**
 * 通知管理应用服务接口
 */
public interface NotificationAppService {
    
    /**
     * 创建通知渠道
     *
     * @param channelDTO 通知渠道DTO
     * @return 创建的通知渠道DTO
     */
    NotificationChannelDTO createNotificationChannel(NotificationChannelDTO channelDTO);
    
    /**
     * 更新通知渠道
     *
     * @param channelDTO 通知渠道DTO
     * @return 更新后的通知渠道DTO
     */
    NotificationChannelDTO updateNotificationChannel(NotificationChannelDTO channelDTO);
    
    /**
     * 删除通知渠道
     *
     * @param channelId 通知渠道ID
     * @return 操作是否成功
     */
    boolean deleteNotificationChannel(String channelId);
    
    /**
     * 启用通知渠道
     *
     * @param channelId 通知渠道ID
     * @return 更新后的通知渠道DTO
     */
    NotificationChannelDTO enableNotificationChannel(String channelId);
    
    /**
     * 禁用通知渠道
     *
     * @param channelId 通知渠道ID
     * @return 更新后的通知渠道DTO
     */
    NotificationChannelDTO disableNotificationChannel(String channelId);
    
    /**
     * 获取通知渠道
     *
     * @param channelId 通知渠道ID
     * @return 通知渠道DTO
     */
    NotificationChannelDTO getNotificationChannel(String channelId);
    
    /**
     * 获取所有通知渠道
     *
     * @return 所有通知渠道DTO列表
     */
    List<NotificationChannelDTO> getAllNotificationChannels();
    
    /**
     * 获取启用的通知渠道
     *
     * @return 启用的通知渠道DTO列表
     */
    List<NotificationChannelDTO> getEnabledNotificationChannels();
    
    /**
     * 获取指定类型的通知渠道
     *
     * @param type 通知渠道类型
     * @return 通知渠道DTO列表
     */
    List<NotificationChannelDTO> getNotificationChannelsByType(String type);
    
    /**
     * 测试通知渠道
     *
     * @param channelId 通知渠道ID
     * @param testMessage 测试消息
     * @return 测试结果（成功/失败消息）
     */
    String testNotificationChannel(String channelId, String testMessage);
    
    /**
     * 获取通知模板
     *
     * @param templateType 模板类型
     * @return 通知模板内容
     */
    String getNotificationTemplate(String templateType);
    
    /**
     * 更新通知模板
     *
     * @param templateType 模板类型
     * @param templateContent 模板内容
     * @return 操作是否成功
     */
    boolean updateNotificationTemplate(String templateType, String templateContent);
    
    /**
     * 获取通知发送统计
     *
     * @return 通知发送统计（通知渠道 -> 成功/失败数量）
     */
    Map<String, Map<String, Integer>> getNotificationSendStatistics();
    
    /**
     * 获取可用的通知渠道类型
     *
     * @return 通知渠道类型列表
     */
    List<Map<String, String>> getAvailableChannelTypes();
    
    /**
     * 验证通知渠道配置
     *
     * @param channelDTO 通知渠道DTO
     * @return 验证结果（成功/失败消息）
     */
    String validateChannelConfig(NotificationChannelDTO channelDTO);
    
    /**
     * 发送自定义通知
     *
     * @param channelIds 通知渠道ID列表
     * @param subject 主题
     * @param content 内容
     * @return 发送状态（通知渠道ID -> 发送状态）
     */
    Map<String, Boolean> sendCustomNotification(List<String> channelIds, String subject, String content);
    
    /**
     * 获取通知历史记录
     *
     * @param limit 限制数量
     * @return 通知历史记录
     */
    List<Map<String, Object>> getNotificationHistory(int limit);
    
    /**
     * 重发失败的通知
     *
     * @param notificationId 通知ID
     * @return 操作是否成功
     */
    boolean resendFailedNotification(String notificationId);
}
