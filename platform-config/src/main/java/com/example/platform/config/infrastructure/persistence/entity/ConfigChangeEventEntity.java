package com.example.platform.config.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 配置变更事件数据库实体
 */
@Entity
@Table(name = "t_config_change_event", 
       indexes = {
           @Index(name = "idx_data_id_group_env", columnList = "data_id, group_name, environment"),
           @Index(name = "idx_occurred_time", columnList = "occurred_time")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 配置项dataId
     */
    @Column(name = "data_id", nullable = false, length = 200)
    private String dataId;
    
    /**
     * 配置项分组
     */
    @Column(name = "group_name", nullable = false, length = 100)
    private String group;
    
    /**
     * 环境标识：dev, test, prod等
     */
    @Column(name = "environment", nullable = false, length = 50)
    private String environment;
    
    /**
     * 事件类型：CREATE, UPDATE, DELETE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventTypeEnum eventType;
    
    /**
     * 变更内容摘要
     */
    @Column(name = "change_summary", length = 500)
    private String changeSummary;
    
    /**
     * 事件发生时间
     */
    @Column(name = "occurred_time", nullable = false)
    private LocalDateTime occurredTime;
    
    /**
     * 操作人
     */
    @Column(name = "operator", nullable = false, length = 100)
    private String operator;
    
    /**
     * 事件类型枚举
     */
    public enum EventTypeEnum {
        /**
         * 创建配置
         */
        CREATE,
        
        /**
         * 更新配置
         */
        UPDATE,
        
        /**
         * 删除配置
         */
        DELETE
    }
}
