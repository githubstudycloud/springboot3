package com.example.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 日期时间工具类
 *
 * @author platform
 * @since 1.0.0
 */
public class DateTimeUtil {
    /**
     * 标准日期格式：yyyy-MM-dd
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    
    /**
     * 标准时间格式：HH:mm:ss
     */
    public static final String PATTERN_TIME = "HH:mm:ss";
    
    /**
     * 标准日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 标准日期时间带毫秒格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String PATTERN_DATETIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    
    /**
     * 紧凑日期格式：yyyyMMdd
     */
    public static final String PATTERN_DATE_COMPACT = "yyyyMMdd";
    
    /**
     * 紧凑时间格式：HHmmss
     */
    public static final String PATTERN_TIME_COMPACT = "HHmmss";
    
    /**
     * 紧凑日期时间格式：yyyyMMddHHmmss
     */
    public static final String PATTERN_DATETIME_COMPACT = "yyyyMMddHHmmss";
    
    /**
     * 紧凑日期时间带毫秒格式：yyyyMMddHHmmssSSS
     */
    public static final String PATTERN_DATETIME_MS_COMPACT = "yyyyMMddHHmmssSSS";
    
    /**
     * 获取当前日期
     *
     * @return LocalDate
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    /**
     * 获取当前时间
     *
     * @return LocalTime
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }
    
    /**
     * 获取当前日期时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 时间戳
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    /**
     * 获取当前时间戳（秒）
     *
     * @return 时间戳
     */
    public static long getCurrentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }
    
    /**
     * Date转LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * LocalDateTime转Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * 时间戳（毫秒）转LocalDateTime
     *
     * @param timestamp 时间戳（毫秒）
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
    
    /**
     * LocalDateTime转时间戳（毫秒）
     *
     * @param localDateTime LocalDateTime
     * @return 时间戳（毫秒）
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    /**
     * 格式化日期（yyyy-MM-dd）
     *
     * @param localDate LocalDate
     * @return 格式化后的字符串
     */
    public static String formatDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DateTimeFormatter.ofPattern(PATTERN_DATE));
    }
    
    /**
     * 格式化时间（HH:mm:ss）
     *
     * @param localTime LocalTime
     * @return 格式化后的字符串
     */
    public static String formatTime(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        return localTime.format(DateTimeFormatter.ofPattern(PATTERN_TIME));
    }
    
    /**
     * 格式化日期时间（yyyy-MM-dd HH:mm:ss）
     *
     * @param localDateTime LocalDateTime
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(PATTERN_DATETIME));
    }
    
    /**
     * 按自定义格式格式化日期时间
     *
     * @param localDateTime LocalDateTime
     * @param pattern       格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 解析日期字符串（yyyy-MM-dd）
     *
     * @param dateStr 日期字符串
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        if (StringUtil.isBlank(dateStr)) {
            return null;
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(PATTERN_DATE));
    }
    
    /**
     * 解析时间字符串（HH:mm:ss）
     *
     * @param timeStr 时间字符串
     * @return LocalTime
     */
    public static LocalTime parseTime(String timeStr) {
        if (StringUtil.isBlank(timeStr)) {
            return null;
        }
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(PATTERN_TIME));
    }
    
    /**
     * 解析日期时间字符串（yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (StringUtil.isBlank(dateTimeStr)) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(PATTERN_DATETIME));
    }
    
    /**
     * 按自定义格式解析日期时间字符串
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     格式
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String dateTimeStr, String pattern) {
        if (StringUtil.isBlank(dateTimeStr)) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 计算两个日期之间的天数差
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
    
    /**
     * 计算两个日期时间之间的小时差
     *
     * @param start 开始日期时间
     * @param end   结束日期时间
     * @return 小时差
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }
    
    /**
     * 计算两个日期时间之间的分钟差
     *
     * @param start 开始日期时间
     * @param end   结束日期时间
     * @return 分钟差
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }
    
    /**
     * 日期加天数
     *
     * @param localDate 日期
     * @param days      天数
     * @return 新日期
     */
    public static LocalDate plusDays(LocalDate localDate, long days) {
        return localDate.plusDays(days);
    }
    
    /**
     * 日期时间加小时
     *
     * @param localDateTime 日期时间
     * @param hours         小时数
     * @return 新日期时间
     */
    public static LocalDateTime plusHours(LocalDateTime localDateTime, long hours) {
        return localDateTime.plusHours(hours);
    }
    
    /**
     * 获取某月的第一天
     *
     * @param year  年
     * @param month 月
     * @return 第一天
     */
    public static LocalDate firstDayOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1);
    }
    
    /**
     * 获取某月的最后一天
     *
     * @param year  年
     * @param month 月
     * @return 最后一天
     */
    public static LocalDate lastDayOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
    }
    
    /**
     * 获取本周第一天（周一）
     *
     * @return 本周第一天
     */
    public static LocalDate firstDayOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
    
    /**
     * 获取本周最后一天（周日）
     *
     * @return 本周最后一天
     */
    public static LocalDate lastDayOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }
}