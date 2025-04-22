package com.platform.scheduler.util;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cron表达式工具类
 * 
 * @author platform
 */
public class CronUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(CronUtils.class);
    
    /**
     * 验证Cron表达式是否有效
     * 
     * @param cronExpression Cron表达式
     * @return 是否有效
     */
    public static boolean isValid(String cronExpression) {
        try {
            return CronExpression.isValidExpression(cronExpression);
        } catch (Exception e) {
            logger.error("验证Cron表达式异常: " + cronExpression, e);
            return false;
        }
    }
    
    /**
     * 获取下一次执行时间
     * 
     * @param cronExpression Cron表达式
     * @param baseTime 基准时间
     * @return 下一次执行时间
     */
    public static Date getNextExecution(String cronExpression, Date baseTime) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(baseTime);
        } catch (ParseException e) {
            logger.error("解析Cron表达式异常: " + cronExpression, e);
            return null;
        }
    }
    
    /**
     * 获取多次执行时间
     * 
     * @param cronExpression Cron表达式
     * @param baseTime 基准时间
     * @param count 次数
     * @return 执行时间数组
     */
    public static Date[] getNextExecutions(String cronExpression, Date baseTime, int count) {
        if (count <= 0) {
            return new Date[0];
        }
        
        try {
            CronExpression cron = new CronExpression(cronExpression);
            Date[] times = new Date[count];
            Date nextTime = baseTime;
            
            for (int i = 0; i < count; i++) {
                nextTime = cron.getNextValidTimeAfter(nextTime);
                if (nextTime == null) {
                    break;
                }
                times[i] = nextTime;
            }
            
            return times;
        } catch (ParseException e) {
            logger.error("解析Cron表达式异常: " + cronExpression, e);
            return new Date[0];
        }
    }
    
    /**
     * 计算Cron表达式在指定时间范围内的执行次数
     * 
     * @param cronExpression Cron表达式
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 执行次数
     */
    public static int getExecutionCount(String cronExpression, Date startTime, Date endTime) {
        if (startTime.after(endTime)) {
            return 0;
        }
        
        try {
            CronExpression cron = new CronExpression(cronExpression);
            int count = 0;
            Date nextTime = startTime;
            
            while (true) {
                nextTime = cron.getNextValidTimeAfter(nextTime);
                if (nextTime == null || nextTime.after(endTime)) {
                    break;
                }
                count++;
            }
            
            return count;
        } catch (ParseException e) {
            logger.error("解析Cron表达式异常: " + cronExpression, e);
            return 0;
        }
    }
    
    /**
     * 获取Cron表达式的描述
     * 
     * @param cronExpression Cron表达式
     * @return 描述
     */
    public static String getDescription(String cronExpression) {
        if (!isValid(cronExpression)) {
            return "无效的Cron表达式";
        }
        
        try {
            CronExpression cron = new CronExpression(cronExpression);
            StringBuilder description = new StringBuilder();
            
            // 简单解析Cron表达式
            String[] parts = cronExpression.split("\\s+");
            if (parts.length >= 6) {
                // 描述秒
                if ("0".equals(parts[0])) {
                    description.append("每分钟的第0秒");
                } else if ("*".equals(parts[0])) {
                    description.append("每秒");
                } else if (parts[0].contains("/")) {
                    String[] divParts = parts[0].split("/");
                    description.append("每").append(divParts[1]).append("秒");
                } else {
                    description.append("在第").append(parts[0]).append("秒");
                }
                
                description.append("执行，");
                
                // 分钟
                if ("*".equals(parts[1])) {
                    description.append("每分钟");
                } else if (parts[1].contains("/")) {
                    String[] divParts = parts[1].split("/");
                    description.append("每").append(divParts[1]).append("分钟");
                } else {
                    description.append("在第").append(parts[1]).append("分钟");
                }
                
                // 更多部分的描述...
            }
            
            // 添加下次执行时间
            Date nextTime = cron.getNextValidTimeAfter(new Date());
            if (nextTime != null) {
                description.append("，下次执行时间: ").append(nextTime);
            }
            
            return description.toString();
        } catch (ParseException e) {
            return "无法解析的Cron表达式";
        }
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private CronUtils() {
        throw new IllegalStateException("Utility class");
    }
}
