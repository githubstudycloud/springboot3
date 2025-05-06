package com.platform.monitor.domain.event;

import com.platform.monitor.domain.model.Alert;
import com.platform.monitor.domain.model.AlertStatus;
import lombok.Getter;

/**
 * 告警状态变更事件
 */
@Getter
public class AlertStatusChangedEvent extends MonitorEvent {
    
    /**
     * 告警
     */
    private final Alert alert;
    
    /**
     * 旧状态
     */
    private final AlertStatus oldStatus;
    
    /**
     * 新状态
     */
    private final AlertStatus newStatus;
    
    /**
     * 操作人
     */
    private final String operator;
    
    /**
     * 备注信息
     */
    private final String note;
    
    /**
     * 构造函数
     *
     * @param alert 告警
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     * @param operator 操作人
     * @param note 备注信息
     */
    public AlertStatusChangedEvent(Alert alert, AlertStatus oldStatus, AlertStatus newStatus, 
                                  String operator, String note) {
        super(MonitorEventType.ALERT_STATUS_CHANGED, "platform-monitor-dashboard");
        this.alert = alert;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.operator = operator;
        this.note = note;
        
        // 添加事件数据
        addData("alertId", alert.getId());
        addData("alertName", alert.getName());
        addData("severity", alert.getSeverity().getCode());
        addData("serviceName", alert.getServiceName());
        addData("oldStatus", oldStatus.getCode());
        addData("newStatus", newStatus.getCode());
        addData("operator", operator);
        addData("note", note);
        addData("lastUpdateTime", alert.getLastUpdateTime());
    }
    
    @Override
    public String getDescription() {
        return String.format("告警状态变更: [%s] %s - %s -> %s (操作人: %s, 备注: %s)",
                alert.getSeverity().getDisplayName(),
                alert.getName(),
                oldStatus.getDisplayName(),
                newStatus.getDisplayName(),
                operator,
                note);
    }
    
    /**
     * 判断是否为告警确认事件
     *
     * @return 如果是告警确认事件则返回true
     */
    public boolean isAcknowledged() {
        return oldStatus == AlertStatus.ACTIVE && newStatus == AlertStatus.ACKNOWLEDGED;
    }
    
    /**
     * 判断是否为告警解决事件
     *
     * @return 如果是告警解决事件则返回true
     */
    public boolean isResolved() {
        return (oldStatus == AlertStatus.ACTIVE || oldStatus == AlertStatus.ACKNOWLEDGED) 
                && newStatus == AlertStatus.RESOLVED;
    }
    
    /**
     * 判断是否为告警关闭事件
     *
     * @return 如果是告警关闭事件则返回true
     */
    public boolean isClosed() {
        return newStatus == AlertStatus.CLOSED;
    }
}
