package com.platform.monitor.domain.event;

/**
 * 监控事件监听器接口
 */
public interface MonitorEventListener {
    
    /**
     * 处理监控事件
     *
     * @param event 监控事件
     */
    void onEvent(MonitorEvent event);
    
    /**
     * 获取监听器名称
     *
     * @return 监听器名称
     */
    String getName();
    
    /**
     * 判断是否支持处理指定类型的事件
     *
     * @param eventType 事件类型
     * @return 如果支持则返回true，否则返回false
     */
    boolean supports(MonitorEventType eventType);
}
