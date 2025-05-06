package com.platform.report.domain.model.schedule;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumSet;
import java.util.Set;

/**
 * 计划周期
 * 定义报表生成的定时周期
 */
@Getter
public class ScheduleRecurrence {
    
    private RecurrenceType type;
    private Integer interval;
    private LocalTime executionTime;
    private Set<DayOfWeek> daysOfWeek;
    private Integer dayOfMonth;
    private Integer monthOfYear;
    private String cronExpression;
    
    /**
     * 创建日频率周期
     */
    public static ScheduleRecurrence daily(int interval, LocalTime executionTime) {
        ScheduleRecurrence recurrence = new ScheduleRecurrence();
        recurrence.type = RecurrenceType.DAILY;
        recurrence.interval = interval;
        recurrence.executionTime = executionTime;
        return recurrence;
    }
    
    /**
     * 创建周频率周期
     */
    public static ScheduleRecurrence weekly(int interval, Set<DayOfWeek> daysOfWeek, LocalTime executionTime) {
        ScheduleRecurrence recurrence = new ScheduleRecurrence();
        recurrence.type = RecurrenceType.WEEKLY;
        recurrence.interval = interval;
        recurrence.daysOfWeek = daysOfWeek != null ? daysOfWeek : EnumSet.noneOf(DayOfWeek.class);
        recurrence.executionTime = executionTime;
        return recurrence;
    }
    
    /**
     * 创建月频率周期
     */
    public static ScheduleRecurrence monthly(int interval, int dayOfMonth, LocalTime executionTime) {
        ScheduleRecurrence recurrence = new ScheduleRecurrence();
        recurrence.type = RecurrenceType.MONTHLY;
        recurrence.interval = interval;
        recurrence.dayOfMonth = dayOfMonth;
        recurrence.executionTime = executionTime;
        return recurrence;
    }
    
    /**
     * 创建年频率周期
     */
    public static ScheduleRecurrence yearly(int monthOfYear, int dayOfMonth, LocalTime executionTime) {
        ScheduleRecurrence recurrence = new ScheduleRecurrence();
        recurrence.type = RecurrenceType.YEARLY;
        recurrence.monthOfYear = monthOfYear;
        recurrence.dayOfMonth = dayOfMonth;
        recurrence.executionTime = executionTime;
        return recurrence;
    }
    
    /**
     * 创建CRON表达式周期
     */
    public static ScheduleRecurrence cron(String cronExpression) {
        ScheduleRecurrence recurrence = new ScheduleRecurrence();
        recurrence.type = RecurrenceType.CRON;
        recurrence.cronExpression = cronExpression;
        return recurrence;
    }
    
    /**
     * 计算下一次执行时间
     */
    public LocalDateTime calculateNextExecutionTime(LocalDateTime from) {
        LocalDateTime baseTime = from.truncatedTo(ChronoUnit.MINUTES);
        
        switch (this.type) {
            case DAILY:
                LocalDateTime nextDay = baseTime.plusDays(this.interval);
                return nextDay.with(this.executionTime);
                
            case WEEKLY:
                // 找到下一个满足条件的工作日
                LocalDateTime nextDate = baseTime;
                int weekCounter = 0;
                
                while (true) {
                    nextDate = nextDate.plusDays(1);
                    
                    if (this.daysOfWeek.contains(nextDate.getDayOfWeek())) {
                        int weekNumber = (int) (ChronoUnit.WEEKS.between(baseTime, nextDate) + 1);
                        
                        if (weekNumber % this.interval == 0) {
                            return nextDate.with(this.executionTime);
                        }
                    }
                    
                    // 避免无限循环
                    if (nextDate.isAfter(baseTime.plusYears(1))) {
                        return baseTime.plusYears(1).with(this.executionTime);
                    }
                }
                
            case MONTHLY:
                LocalDateTime nextMonth = baseTime.plusMonths(this.interval);
                int day = Math.min(this.dayOfMonth, nextMonth.getMonth().length(nextMonth.toLocalDate().isLeapYear()));
                return nextMonth.withDayOfMonth(day).with(this.executionTime);
                
            case YEARLY:
                LocalDateTime nextYear = baseTime.plusYears(1);
                int monthDays = nextYear.withMonth(this.monthOfYear).getMonth().length(nextYear.toLocalDate().isLeapYear());
                int yearDay = Math.min(this.dayOfMonth, monthDays);
                return nextYear.withMonth(this.monthOfYear).withDayOfMonth(yearDay).with(this.executionTime);
                
            case CRON:
                // 这里可以使用Quartz的CronExpression来计算下一次执行时间
                // 简化处理，这里假设是每天执行
                return baseTime.plusDays(1).with(LocalTime.of(0, 0));
                
            default:
                throw new IllegalStateException("未知的重复类型: " + this.type);
        }
    }
    
    // 私有构造函数
    private ScheduleRecurrence() {
    }
}
