package com.framework.excel.util;

import com.framework.excel.config.ExcelFieldConfig;
import com.framework.excel.enums.DataType;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * 数据转换工具测试
 *
 * @author framework
 * @since 1.0.0
 */
public class DataConverterTest {

    @Test
    public void testConvertCellValue() {
        // 测试字符串转换
        ExcelFieldConfig stringConfig = new ExcelFieldConfig();
        stringConfig.setDataType(DataType.STRING);
        
        Object result = DataConverter.convertCellValue("test", stringConfig);
        assertEquals("test", result);

        // 测试整数转换
        ExcelFieldConfig intConfig = new ExcelFieldConfig();
        intConfig.setDataType(DataType.INTEGER);
        
        result = DataConverter.convertCellValue("123", intConfig);
        assertEquals(123, result);

        // 测试小数转整数
        result = DataConverter.convertCellValue("123.45", intConfig);
        assertEquals(123, result);

        // 测试布尔值转换
        ExcelFieldConfig boolConfig = new ExcelFieldConfig();
        boolConfig.setDataType(DataType.BOOLEAN);
        
        result = DataConverter.convertCellValue("true", boolConfig);
        assertEquals(true, result);
        
        result = DataConverter.convertCellValue("1", boolConfig);
        assertEquals(true, result);
        
        result = DataConverter.convertCellValue("是", boolConfig);
        assertEquals(true, result);
        
        result = DataConverter.convertCellValue("false", boolConfig);
        assertEquals(false, result);
    }

    @Test
    public void testConvertToExcelValue() {
        // 测试日期时间转换
        ExcelFieldConfig dateTimeConfig = new ExcelFieldConfig();
        dateTimeConfig.setDataType(DataType.DATETIME);
        dateTimeConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30, 45);
        String result = DataConverter.convertToExcelValue(dateTime, dateTimeConfig);
        assertEquals("2023-12-25 10:30:45", result);

        // 测试普通对象转换
        ExcelFieldConfig stringConfig = new ExcelFieldConfig();
        stringConfig.setDataType(DataType.STRING);
        
        result = DataConverter.convertToExcelValue(123, stringConfig);
        assertEquals("123", result);
    }

    @Test
    public void testNullAndEmptyValues() {
        ExcelFieldConfig config = new ExcelFieldConfig();
        config.setDataType(DataType.STRING);
        
        // 测试null值
        Object result = DataConverter.convertCellValue(null, config);
        assertNull(result);
        
        // 测试空字符串
        result = DataConverter.convertCellValue("", config);
        assertNull(result);
        
        // 测试空白字符串
        result = DataConverter.convertCellValue("   ", config);
        assertNull(result);
    }

    @Test
    public void testDefaultValues() {
        // 测试整数默认值
        ExcelFieldConfig intConfig = new ExcelFieldConfig();
        intConfig.setDataType(DataType.INTEGER);
        
        Object result = DataConverter.convertCellValue(null, intConfig);
        assertEquals(0, result);
        
        // 测试布尔默认值
        ExcelFieldConfig boolConfig = new ExcelFieldConfig();
        boolConfig.setDataType(DataType.BOOLEAN);
        
        result = DataConverter.convertCellValue(null, boolConfig);
        assertEquals(false, result);
        
        // 测试自定义默认值
        intConfig.setDefaultValue(100);
        result = DataConverter.convertCellValue(null, intConfig);
        assertEquals(100, result);
    }
}