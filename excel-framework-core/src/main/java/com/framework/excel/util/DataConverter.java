package com.framework.excel.util;

import com.framework.excel.config.ExcelFieldConfig;
import com.framework.excel.enums.DataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 数据转换工具类
 *
 * @author framework
 * @since 1.0.0
 */
@Slf4j
public class DataConverter {

    /**
     * 默认日期时间格式
     */
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 将Excel单元格值转换为指定类型
     *
     * @param cellValue   单元格值
     * @param fieldConfig 字段配置
     * @return 转换后的值
     */
    public static Object convertCellValue(Object cellValue, ExcelFieldConfig fieldConfig) {
        if (cellValue == null) {
            return getDefaultValue(fieldConfig);
        }

        String stringValue = String.valueOf(cellValue).trim();
        if (!StringUtils.hasText(stringValue)) {
            return getDefaultValue(fieldConfig);
        }

        try {
            return convertByDataType(stringValue, fieldConfig.getDataType(), fieldConfig.getDateFormat());
        } catch (Exception e) {
            log.warn("Failed to convert value '{}' to type {}: {}", 
                    stringValue, fieldConfig.getDataType(), e.getMessage());
            return getDefaultValue(fieldConfig);
        }
    }

    /**
     * 将对象值转换为Excel显示值
     *
     * @param value       对象值
     * @param fieldConfig 字段配置
     * @return Excel显示值
     */
    public static String convertToExcelValue(Object value, ExcelFieldConfig fieldConfig) {
        if (value == null) {
            return "";
        }

        try {
            DataType dataType = fieldConfig.getDataType();
            String dateFormat = fieldConfig.getDateFormat();

            switch (dataType) {
                case DATETIME:
                    if (value instanceof LocalDateTime) {
                        String format = StringUtils.hasText(dateFormat) ? dateFormat : DEFAULT_DATETIME_FORMAT;
                        return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(format));
                    }
                    break;
                case DATE:
                    if (value instanceof LocalDate) {
                        String format = StringUtils.hasText(dateFormat) ? dateFormat : DEFAULT_DATE_FORMAT;
                        return ((LocalDate) value).format(DateTimeFormatter.ofPattern(format));
                    }
                    break;
                case TIME:
                    if (value instanceof LocalTime) {
                        String format = StringUtils.hasText(dateFormat) ? dateFormat : DEFAULT_TIME_FORMAT;
                        return ((LocalTime) value).format(DateTimeFormatter.ofPattern(format));
                    }
                    break;
                default:
                    break;
            }

            return String.valueOf(value);
        } catch (Exception e) {
            log.warn("Failed to convert value '{}' to Excel format: {}", value, e.getMessage());
            return String.valueOf(value);
        }
    }

    /**
     * 根据数据类型转换值
     *
     * @param stringValue 字符串值
     * @param dataType    数据类型
     * @param dateFormat  日期格式
     * @return 转换后的值
     */
    private static Object convertByDataType(String stringValue, DataType dataType, String dateFormat) {
        switch (dataType) {
            case STRING:
                return stringValue;
            case INTEGER:
                return parseInteger(stringValue);
            case LONG:
                return parseLong(stringValue);
            case DOUBLE:
                return parseDouble(stringValue);
            case FLOAT:
                return parseFloat(stringValue);
            case BOOLEAN:
                return parseBoolean(stringValue);
            case BIGDECIMAL:
                return parseBigDecimal(stringValue);
            case DATETIME:
                return parseDateTime(stringValue, dateFormat);
            case DATE:
                return parseDate(stringValue, dateFormat);
            case TIME:
                return parseTime(stringValue, dateFormat);
            default:
                return stringValue;
        }
    }

    /**
     * 获取默认值
     *
     * @param fieldConfig 字段配置
     * @return 默认值
     */
    private static Object getDefaultValue(ExcelFieldConfig fieldConfig) {
        if (fieldConfig.getDefaultValue() != null) {
            return fieldConfig.getDefaultValue();
        }

        DataType dataType = fieldConfig.getDataType();
        switch (dataType) {
            case INTEGER:
                return 0;
            case LONG:
                return 0L;
            case DOUBLE:
                return 0.0;
            case FLOAT:
                return 0.0f;
            case BOOLEAN:
                return false;
            case BIGDECIMAL:
                return BigDecimal.ZERO;
            default:
                return null;
        }
    }

    private static Integer parseInteger(String value) {
        try {
            // 处理小数点（Excel数字可能带小数点）
            if (value.contains(".")) {
                return (int) Double.parseDouble(value);
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer value: " + value);
        }
    }

    private static Long parseLong(String value) {
        try {
            if (value.contains(".")) {
                return (long) Double.parseDouble(value);
            }
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid long value: " + value);
        }
    }

    private static Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid double value: " + value);
        }
    }

    private static Float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid float value: " + value);
        }
    }

    private static Boolean parseBoolean(String value) {
        String lowerValue = value.toLowerCase();
        if ("true".equals(lowerValue) || "1".equals(lowerValue) || "是".equals(value) || "yes".equals(lowerValue)) {
            return true;
        } else if ("false".equals(lowerValue) || "0".equals(lowerValue) || "否".equals(value) || "no".equals(lowerValue)) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid boolean value: " + value);
        }
    }

    private static BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid decimal value: " + value);
        }
    }

    private static LocalDateTime parseDateTime(String value, String dateFormat) {
        String format = StringUtils.hasText(dateFormat) ? dateFormat : DEFAULT_DATETIME_FORMAT;
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            // 尝试其他常见格式
            String[] commonFormats = {
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy/MM/dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm",
                    "yyyy/MM/dd HH:mm",
                    "yyyy-MM-dd",
                    "yyyy/MM/dd"
            };
            
            for (String commonFormat : commonFormats) {
                try {
                    if (commonFormat.contains("HH:mm")) {
                        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(commonFormat));
                    } else {
                        return LocalDate.parse(value, DateTimeFormatter.ofPattern(commonFormat)).atStartOfDay();
                    }
                } catch (DateTimeParseException ignored) {
                    // 继续尝试下一个格式
                }
            }
            
            throw new IllegalArgumentException("Invalid datetime value: " + value);
        }
    }

    private static LocalDate parseDate(String value, String dateFormat) {
        String format = StringUtils.hasText(dateFormat) ? dateFormat : DEFAULT_DATE_FORMAT;
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            // 尝试其他常见格式
            String[] commonFormats = {"yyyy-MM-dd", "yyyy/MM/dd", "MM/dd/yyyy", "dd/MM/yyyy"};
            
            for (String commonFormat : commonFormats) {
                try {
                    return LocalDate.parse(value, DateTimeFormatter.ofPattern(commonFormat));
                } catch (DateTimeParseException ignored) {
                    // 继续尝试下一个格式
                }
            }
            
            throw new IllegalArgumentException("Invalid date value: " + value);
        }
    }

    private static LocalTime parseTime(String value, String dateFormat) {
        String format = StringUtils.hasText(dateFormat) ? dateFormat : DEFAULT_TIME_FORMAT;
        try {
            return LocalTime.parse(value, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            // 尝试其他常见格式
            String[] commonFormats = {"HH:mm:ss", "HH:mm", "H:mm:ss", "H:mm"};
            
            for (String commonFormat : commonFormats) {
                try {
                    return LocalTime.parse(value, DateTimeFormatter.ofPattern(commonFormat));
                } catch (DateTimeParseException ignored) {
                    // 继续尝试下一个格式
                }
            }
            
            throw new IllegalArgumentException("Invalid time value: " + value);
        }
    }
}