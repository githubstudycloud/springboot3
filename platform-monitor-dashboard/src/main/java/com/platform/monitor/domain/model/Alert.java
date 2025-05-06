package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警领域模型
 */
@Getter
public class Alert extends AbstractEntity<String> {
    
    /**
     * 告警ID
     */
    private final String id;
    
    /**
     * 告警名称
     */
    private final String name;
    
    /**
     * 告警描述
     */
    private final String description;
    
    /**
     * 告警级别
     */
    private final AlertSeverity severity;
    
    /**
     * 触发告警的指标
     */
    private final MetricType metricType;
    
    /**
     * 指标值
     */
    private final double metricValue;
    
    /**
     * 指标单位
     */
    private final MetricUnit metricUnit;
    
    /**
     * 告警规则ID
     */
    private final String ruleId;
    
    /**
     * 服务实例ID
     */
    private final String serviceInstanceId;
    
    /**
     * 服务名称
     */
    private final String serviceName;
    
    /**
     * 告警状态
     */
    private AlertStatus status;
    
    /**
     * 首次出现时间
     */
    private final LocalDateTime firstOccurTime;
    
    /**
     * 最近更新时间
     */
    private LocalDateTime lastUpdateTime;
    
    /**
     * 告警处理时间
     */
    private LocalDateTime handledTime;
    
    /**
     * 处理人
     */
    private String handledBy;
    
    /**
     * 处理备注
     */
    private String handlingNote;
    
    /**
     * 告警上下文数据
     */
    private final Map<String, String> context;
    
    /**
     * 告警事件历史
     */
    private final List<AlertEvent> events;
    
    /**
     * 构造函数
     *
     * @param id 告警ID
     * @param name 告警名称
     * @param description 告警描述
     * @param severity 告警级别
     * @param metricType 指标类型
     * @param metricValue 指标值
     * @param metricUnit 指标单位
     * @param ruleId 告警规则ID
     * @param serviceInstanceId 服务实例ID
     * @param serviceName 服务名称
     */
    public Alert(String id, String name, String description, AlertSeverity severity,
                MetricType metricType, double metricValue, MetricUnit metricUnit,
                String ruleId, String serviceInstanceId, String serviceName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.severity = severity;
        this.metricType = metricType;
        this.metricValue = metricValue;
        this.metricUnit = metricUnit;
        this.ruleId = ruleId;
        this.serviceInstanceId = serviceInstanceId;
        this.serviceName = serviceName;
        this.status = AlertStatus.ACTIVE;
        this.firstOccurTime = LocalDateTime.now();
        this.lastUpdateTime = LocalDateTime.now();
        this.context = new HashMap<>();
        this.events = new ArrayList<>();
        
        // 添加告警生成事件
        addEvent(new AlertEvent(
            "CREATED",
            "告警生成",
            "系统",
            String.format("告警触发：%s，指标值：%s", name, getFormattedMetricValue())
        ));
    }
    
    /**
     * 确认告警
     *
     * @param operator 操作人
     * @param note 备注信息
     * @return 当前告警实例
     */
    public Alert acknowledge(String operator, String note) {
        if (this.status == AlertStatus.ACTIVE) {
            this.status = AlertStatus.ACKNOWLEDGED;
            this.lastUpdateTime = LocalDateTime.now();
            this.handledBy = operator;
            this.handlingNote = note;
            
            addEvent(new AlertEvent(
                "ACKNOWLEDGED",
                "告警确认",
                operator,
                String.format("告警已确认：%s", note)
            ));
        }
        return this;
    }
    
    /**
     * 解决告警
     *
     * @param operator 操作人
     * @param note 备注信息
     * @return 当前告警实例
     */
    public Alert resolve(String operator, String note) {
        if (this.status == AlertStatus.ACTIVE || this.status == AlertStatus.ACKNOWLEDGED) {
            this.status = AlertStatus.RESOLVED;
            this.lastUpdateTime = LocalDateTime.now();
            this.handledTime = LocalDateTime.now();
            this.handledBy = operator;
            this.handlingNote = note;
            
            addEvent(new AlertEvent(
                "RESOLVED",
                "告警解决",
                operator,
                String.format("告警已解决：%s", note)
            ));
        }
        return this;
    }
    
