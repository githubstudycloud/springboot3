package com.platform.monitor.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 监控事件基类
 */
@Getter
public abstract class MonitorEvent {
    
    /**
     * 事件ID
     */
    private final String id;
    
    /**
     * 事件类型
     */
    private final MonitorEventType eventType;
    
    /**
     * 事件发生时间
     */
    private final LocalDateTime timestamp;
    
    /**
     * 事件来源
     */
    private final String source;
    
    /**
     * 事件数据
     */
    private final Map<String, Object> data;
    
    /**
     * 构造函数
     *
     * @param eventType 事件类型
     * @param source 事件来源
     */
    protected MonitorEvent(MonitorEventType eventType, String source) {
        this.id = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.source = source;
        this.data = new HashMap<>();
    }
    
    /**
     * 构造函数
     *
     * @param eventType 事件类型
     * @param source 事件来源
     * @param data 事件数据
     */
    protected MonitorEvent(MonitorEventType eventType, String source, Map<String, Object> data) {
        this.id = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.source = source;
        this.data = data != null ? new HashMap<>(data) : new HashMap<>();
    }
    
    /**
     * 添加事件数据
     *
     * @param key 数据键
     * @param value 数据值
     */
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
    
    /**
     * 获取事件数据值
     *
     * @param key 数据键
     * @return 数据值
     */
    public Object getData(String key) {
        return this.data.get(key);
    }
    
    /**
     * 获取事件数据值，如果不存在则返回默认值
     *
     * @param key 数据键
     * @param defaultValue 默认值
     * @return 数据值或默认值
     */
    public Object getData(String key, Object defaultValue) {
        return this.data.getOrDefault(key, defaultValue);
    }
    
    /**
     * 获取事件描述
     *
     * @return 事件描述
     */
    public abstract String getDescription();
}
