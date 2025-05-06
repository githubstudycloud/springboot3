package com.platform.monitor.domain.event;

import com.platform.monitor.domain.model.AlertRule;
import com.platform.monitor.domain.model.Metric;
import lombok.Getter;

/**
 * 指标阈值超出事件
 */
@Getter
public class MetricThresholdExceededEvent extends MonitorEvent {
    
    /**
     * 指标数据
     */
    private final Metric metric;
    
    /**
     * 告警规则
     */
    private final AlertRule alertRule;
    
    /**
     * 构造函数
     *
     * @param metric 指标数据
     * @param alertRule 告警规则
     */
    public MetricThresholdExceededEvent(Metric metric, AlertRule alertRule) {
        super(MonitorEventType.METRIC_THRESHOLD_EXCEEDED, "platform-monitor-dashboard");
        this.metric = metric;
        this.alertRule = alertRule;
        
        // 添加事件数据
        addData("metricId", metric.getId());
        addData("metricName", metric.getName());
        addData("metricType", metric.getMetricType().getCode());
        addData("metricValue", metric.getValue());
        addData("serviceInstanceId", metric.getServiceInstanceId());
        addData("ruleId", alertRule.getId());
        addData("ruleName", alertRule.getName());
        addData("severity", alertRule.getSeverity().getCode());
        
        // 添加首个条件数据
        if (!alertRule.getConditions().isEmpty()) {
            addData("conditionType", alertRule.getConditions().get(0).getConditionType().getCode());
            addData("threshold", alertRule.getConditions().get(0).getThreshold());
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("指标阈值超出: [%s] %s - %s (当前值: %s, 规则: %s)",
                alertRule.getSeverity().getDisplayName(),
                metric.getMetricType().getDisplayName(),
                metric.getName(),
                metric.getFormattedValue(),
                alertRule.getName());
    }
}
