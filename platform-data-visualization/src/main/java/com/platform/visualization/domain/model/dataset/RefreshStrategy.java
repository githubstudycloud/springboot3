package com.platform.visualization.domain.model.dataset;

import java.time.Duration;

/**
 * 数据集刷新策略
 */
public class RefreshStrategy {
    private final RefreshType type;
    private final String cronExpression;
    private final Duration interval;

    private RefreshStrategy(RefreshType type, String cronExpression, Duration interval) {
        this.type = type;
        this.cronExpression = cronExpression;
        this.interval = interval;
    }

    public static RefreshStrategy manual() {
        return new RefreshStrategy(RefreshType.MANUAL, null, null);
    }

    public static RefreshStrategy scheduled(String cronExpression) {
        return new RefreshStrategy(RefreshType.SCHEDULED, cronExpression, null);
    }

    public static RefreshStrategy interval(Duration interval) {
        return new RefreshStrategy(RefreshType.INTERVAL, null, interval);
    }

    public RefreshType getType() {
        return type;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public Duration getInterval() {
        return interval;
    }

    /**
     * 刷新类型
     */
    public enum RefreshType {
        MANUAL, SCHEDULED, INTERVAL
    }
}