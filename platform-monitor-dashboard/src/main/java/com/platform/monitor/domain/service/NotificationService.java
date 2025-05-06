package com.platform.monitor.domain.service;

import com.platform.monitor.domain.model.Alert;
import com.platform.monitor.domain.model.NotificationChannel;

import java.util.List;
import java.util.Map;

/**
 * 通知服务领域服务接口
 */
public interface NotificationService {
    
    /**
     * 发送告警通知
     * 
     * @param alert 告警
     * @param channels 通知渠道列表
     * @return 是否发送成功
     */
    boolean sendAlertNotification(Alert alert, List<NotificationChannel> channels);
    
    /**
     * 发送告警通知
     * 
     * @param alert 告警
     * @param channelIds 通知渠道ID列表
     * @return 是否发送成功
     */
    boolean sendAlertNotification(Alert alert, String... channelIds);
    
    /**
     * 发送告警恢复通知
     * 
     * @param alert 告警
     * @param channels 通知渠道列表
     * @return 是否发送成功
     */
    boolean sendRecoveryNotification(Alert alert, List<NotificationChannel> channels);
    
    /**
     * 创建通知渠道
     * 
     * @param channel 通知渠道
     * @return 创建的通知渠道
     */
    NotificationChannel createNotificationChannel(NotificationChannel channel);
    
    /**
     * 更新通知渠道
     * 
     * @param channel 通知渠道
     * @return 更新后的通知渠道
     */
    NotificationChannel updateNotificationChannel(NotificationChannel channel);
    
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
     * @return 更新后的通知渠道
     */
    NotificationChannel enableNotificationChannel(String channelId);
    
    /**
     * 禁用通知渠道
     * 
     * @param channelId 通知渠道ID
     * @return 更新后的通知渠道
     */
    NotificationChannel disableNotificationChannel(String channelId);
    
    /**
     * 获取通知渠道
     * 
     * @param channelId 通知渠道ID
     * @return 通知渠道
     */
    NotificationChannel getNotificationChannel(String channelId);
    
    /**
     * 获取所有通知渠道
     * 
     * @return 所有通知渠道列表
     */
    List<NotificationChannel> getAllNotificationChannels();
    
    /**
     * 获取启用的通知渠道
     * 
     * @return 启用的通知渠道列表
     */
    List<NotificationChannel> getEnabledNotificationChannels();
    
    /**
     * 测试通知渠道
     * 
     * @param channelId 通知渠道ID
     * @param testMessage 测试消息
     * @return 测试结果（成功/失败消息）
     */
    String testNotificationChannel(String channelId, String testMessage);
    
    /**
     * 构建告警通知内容
     * 
     * @param alert 告警
     * @param templateType 模板类型（如"email", "sms", "webhook"等）
     * @return 告警通知内容
     */
    String buildAlertNotificationContent(Alert alert, String templateType);
    
    /**
     * 构建告警恢复通知内容
     * 
     * @param alert 告警
     * @param templateType 模板类型（如"email", "sms", "webhook"等）
     * @return 告警恢复通知内容
     */
    String buildRecoveryNotificationContent(Alert alert, String templateType);
    
    /**
     * 获取通知发送记录
     * 
     * @param alertId 告警ID
     * @return 通知发送记录（通知渠道ID -> 发送状态）
     */
    Map<String, Boolean> getNotificationRecords(String alertId);
    
    /**
     * 批量发送通知
     * 
     * @param alerts 告警列表
     * @param channelIds 通知渠道ID列表
     * @return 发送成功的告警数量
     */
    int batchSendNotifications(List<Alert> alerts, List<String> channelIds);
}
