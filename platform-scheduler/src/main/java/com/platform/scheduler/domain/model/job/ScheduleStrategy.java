package com.platform.scheduler.domain.model.job;

import com.platform.scheduler.domain.model.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 调度策略值对象
 * 定义作业的调度规则，支持Cron表达式、固定延迟和固定频率调度
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
@ToString
public class ScheduleStrategy implements ValueObject {
    
    /**
     * 调度类型
     */
    private final ScheduleType type;
    
    /**
     * Cron表达式(当type为CRON时有效)
     */
    private final String cronExpression;
    
    /**
     * 固定延迟时间(秒)(当type为FIXED_DELAY时有效)
     */
    private final Long fixedDelay;
    
    /**
     * 固定频率时间(秒)(当type为FIXED_RATE时有效)
     */
    private final Long fixedRate;
    
    /**
     * 首次执行时间(对于ONE_TIME类型或需要指定初始延迟的情况)
     */
    private final LocalDateTime startAt;
    
    /**
     * 结束时间(可选)
     */
    private final LocalDateTime endAt;
    
    /**
     * 最大执行次数(可选，0表示无限制)
     */
    private final Integer maxExecutions;
    
    private ScheduleStrategy(Builder builder) {
        this.type = builder.type;
        this.cronExpression = builder.cronExpression;
        this.fixedDelay = builder.fixedDelay;
        this.fixedRate = builder.fixedRate;
        this.startAt = builder.startAt;
        this.endAt = builder.endAt;
        this.maxExecutions = builder.maxExecutions;
        
        validate();
    }
    
    /**
     * 校验调度策略配置的有效性
     */
    private void validate() {
        if (type == null) {
            throw new IllegalArgumentException("Schedule type cannot be null");
        }
        
        switch (type) {
            case CRON:
                if (cronExpression == null || cronExpression.trim().isEmpty()) {
                    throw new IllegalArgumentException("Cron expression cannot be null or empty for CRON type");
                }
                break;
            case FIXED_DELAY:
                if (fixedDelay == null || fixedDelay <= 0) {
                    throw new IllegalArgumentException("Fixed delay must be positive for FIXED_DELAY type");
                }
                break;
            case FIXED_RATE:
                if (fixedRate == null || fixedRate <= 0) {
                    throw new IllegalArgumentException("Fixed rate must be positive for FIXED_RATE type");
                }
                break;
            case ONE_TIME:
                if (startAt == null) {
                    throw new IllegalArgumentException("Start time cannot be null for ONE_TIME type");
                }
                break;
        }
        
        if (endAt != null && startAt != null && endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        
        if (maxExecutions != null && maxExecutions < 0) {
            throw new IllegalArgumentException("Max executions cannot be negative");
        }
    }
    
    /**
     * 判断当前策略是否已过期
     *
     * @param currentTime 当前时间
     * @return 如果当前时间已超过结束时间，则返回true
     */
    public boolean isExpired(LocalDateTime currentTime) {
        return endAt != null && currentTime.isAfter(endAt);
    }
    
    /**
     * 判断当前策略是否已达到最大执行次数
     *
     * @param executionCount 已执行次数
     * @return 如果已达到最大执行次数，则返回true
     */
    public boolean hasReachedMaxExecutions(int executionCount) {
        return maxExecutions != null && maxExecutions > 0 && executionCount >= maxExecutions;
    }
    
    /**
     * 创建Cron表达式调度策略
     *
     * @param cronExpression Cron表达式
     * @return 新的调度策略
     */
    public static ScheduleStrategy cronSchedule(String cronExpression) {
        return builder()
                .withType(ScheduleType.CRON)
                .withCronExpression(cronExpression)
                .build();
    }
    
    /**
     * 创建固定延迟调度策略
     *
     * @param delaySeconds 延迟秒数
     * @return 新的调度策略
     */
    public static ScheduleStrategy fixedDelaySchedule(long delaySeconds) {
        return builder()
                .withType(ScheduleType.FIXED_DELAY)
                .withFixedDelay(delaySeconds)
                .build();
    }
    
    /**
     * 创建固定频率调度策略
     *
     * @param rateSeconds 频率秒数
     * @return 新的调度策略
     */
    public static ScheduleStrategy fixedRateSchedule(long rateSeconds) {
        return builder()
                .withType(ScheduleType.FIXED_RATE)
                .withFixedRate(rateSeconds)
                .build();
    }
    
    /**
     * 创建一次性调度策略
     *
     * @param executeAt 执行时间
     * @return 新的调度策略
     */
    public static ScheduleStrategy oneTimeSchedule(LocalDateTime executeAt) {
        return builder()
                .withType(ScheduleType.ONE_TIME)
                .withStartAt(executeAt)
                .build();
    }
    
    /**
     * 获取构建器
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 调度类型枚举
     */
    public enum ScheduleType {
        /**
         * 基于Cron表达式的调度
         */
        CRON,
        
        /**
         * 固定延迟调度(任务完成后等待指定时间再执行下一次)
         */
        FIXED_DELAY,
        
        /**
         * 固定频率调度(按照指定的时间间隔执行，不考虑任务执行时间)
         */
        FIXED_RATE,
        
        /**
         * 一次性调度(仅执行一次)
         */
        ONE_TIME
    }
    
    /**
     * 调度策略构建器
     */
    public static class Builder {
        private ScheduleType type;
        private String cronExpression;
        private Long fixedDelay;
        private Long fixedRate;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private Integer maxExecutions;
        
        public Builder withType(ScheduleType type) {
            this.type = type;
            return this;
        }
        
        public Builder withCronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
            return this;
        }
        
        public Builder withFixedDelay(Long fixedDelay) {
            this.fixedDelay = fixedDelay;
            return this;
        }
        
        public Builder withFixedRate(Long fixedRate) {
            this.fixedRate = fixedRate;
            return this;
        }
        
        public Builder withStartAt(LocalDateTime startAt) {
            this.startAt = startAt;
            return this;
        }
        
        public Builder withEndAt(LocalDateTime endAt) {
            this.endAt = endAt;
            return this;
        }
        
        public Builder withMaxExecutions(Integer maxExecutions) {
            this.maxExecutions = maxExecutions;
            return this;
        }
        
        public ScheduleStrategy build() {
            return new ScheduleStrategy(this);
        }
    }
}