    /**
     * 关闭告警
     *
     * @param operator 操作人
     * @param note 备注信息
     * @return 当前告警实例
     */
    public Alert close(String operator, String note) {
        if (this.status != AlertStatus.CLOSED) {
            this.status = AlertStatus.CLOSED;
            this.lastUpdateTime = LocalDateTime.now();
            if (this.handledTime == null) {
                this.handledTime = LocalDateTime.now();
            }
            this.handledBy = operator;
            this.handlingNote = note;
            
            addEvent(new AlertEvent(
                "CLOSED",
                "告警关闭",
                operator,
                String.format("告警已关闭：%s", note)
            ));
        }
        return this;
    }
    
    /**
     * 抑制告警
     *
     * @param operator 操作人
     * @param note 备注信息
     * @return 当前告警实例
     */
    public Alert suppress(String operator, String note) {
        if (this.status != AlertStatus.SUPPRESSED) {
            this.status = AlertStatus.SUPPRESSED;
            this.lastUpdateTime = LocalDateTime.now();
            this.handledBy = operator;
            this.handlingNote = note;
            
            addEvent(new AlertEvent(
                "SUPPRESSED",
                "告警抑制",
                operator,
                String.format("告警已抑制：%s", note)
            ));
        }
        return this;
    }
    
    /**
     * 自动解决告警（由系统恢复）
     *
     * @return 当前告警实例
     */
    public Alert autoResolve() {
        if (this.status == AlertStatus.ACTIVE || this.status == AlertStatus.ACKNOWLEDGED) {
            this.status = AlertStatus.RESOLVED;
            this.lastUpdateTime = LocalDateTime.now();
            this.handledTime = LocalDateTime.now();
            this.handledBy = "系统";
            this.handlingNote = "系统自动恢复";
            
            addEvent(new AlertEvent(
                "AUTO_RESOLVED",
                "自动恢复",
                "系统",
                "告警条件已不满足，系统自动解决"
            ));
        }
        return this;
    }
    
    /**
     * 添加上下文数据
     *
     * @param key 键
     * @param value 值
     * @return 当前告警实例
     */
    public Alert addContext(String key, String value) {
        this.context.put(key, value);
        return this;
    }
    
    /**
     * 添加告警事件
     *
     * @param event 告警事件
     * @return 当前告警实例
     */
    public Alert addEvent(AlertEvent event) {
        this.events.add(event);
        return this;
    }
    
    /**
     * 获取带单位的指标值显示
     *
     * @return 带单位的指标值字符串
     */
    public String getFormattedMetricValue() {
        // 对特殊单位进行格式化处理
        if (MetricUnit.PERCENTAGE.equals(metricUnit)) {
            return String.format("%.2f%s", metricValue, metricUnit.getSymbol());
        }
        
        // 处理字节单位的自动转换
        if (MetricUnit.BYTES.equals(metricUnit) && metricValue > 1024) {
            if (metricValue < 1024 * 1024) {
                return String.format("%.2f KB", metricValue / 1024);
            } else if (metricValue < 1024 * 1024 * 1024) {
                return String.format("%.2f MB", metricValue / (1024 * 1024));
            } else if (metricValue < 1024 * 1024 * 1024 * 1024) {
                return String.format("%.2f GB", metricValue / (1024 * 1024 * 1024));
            } else {
                return String.format("%.2f TB", metricValue / (1024 * 1024 * 1024 * 1024));
            }
        }
        
        // 默认格式
        if (metricUnit.getSymbol().isEmpty()) {
            return String.format("%.2f", metricValue);
        } else {
            return String.format("%.2f %s", metricValue, metricUnit.getSymbol());
        }
    }
    
    /**
     * 判断告警是否为活跃状态
     *
     * @return 如果为活跃状态则返回true
     */
    public boolean isActive() {
        return this.status.isActive();
    }
    
    /**
     * 判断告警是否已处理
     *
     * @return 如果已处理则返回true
     */
    public boolean isHandled() {
        return this.handledTime != null;
    }
}
