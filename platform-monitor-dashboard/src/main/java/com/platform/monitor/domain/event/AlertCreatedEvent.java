package com.platform.monitor.domain.event;

import com.platform.monitor.domain.model.Alert;
import lombok.Getter;

/**
 * 告警创建事件
 */
@Getter
public class AlertCreatedEvent extends MonitorEvent {
    
    /**
     * 告警
     */
    private final Alert alert;
    
    /**
     * 构造函数
     *
     * @param alert 告警
     */
    public AlertCreatedEvent(Alert alert) {
        super(MonitorEventType.ALERT_CREATED, "platform-monitor-dashboard");
        this.alert = alert;
        
        // 添加事件数据
        addData("alertId", alert.getId());
        addData("alertName", alert.getName());
        addData("alertDescription", alert.getDescription());
        addData("severity", alert.getSeverity().getCode());
        addData("metricType", alert.getMetricType().getCode());
        addData("metricValue", alert.getMetricValue());
        addData("ruleId", alert.getRuleId());
        addData("serviceInstanceId", alert.getServiceInstanceId());
        addData("serviceName", alert.getServiceName());
        addData("status", alert.getStatus().getCode());
        addData("firstOccurTime", alert.getFirstOccurTime());
    }
    
    @Override
    public String getDescription() {
        return String.format("告警已创建: [%s] %s - %s (服务: %s, 指标值: %s)",
                alert.getSeverity().getDisplayName(),
                alert.getName(),
                alert.getDescription(),
                alert.getServiceName(),
                alert.getFormattedMetricValue());
    }
}
