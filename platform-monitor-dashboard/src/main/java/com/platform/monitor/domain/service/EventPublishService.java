package com.platform.monitor.domain.service;

import com.platform.monitor.domain.event.MonitorEvent;
import com.platform.monitor.domain.event.MonitorEventListener;
import com.platform.monitor.domain.event.MonitorEventType;

import java.util.List;

/**
 * 事件发布服务接口
 */
public interface EventPublishService {
    
    /**
     * 发布事件
     *
     * @param event 事件
     */
    void publishEvent(MonitorEvent event);
    
    /**
     * 注册事件监听器
     *
     * @param listener 事件监听器
     */
    void registerListener(MonitorEventListener listener);
    
    /**
     * 注销事件监听器
     *
     * @param listenerName 事件监听器名称
     * @return 操作是否成功
     */
    boolean unregisterListener(String listenerName);
    
    /**
     * 获取指定类型事件的所有监听器
     *
     * @param eventType 事件类型
     * @return 事件监听器列表
     */
    List<MonitorEventListener> getListeners(MonitorEventType eventType);
    
    /**
     * 获取所有事件监听器
     *
     * @return 所有事件监听器列表
     */
    List<MonitorEventListener> getAllListeners();
    
    /**
     * 同步发布事件并等待处理完成
     *
     * @param event 事件
     */
    void publishEventSync(MonitorEvent event);
    
    /**
     * 发布事件到主题
     *
     * @param event 事件
     * @param topic 主题
     */
    void publishEventToTopic(MonitorEvent event, String topic);
    
    /**
     * 批量发布事件
     *
     * @param events 事件列表
     */
    void publishEvents(List<MonitorEvent> events);
    
    /**
     * 注册全局事件监听器
     *
     * @param listener 事件监听器
     */
    void registerGlobalListener(MonitorEventListener listener);
}
