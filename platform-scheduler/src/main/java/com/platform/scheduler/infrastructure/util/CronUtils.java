package com.platform.scheduler.infrastructure.util;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Cron表达式工具类
 * 提供Cron表达式相关的工具方法
 *
 * @author platform
 */
public class CronUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(CronUtils.class);
    
    private CronUtils() {
        // 私有构造方法，防止实例化
    }
    
    /**
     * 验证Cron表达式是否有效
     *
     * @param cronExpression Cron表达式
     * @return 是否有效
     */
    public static boolean isValid(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }
    
    /**
     * 获取Cron表达式的下一次执行时间
     *
     * @param cronExpression Cron表达式
     * @return 下一次执行时间，如果表达式无效则返回null
     */
    public static LocalDateTime getNextExecutionTime(String cronExpression) {
        try {
            CronExpression expression = new CronExpression(cronExpression);
            Date nextDate = expression.getNextValidTimeAfter(new Date());
            return nextDate != null ? LocalDateTime.ofInstant(nextDate.toInstant(), ZoneId.systemDefault()) : null;
        } catch (ParseException e) {
            logger.error("Parse cron expression failed: {}", cronExpression, e);
            return null;
        }
    }
    
    /**
     * 获取Cron表达式的下N次执行时间
     *
     * @param cronExpression Cron表达式
     * @param numTimes 获取的次数
     * @return 下N次执行时间列表，如果表达式无效则返回空列表
     */
    public static List<LocalDateTime> getNextExecutionTimes(String cronExpression, int numTimes) {
        List<LocalDateTime> executionTimes = new ArrayList<>();
        try {
            CronExpression expression = new CronExpression(cronExpression);
            Date nextDate = new Date();
            
            for (int i = 0; i < numTimes; i++) {
                nextDate = expression.getNextValidTimeAfter(nextDate);
                if (nextDate == null) {
                    break;
                }
                executionTimes.add(LocalDateTime.ofInstant(nextDate.toInstant(), ZoneId.systemDefault()));
            }
        } catch (ParseException e) {
            logger.error("Parse cron expression failed: {}", cronExpression, e);
        }
        
        return executionTimes;
    }
    
    /**
     * 获取指定时间范围内的所有执行时间
     *
     * @param cronExpression Cron表达式
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间范围内的所有执行时间，如果表达式无效则返回空列表
     */
    public static List<LocalDateTime> getExecutionTimesBetween(String cronExpression, LocalDateTime startTime, LocalDateTime endTime) {
        List<LocalDateTime> executionTimes = new ArrayList<>();
        
        try {
            CronExpression expression = new CronExpression(cronExpression);
            Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
            
            Date nextDate = expression.getNextValidTimeAfter(startDate);
            while (nextDate != null && !nextDate.after(endDate)) {
                executionTimes.add(LocalDateTime.ofInstant(nextDate.toInstant(), ZoneId.systemDefault()));
                nextDate = expression.getNextValidTimeAfter(nextDate);
            }
        } catch (ParseException e) {
            logger.error("Parse cron expression failed: {}", cronExpression, e);
        }
        
        return executionTimes;
    }
    
    /**
     * 将Cron表达式转换为可读的描述文本
     *
     * @param cronExpression Cron表达式
     * @return 可读的描述文本，如果表达式无效则返回null
     */
    public static String getDescription(String cronExpression) {
        if (!isValid(cronExpression)) {
            return null;
        }
        
        try {
            StringBuilder description = new StringBuilder();
            
            String[] parts = cronExpression.split("\\s+");
            if (parts.length < 6) {
                return "无效的Cron表达式";
            }
            
            // 秒
            if (!"0".equals(parts[0]) && !"*".equals(parts[0])) {
                description.append("每分钟的第 ").append(parts[0]).append(" 秒");
            } else {
                description.append("每分钟的第 0 秒");
            }
            
            // 分
            if (!"*".equals(parts[1])) {
                if (parts[1].contains("/")) {
                    String[] divides = parts[1].split("/");
                    description.append("，每隔 ").append(divides[1]).append(" 分钟");
                } else if (parts[1].contains("-")) {
                    String[] ranges = parts[1].split("-");
                    description.append("，在第 ").append(ranges[0]).append(" 到第 ").append(ranges[1]).append(" 分钟之间");
                } else if (parts[1].contains(",")) {
                    description.append("，在第 ").append(parts[1].replace(",", "、")).append(" 分钟");
                } else {
                    description.append("，在第 ").append(parts[1]).append(" 分钟");
                }
            } else {
                description.append("，每分钟");
            }
            
            // 时
            if (!"*".equals(parts[2])) {
                if (parts[2].contains("/")) {
                    String[] divides = parts[2].split("/");
                    description.append("，每隔 ").append(divides[1]).append(" 小时");
                } else if (parts[2].contains("-")) {
                    String[] ranges = parts[2].split("-");
                    description.append("，在 ").append(ranges[0]).append(" 点到 ").append(ranges[1]).append(" 点之间");
                } else if (parts[2].contains(",")) {
                    description.append("，在 ").append(parts[2].replace(",", "、")).append(" 点");
                } else {
                    description.append("，在 ").append(parts[2]).append(" 点");
                }
            } else {
                description.append("，每小时");
            }
            
            // 日
            if (!"*".equals(parts[3]) && !"?".equals(parts[3])) {
                if (parts[3].contains("/")) {
                    String[] divides = parts[3].split("/");
                    description.append("，每隔 ").append(divides[1]).append(" 天");
                } else if (parts[3].contains("-")) {
                    String[] ranges = parts[3].split("-");
                    description.append("，在每月的 ").append(ranges[0]).append(" 号到 ").append(ranges[1]).append(" 号之间");
                } else if (parts[3].contains(",")) {
                    description.append("，在每月的 ").append(parts[3].replace(",", "、")).append(" 号");
                } else {
                    description.append("，在每月的 ").append(parts[3]).append(" 号");
                }
            } else if (!"?".equals(parts[3])) {
                description.append("，每天");
            }
            
            // 月
            if (!"*".equals(parts[4])) {
                if (parts[4].contains("/")) {
                    String[] divides = parts[4].split("/");
                    description.append("，每隔 ").append(divides[1]).append(" 个月");
                } else if (parts[4].contains("-")) {
                    String[] ranges = parts[4].split("-");
                    description.append("，在 ").append(getMonthName(ranges[0])).append(" 到 ").append(getMonthName(ranges[1])).append(" 之间");
                } else if (parts[4].contains(",")) {
                    String[] months = parts[4].split(",");
                    description.append("，在 ");
                    for (int i = 0; i < months.length; i++) {
                        if (i > 0) {
                            description.append("、");
                        }
                        description.append(getMonthName(months[i]));
                    }
                } else {
                    description.append("，在 ").append(getMonthName(parts[4]));
                }
            } else {
                description.append("，每月");
            }
            
            // 周
            if (!"*".equals(parts[5]) && !"?".equals(parts[5])) {
                if (parts[5].contains("/")) {
                    String[] divides = parts[5].split("/");
                    description.append("，每隔 ").append(divides[1]).append(" 周");
                } else if (parts[5].contains("-")) {
                    String[] ranges = parts[5].split("-");
                    description.append("，在 ").append(getWeekdayName(ranges[0])).append(" 到 ").append(getWeekdayName(ranges[1])).append(" 之间");
                } else if (parts[5].contains(",")) {
                    String[] weeks = parts[5].split(",");
                    description.append("，在 ");
                    for (int i = 0; i < weeks.length; i++) {
                        if (i > 0) {
                            description.append("、");
                        }
                        description.append(getWeekdayName(weeks[i]));
                    }
                } else if (parts[5].contains("L")) {
                    description.append("，在本月的最后一个").append(getWeekdayName(parts[5].replace("L", "")));
                } else {
                    description.append("，在 ").append(getWeekdayName(parts[5]));
                }
            } else if (!"?".equals(parts[5])) {
                description.append("，每周");
            }
            
            return description.toString();
            
        } catch (Exception e) {
            logger.error("Generate cron description failed: {}", cronExpression, e);
            return "无法解析的Cron表达式";
        }
    }
    
    /**
     * 获取月份名称
     *
     * @param month 月份数字（1-12）
     * @return 月份名称
     */
    private static String getMonthName(String month) {
        switch (month) {
            case "1": return "一月";
            case "2": return "二月";
            case "3": return "三月";
            case "4": return "四月";
            case "5": return "五月";
            case "6": return "六月";
            case "7": return "七月";
            case "8": return "八月";
            case "9": return "九月";
            case "10": return "十月";
            case "11": return "十一月";
            case "12": return "十二月";
            default: return month + "月";
        }
    }
    
    /**
     * 获取星期名称
     *
     * @param weekday 星期数字（1-7）
     * @return 星期名称
     */
    private static String getWeekdayName(String weekday) {
        switch (weekday) {
            case "1": return "星期日";
            case "2": return "星期一";
            case "3": return "星期二";
            case "4": return "星期三";
            case "5": return "星期四";
            case "6": return "星期五";
            case "7": return "星期六";
            default: return "星期" + weekday;
        }
    }
}
