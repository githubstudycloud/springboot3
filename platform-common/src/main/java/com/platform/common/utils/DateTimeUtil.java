package com.platform.common.utils;

import com.platform.common.constants.CommonConstants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 日期时间工具类，基于Java 8时间API
 */
public class DateTimeUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private DateTimeUtil() {
        throw new AssertionError("No DateTimeUtil instances for you!");
    }
    
    /**
     * 系统默认时区
     */
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of(CommonConstants.System.DEFAULT_TIMEZONE);
    
    /**
     * 默认日期时间格式器
     */
    public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = 
            DateTimeFormatter.ofPattern(CommonConstants.System.DATETIME_FORMAT);
    
    /**
     * 默认日期格式器
     */
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = 
            DateTimeFormatter.ofPattern(CommonConstants.System.DATE_FORMAT);
    
    /**
     * 默认时间格式器
     */
    public static final DateTimeFormatter DEFAULT_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern(CommonConstants.System.TIME_FORMAT);
    
    /**
     * 获取当前日期时间
     * 
     * @return 当前日期时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }
    
    /**
     * 获取当前日期
     * 
     * @return 当前日期
     */
    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE_ID);
    }
    
    /**
     * 获取当前时间
     * 
     * @return 当前时间
     */
    public static LocalTime currentTime() {
        return LocalTime.now(DEFAULT_ZONE_ID);
    }
    
    /**
     * 获取昨天日期
     * 
     * @return 昨天日期
     */
    public static LocalDate yesterday() {
        return today().minusDays(1);
    }
    
    /**
     * 获取明天日期
     * 
     * @return 明天日期
     */
    public static LocalDate tomorrow() {
        return today().plusDays(1);
    }
    
    /**
     * 获取本周第一天（周一）
     * 
     * @return 本周第一天
     */
    public static LocalDate firstDayOfWeek() {
        return today().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    }
    
    /**
     * 获取本周最后一天（周日）
     * 
     * @return 本周最后一天
     */
    public static LocalDate lastDayOfWeek() {
        return today().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
    }
    
    /**
     * 获取本月第一天
     * 
     * @return 本月第一天
     */
    public static LocalDate firstDayOfMonth() {
        return today().with(TemporalAdjusters.firstDayOfMonth());
    }
    
    /**
     * 获取本月最后一天
     * 
     * @return 本月最后一天
     */
    public static LocalDate lastDayOfMonth() {
        return today().with(TemporalAdjusters.lastDayOfMonth());
    }
    
    /**
     * 获取本年第一天
     * 
     * @return 本年第一天
     */
    public static LocalDate firstDayOfYear() {
        return today().with(TemporalAdjusters.firstDayOfYear());
    }
    
    /**
     * 获取本年最后一天
     * 
     * @return 本年最后一天
     */
    public static LocalDate lastDayOfYear() {
        return today().with(TemporalAdjusters.lastDayOfYear());
    }
    
    /**
     * 格式化日期时间为字符串
     * 
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_DATETIME_FORMATTER);
    }
    
    /**
     * 格式化日期时间为字符串
     * 
     * @param dateTime 日期时间
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 格式化日期为字符串
     * 
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date) {
        return date.format(DEFAULT_DATE_FORMATTER);
    }
    
    /**
     * 格式化日期为字符串
     * 
     * @param date 日期
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 格式化时间为字符串
     * 
     * @param time 时间
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time) {
        return time.format(DEFAULT_TIME_FORMATTER);
    }
    
    /**
     * 格式化时间为字符串
     * 
     * @param time 时间
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time, String pattern) {
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 将字符串解析为日期时间
     * 
     * @param text 日期时间字符串
     * @return 解析后的日期时间
     */
    public static LocalDateTime parseDateTime(String text) {
        return LocalDateTime.parse(text, DEFAULT_DATETIME_FORMATTER);
    }
    
    /**
     * 将字符串按照指定格式解析为日期时间
     * 
     * @param text 日期时间字符串
     * @param pattern 格式模式
     * @return 解析后的日期时间
     */
    public static LocalDateTime parseDateTime(String text, String pattern) {
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 将字符串解析为日期
     * 
     * @param text 日期字符串
     * @return 解析后的日期
     */
    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DEFAULT_DATE_FORMATTER);
    }
    
    /**
     * 将字符串按照指定格式解析为日期
     * 
     * @param text 日期字符串
     * @param pattern 格式模式
     * @return 解析后的日期
     */
    public static LocalDate parseDate(String text, String pattern) {
        return LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 将字符串解析为时间
     * 
     * @param text 时间字符串
     * @return 解析后的时间
     */
    public static LocalTime parseTime(String text) {
        return LocalTime.parse(text, DEFAULT_TIME_FORMATTER);
    }
    
    /**
     * 将字符串按照指定格式解析为时间
     * 
     * @param text 时间字符串
     * @param pattern 格式模式
     * @return 解析后的时间
     */
    public static LocalTime parseTime(String text, String pattern) {
        return LocalTime.parse(text, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * LocalDateTime转换为Date
     * 
     * @param dateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = dateTime.atZone(DEFAULT_ZONE_ID);
        return Date.from(zonedDateTime.toInstant());
    }
    
    /**
     * LocalDate转换为Date
     * 
     * @param date LocalDate
     * @return Date
     */
    public static Date toDate(LocalDate date) {
        return toDate(date.atStartOfDay());
    }
    
    /**
     * Date转换为LocalDateTime
     * 
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE_ID);
    }
    
    /**
     * Date转换为LocalDate
     * 
     * @param date Date
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }
    
    /**
     * 计算两个日期时间之间的差异（毫秒数）
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return 差异毫秒数
     */
    public static long diffMillis(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MILLIS.between(start, end);
    }
    
    /**
     * 计算两个日期时间之间的差异（秒数）
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return 差异秒数
     */
    public static long diffSeconds(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.SECONDS.between(start, end);
    }
    
    /**
     * 计算两个日期时间之间的差异（分钟数）
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return 差异分钟数
     */
    public static long diffMinutes(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }
    
    /**
     * 计算两个日期时间之间的差异（小时数）
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return 差异小时数
     */
    public static long diffHours(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }
    
    /**
     * 计算两个日期之间的差异（天数）
     * 
     * @param start 开始日期
     * @param end 结束日期
     * @return 差异天数
     */
    public static long diffDays(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
    
    /**
     * 计算两个日期时间之间的差异（天数）
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return 差异天数
     */
    public static long diffDays(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
    }
    
    /**
     * 计算两个日期之间的差异（月数）
     * 
     * @param start 开始日期
     * @param end 结束日期
     * @return 差异月数
     */
    public static long diffMonths(LocalDate start, LocalDate end) {
        return ChronoUnit.MONTHS.between(start, end);
    }
    
    /**
     * 计算两个日期之间的差异（年数）
     * 
     * @param start 开始日期
     * @param end 结束日期
     * @return 差异年数
     */
    public static long diffYears(LocalDate start, LocalDate end) {
        return ChronoUnit.YEARS.between(start, end);
    }
}
