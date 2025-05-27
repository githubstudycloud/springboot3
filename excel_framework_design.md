# 简化版动态Excel导入导出框架 - 系统设计文档

## 📋 项目概述

### 1.1 项目背景
基于Spring Boot的轻量级动态Excel导入导出框架，采用数据库配置驱动，支持内存处理和业务扩展属性。适用于中小型项目的Excel数据处理需求。

### 1.2 核心特性
- **数据库配置**：配置存储在数据库中，支持动态修改
- **内存处理**：直接在内存中处理Excel文件，无需临时文件
- **业务扩展**：支持JSON扩展属性，适配不同业务场景
- **工具分离**：核心工具类独立，可复制到其他项目使用
- **类型转换**：灵活的数据类型转换机制

### 1.3 技术栈
- **后端框架**：Spring Boot 2.7.18
- **数据库**：MySQL 8.0+ 
- **ORM框架**：MyBatis (Dao + XML)
- **Excel处理**：Apache POI 5.2.3
- **JSON处理**：FastJSON 1.2.83
- **构建工具**：Maven 3.6+

## 🏗️ 项目结构

### 2.1 完整目录结构
```
excel-framework-parent/
├── pom.xml                                    # 父级POM
├── excel-framework-dependencies/             # 依赖管理
│   └── pom.xml
└── excel-framework-core/                     # 核心模块
    ├── pom.xml
    └── src/main/java/com/framework/excel/
        ├── ExcelFrameworkApplication.java     # 启动类
        ├── config/                           # 配置类
        │   ├── MybatisConfig.java
        │   ├── SwaggerConfig.java
        │   └── WebConfig.java
        ├── entity/                           # 实体类
        │   ├── config/                       # 配置相关实体
        │   │   ├── ExcelTemplateConfig.java
        │   │   ├── ExcelFieldConfig.java
        │   │   └── DropdownConfig.java
        │   ├── business/                     # 业务实体
        │   │   ├── Fault.java
        │   │   ├── FaultClassification.java
        │   │   ├── Model.java
        │   │   └── ModelCategory.java
        │   └── dto/                          # 数据传输对象
        │       ├── ImportResult.java
        │       ├── ExportRequest.java
        │       └── DropdownOption.java
        ├── mapper/                           # MyBatis Mapper
        │   ├── ExcelTemplateConfigMapper.java
        │   ├── ExcelFieldConfigMapper.java
        │   ├── DynamicMapper.java
        │   ├── FaultMapper.java
        │   └── ModelMapper.java
        ├── service/                          # 服务层
        │   ├── ExcelConfigService.java
        │   ├── ExcelDataService.java
        │   └── DropdownDataService.java
        ├── controller/                       # 控制器
        │   ├── ExcelController.java
        │   └── ConfigController.java
        ├── util/                            # 工具类
        │   ├── excel/                       # Excel工具包(可独立使用)
        │   │   ├── ExcelUtils.java          # 核心Excel工具类
        │   │   ├── ExcelReader.java         # Excel读取器
        │   │   ├── ExcelWriter.java         # Excel写入器
        │   │   ├── DataConverter.java       # 数据转换器
        │   │   └── ValidationUtils.java     # 验证工具类
        │   ├── JsonUtils.java               # JSON工具类
        │   └── TypeConverter.java           # 类型转换工具
        ├── enums/                           # 枚举类
        │   ├── DataType.java
        │   ├── UpdateMode.java
        │   └── DropdownType.java
        └── exception/                       # 异常类
            ├── ExcelProcessException.java
            ├── ConfigNotFoundException.java
            └── ValidationException.java
```

## 💾 数据库设计

### 3.1 配置表设计

#### Excel模板配置表
```sql
CREATE TABLE excel_template_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_key VARCHAR(64) NOT NULL COMMENT '模板标识',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    entity_class VARCHAR(255) NOT NULL COMMENT '实体类名',
    table_name VARCHAR(64) NOT NULL COMMENT '表名',
    sheet_name VARCHAR(64) NOT NULL COMMENT 'Sheet名称',
    primary_key_fields VARCHAR(255) NOT NULL COMMENT '主键字段,逗号分隔',
    update_mode VARCHAR(32) DEFAULT 'INSERT_OR_UPDATE' COMMENT '更新模式',
    business_config TEXT COMMENT '业务扩展配置(JSON)',
    description TEXT COMMENT '模板描述',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_key (template_key)
) COMMENT='Excel模板配置表';

CREATE TABLE excel_field_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id BIGINT NOT NULL COMMENT '模板ID',
    field_name VARCHAR(64) NOT NULL COMMENT '字段名称',
    column_name VARCHAR(64) NOT NULL COMMENT '列名称',
    column_index INT NOT NULL COMMENT '列索引',
    data_type VARCHAR(32) NOT NULL COMMENT '数据类型',
    required BOOLEAN DEFAULT FALSE COMMENT '是否必填',
    visible BOOLEAN DEFAULT TRUE COMMENT '是否可见',
    width INT DEFAULT 15 COMMENT '列宽',
    date_format VARCHAR(32) COMMENT '日期格式',
    dropdown_config TEXT COMMENT '下拉配置(JSON)',
    validation_rules TEXT COMMENT '验证规则(JSON)',
    extra_config TEXT COMMENT '扩展配置(JSON)',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_field (template_id, field_name),
    FOREIGN KEY (template_id) REFERENCES excel_template_config(id) ON DELETE CASCADE
) COMMENT='Excel字段配置表';
```

## 🔧 核心代码实现

### 4.1 实体类设计

#### 模板配置实体
```java
package com.framework.excel.entity.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ExcelTemplateConfig {
    private Long id;
    private String templateKey;
    private String templateName;
    private String entityClass;
    private String tableName;
    private String sheetName;
    private String primaryKeyFields;  // 逗号分隔的字段名
    private String updateMode;
    private String businessConfig;    // JSON格式的业务配置
    private String description;
    private Boolean enabled;
    private Date createTime;
    private Date updateTime;
    
    // 字段配置列表
    private List<ExcelFieldConfig> fields;
    
    // 获取主键字段列表
    public List<String> getPrimaryKeyFieldList() {
        return Arrays.asList(primaryKeyFields.split(","));
    }
    
    // 获取业务配置JSON对象
    public JSONObject getBusinessConfigJson() {
        return JSON.parseObject(businessConfig);
    }
    
    // 设置业务配置
    public void setBusinessConfigJson(JSONObject config) {
        this.businessConfig = config.toJSONString();
    }
}

@Data
public class ExcelFieldConfig {
    private Long id;
    private Long templateId;
    private String fieldName;
    private String columnName;
    private Integer columnIndex;
    private String dataType;
    private Boolean required;
    private Boolean visible;
    private Integer width;
    private String dateFormat;
    private String dropdownConfig;    // JSON格式
    private String validationRules;   // JSON格式
    private String extraConfig;       // JSON格式扩展配置
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
    
    // 获取下拉配置
    public DropdownConfig getDropdownConfigObject() {
        return JSON.parseObject(dropdownConfig, DropdownConfig.class);
    }
    
    // 获取扩展配置
    public JSONObject getExtraConfigJson() {
        return JSON.parseObject(extraConfig);
    }
}

@Data
public class DropdownConfig {
    private String type;              // RELATED_TABLE, STATIC
    private String tableName;         // 关联表名
    private String valueField;        // 值字段
    private String displayField;      // 显示字段
    private String whereClause;       // 查询条件
    private Boolean allowEmpty;       // 是否允许为空
    private List<DropdownOption> staticOptions; // 静态选项
}

@Data
public class ImportConditions {
    private Boolean validateOnly = false;       // 是否仅验证不导入
    private Boolean skipDuplicates = false;     // 是否跳过重复数据
    private Integer batchSize = 100;            // 批处理大小
    private Map<String, Object> defaultValues; // 默认值设置
    private Map<String, Object> businessParams; // 业务参数
}

@Data
public class ExportRequest {
    private Map<String, Object> conditions;     // 业务查询条件（必填）
    private List<String> visibleFields;         // 导出字段（可选）
    private String orderBy;                     // 排序条件（可选）
    private Integer limit;                      // 行数限制（可选）
}

@Data
public class ImportResult {
    private Integer totalRows = 0;
    private Integer successCount = 0;
    private Integer errorCount = 0;
    private Integer skippedCount = 0;
    private List<ErrorInfo> errors = new ArrayList<>();
    private List<WarningInfo> warnings = new ArrayList<>();
    
    public void incrementSuccessCount() {
        this.successCount++;
    }
    
    public void incrementSkippedCount() {
        this.skippedCount++;
    }
    
    public void addError(int row, String message) {
        this.errors.add(new ErrorInfo(row, message));
        this.errorCount++;
    }
    
    public void addWarning(int row, String message) {
        this.warnings.add(new WarningInfo(row, message));
    }
    
    @Data
    @AllArgsConstructor
    public static class ErrorInfo {
        private Integer row;
        private String message;
    }
    
    @Data
    @AllArgsConstructor
    public static class WarningInfo {
        private Integer row;
        private String message;
    }
}
```

### 4.2 核心Excel工具类(可独立使用)

```java
package com.framework.excel.util.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * Excel工具类 - 可独立使用
 */
public class ExcelUtils {
    
    /**
     * 生成Excel模板
     */
    public static byte[] generateTemplate(ExcelTemplateConfig config) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(config.getSheetName());
            
            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // 创建表头行
            Row headerRow = sheet.createRow(0);
            
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields());
            
            for (int i = 0; i < visibleFields.size(); i++) {
                ExcelFieldConfig field = visibleFields.get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(field.getColumnName());
                cell.setCellStyle(headerStyle);
                
                // 设置列宽
                sheet.setColumnWidth(i, field.getWidth() * 256);
                
                // 添加下拉数据验证
                addDataValidation(sheet, field, i);
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new ExcelProcessException("生成Excel模板失败", e);
        }
    }
    
    /**
     * 解析Excel文件
     */
    public static List<Map<String, Object>> parseExcel(MultipartFile file, ExcelTemplateConfig config) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields());
            
            // 跳过标题行，从第二行开始读取数据
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // 跳过标题行
            }
            
            int rowIndex = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, Object> rowData = parseRow(row, visibleFields, rowIndex);
                if (!isEmptyRow(rowData)) {
                    dataList.add(rowData);
                }
                rowIndex++;
            }
            
        } catch (Exception e) {
            throw new ExcelProcessException("解析Excel文件失败", e);
        }
        
        return dataList;
    }
    
    /**
     * 导出数据到Excel
     */
    public static byte[] exportData(List<Map<String, Object>> dataList, ExcelTemplateConfig config) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(config.getSheetName());
            
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields());
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < visibleFields.size(); i++) {
                ExcelFieldConfig field = visibleFields.get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(field.getColumnName());
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, field.getWidth() * 256);
            }
            
            // 填充数据
            for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
                Row dataRow = sheet.createRow(rowIndex + 1);
                Map<String, Object> rowData = dataList.get(rowIndex);
                
                for (int colIndex = 0; colIndex < visibleFields.size(); colIndex++) {
                    ExcelFieldConfig field = visibleFields.get(colIndex);
                    Cell cell = dataRow.createCell(colIndex);
                    
                    Object value = rowData.get(field.getFieldName());
                    setCellValue(cell, value, field);
                    cell.setCellStyle(dataStyle);
                }
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new ExcelProcessException("导出Excel文件失败", e);
        }
    }
    
    // 私有辅助方法
    private static List<ExcelFieldConfig> getVisibleFields(List<ExcelFieldConfig> fields) {
        return fields.stream()
                .filter(ExcelFieldConfig::getVisible)
                .sorted(Comparator.comparing(ExcelFieldConfig::getColumnIndex))
                .collect(Collectors.toList());
    }
    
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        
        return style;
    }
    
    private static void addDataValidation(Sheet sheet, ExcelFieldConfig field, int columnIndex) {
        if (field.getDropdownConfig() != null) {
            DropdownConfig dropdownConfig = field.getDropdownConfigObject();
            if (dropdownConfig != null && "STATIC".equals(dropdownConfig.getType())) {
                String[] options = dropdownConfig.getStaticOptions().stream()
                        .map(DropdownOption::getLabel)
                        .toArray(String[]::new);
                
                DataValidationHelper helper = sheet.getDataValidationHelper();
                DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
                CellRangeAddressList range = new CellRangeAddressList(1, 1000, columnIndex, columnIndex);
                DataValidation validation = helper.createValidation(constraint, range);
                sheet.addValidationData(validation);
            }
        }
    }
    
    // 其他辅助方法...
}
```

### 4.3 数据转换器

```java
package com.framework.excel.util.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据转换器
 */
public class DataConverter {
    
    /**
     * 根据字段配置转换数据
     */
    public static Object convertValue(Object value, ExcelFieldConfig fieldConfig) {
        if (value == null) {
            return null;
        }
        
        String dataType = fieldConfig.getDataType();
        
        // 处理扩展配置中的转换规则
        JSONObject extraConfig = fieldConfig.getExtraConfigJson();
        if (extraConfig != null && extraConfig.containsKey("converter")) {
            JSONObject converterConfig = extraConfig.getJSONObject("converter");
            return applyCustomConverter(value, converterConfig);
        }
        
        // 标准类型转换
        switch (dataType.toUpperCase()) {
            case "STRING":
                return convertToString(value);
            case "INTEGER":
                return convertToInteger(value);
            case "LONG":
                return convertToLong(value);
            case "DOUBLE":
                return convertToDouble(value);
            case "DATE":
                return convertToDate(value, fieldConfig.getDateFormat());
            case "DATETIME":
                return convertToDateTime(value, fieldConfig.getDateFormat());
            case "BOOLEAN":
                return convertToBoolean(value);
            default:
                return value;
        }
    }
    
    /**
     * 自定义转换器
     */
    private static Object applyCustomConverter(Object value, JSONObject converterConfig) {
        String type = converterConfig.getString("type");
        
        switch (type) {
            case "enum_mapping":
                return convertEnumMapping(value, converterConfig.getJSONObject("mapping"));
            case "scale":
                return convertScale(value, converterConfig.getDouble("factor"));
            case "prefix":
                return addPrefix(value, converterConfig.getString("prefix"));
            case "json_extract":
                return extractJsonField(value, converterConfig.getString("field"));
            default:
                return value;
        }
    }
    
    // 具体转换方法实现...
    private static String convertToString(Object value) {
        return value.toString().trim();
    }
    
    private static Integer convertToInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString().trim());
    }
    
    // 其他转换方法...
}
```

### 4.4 服务层实现

```java
package com.framework.excel.service;

import com.framework.excel.entity.config.ExcelTemplateConfig;
import com.framework.excel.mapper.ExcelTemplateConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcelConfigService {
    
    @Autowired
    private ExcelTemplateConfigMapper configMapper;
    
    @Autowired
    private ExcelFieldConfigMapper fieldConfigMapper;
    
    /**
     * 根据模板key获取完整配置
     */
    public ExcelTemplateConfig getConfigByKey(String templateKey) {
        ExcelTemplateConfig config = configMapper.selectByTemplateKey(templateKey);
        if (config == null) {
            throw new ConfigNotFoundException("模板配置不存在: " + templateKey);
        }
        
        // 加载字段配置
        List<ExcelFieldConfig> fields = fieldConfigMapper.selectByTemplateId(config.getId());
        config.setFields(fields);
        
        return config;
    }
    
    /**
     * 保存或更新配置
     */
    public void saveConfig(ExcelTemplateConfig config) {
        if (config.getId() == null) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
        
        // 更新字段配置
        fieldConfigMapper.deleteByTemplateId(config.getId());
        for (ExcelFieldConfig field : config.getFields()) {
            field.setTemplateId(config.getId());
            fieldConfigMapper.insert(field);
        }
    }
}

@Service
public class ExcelDataService {
    
    @Autowired
    private ExcelConfigService configService;
    
    @Autowired
    private DynamicMapper dynamicMapper;
    
    /**
     * 导入Excel数据
     */
    public ImportResult importData(String templateKey, MultipartFile file, String conditionsJson) {
        ExcelTemplateConfig config = configService.getConfigByKey(templateKey);
        
        // 解析导入条件
        ImportConditions conditions = parseImportConditions(conditionsJson);
        
        // 解析Excel文件
        List<Map<String, Object>> dataList = ExcelUtils.parseExcel(file, config);
        
        ImportResult result = new ImportResult();
        result.setTotalRows(dataList.size());
        
        // 如果只是验证模式
        if (conditions.getValidateOnly()) {
            return validateOnly(dataList, config, conditions);
        }
        
        for (int i = 0; i < dataList.size(); i++) {
            try {
                Map<String, Object> rowData = dataList.get(i);
                
                // 应用默认值
                applyDefaultValues(rowData, conditions.getDefaultValues());
                
                // 应用业务参数
                applyBusinessParams(rowData, conditions.getBusinessParams());
                
                // 处理行数据
                boolean processed = processRowData(rowData, config, conditions, i + 2);
                
                if (processed) {
                    result.incrementSuccessCount();
                } else {
                    result.incrementSkippedCount();
                }
            } catch (Exception e) {
                result.addError(i + 2, e.getMessage());
            }
        }
        
        return result;
    }
    
    /**
     * 导出Excel数据
     */
    public byte[] exportData(String templateKey, ExportRequest request) {
        ExcelTemplateConfig config = configService.getConfigByKey(templateKey);
        
        // 构建查询条件
        Map<String, Object> queryConditions = request.getConditions();
        
        // 确定导出字段
        List<String> exportFields = request.getVisibleFields();
        if (exportFields == null || exportFields.isEmpty()) {
            // 使用配置中的所有可见字段
            exportFields = config.getFields().stream()
                    .filter(ExcelFieldConfig::getVisible)
                    .map(ExcelFieldConfig::getFieldName)
                    .collect(Collectors.toList());
        }
        
        // 确定排序条件
        String orderBy = request.getOrderBy();
        if (StringUtils.isEmpty(orderBy)) {
            orderBy = "id DESC"; // 默认排序
        }
        
        // 确定查询限制
        Integer limit = request.getLimit();
        if (limit == null || limit <= 0) {
            limit = 50000; // 系统默认限制
        }
        
        // 查询数据
        List<Map<String, Object>> dataList = dynamicMapper.selectByConditions(
                config.getTableName(), 
                queryConditions, 
                exportFields, 
                orderBy, 
                limit
        );
        
        // 处理关联数据转换
        processExportData(dataList, config);
        
        // 生成Excel文件
        return ExcelUtils.exportData(dataList, config, exportFields);
    }
    
    /**
     * 解析导入条件
     */
    private ImportConditions parseImportConditions(String conditionsJson) {
        if (StringUtils.isEmpty(conditionsJson)) {
            return new ImportConditions(); // 返回默认条件
        }
        
        try {
            return JSON.parseObject(conditionsJson, ImportConditions.class);
        } catch (Exception e) {
            throw new ValidationException("导入条件参数格式错误", e);
        }
    }
    
    /**
     * 处理行数据
     */
    private boolean processRowData(Map<String, Object> rowData, ExcelTemplateConfig config, 
                                 ImportConditions conditions, int rowIndex) {
        // 数据转换和验证
        Map<String, Object> convertedData = new HashMap<>();
        
        for (ExcelFieldConfig field : config.getFields()) {
            Object value = rowData.get(field.getFieldName());
            
            // 数据转换
            Object convertedValue = DataConverter.convertValue(value, field);
            
            // 数据验证
            ValidationUtils.validateField(convertedValue, field, rowIndex);
            
            convertedData.put(field.getFieldName(), convertedValue);
        }
        
        // 检查重复数据
        if (conditions.getSkipDuplicates() && isDuplicateData(convertedData, config)) {
            return false; // 跳过重复数据
        }
        
        // 处理业务扩展配置
        processBusinessConfig(convertedData, config);
        
        // 根据更新模式执行数据库操作
        executeDbOperation(convertedData, config);
        
        return true;
    }
    
    /**
     * 应用默认值
     */
    private void applyDefaultValues(Map<String, Object> rowData, Map<String, Object> defaultValues) {
        if (defaultValues != null) {
            for (Map.Entry<String, Object> entry : defaultValues.entrySet()) {
                String fieldName = entry.getKey();
                Object defaultValue = entry.getValue();
                
                // 如果字段值为空，应用默认值
                if (rowData.get(fieldName) == null || StringUtils.isEmpty(rowData.get(fieldName).toString())) {
                    rowData.put(fieldName, defaultValue);
                }
            }
        }
    }
    
    /**
     * 应用业务参数
     */
    private void applyBusinessParams(Map<String, Object> rowData, Map<String, Object> businessParams) {
        if (businessParams != null) {
            rowData.putAll(businessParams);
        }
    }
    
    /**
     * 检查重复数据
     */
    private boolean isDuplicateData(Map<String, Object> data, ExcelTemplateConfig config) {
        List<String> keyFields = config.getPrimaryKeyFieldList();
        Map<String, Object> keyConditions = new HashMap<>();
        
        for (String keyField : keyFields) {
            keyConditions.put(keyField, data.get(keyField));
        }
        
        int count = dynamicMapper.countByConditions(config.getTableName(), keyConditions);
        return count > 0;
    }
}
```

## 🌐 API接口文档

### 5.1 Excel操作接口

#### 5.1.1 下载Excel模板
```http
GET /api/excel/template/{templateKey}
```

**请求参数：**
- `templateKey` (path): 模板标识

**响应：** Excel文件流

**示例：**
```bash
curl -X GET "http://localhost:8080/api/excel/template/fault" \
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" \
     --output fault_template.xlsx
```

#### 5.1.2 导入Excel数据
```http
POST /api/excel/import/{templateKey}
Content-Type: multipart/form-data
```

**请求参数：**
- `templateKey` (path): 模板标识
- `file` (form-data): Excel文件
- `conditions` (form-data, optional): 导入条件参数，JSON字符串格式

**条件参数说明：**
```json
{
  "validateOnly": false,           // 是否仅验证不导入
  "skipDuplicates": true,         // 是否跳过重复数据
  "batchSize": 100,               // 批处理大小
  "defaultValues": {              // 默认值设置
    "status": 1,
    "createUser": "system"
  },
  "businessParams": {             // 业务参数
    "departmentId": 1,
    "projectId": 100
  }
}
```

**响应：**
```json
{
  "success": true,
  "message": "导入完成",
  "data": {
    "totalRows": 100,
    "successCount": 95,
    "errorCount": 5,
    "skippedCount": 0,
    "errors": [
      {
        "row": 5,
        "message": "故障编码不能为空"
      },
      {
        "row": 12,
        "message": "故障分类不存在"
      }
    ],
    "warnings": [
      {
        "row": 8,
        "message": "数据已存在，已跳过"
      }
    ]
  }
}
```

**示例：**
```bash
# 基础导入
curl -X POST "http://localhost:8080/api/excel/import/fault" \
     -H "Content-Type: multipart/form-data" \
     -F "file=@fault_data.xlsx"

# 带条件参数的导入
curl -X POST "http://localhost:8080/api/excel/import/fault" \
     -H "Content-Type: multipart/form-data" \
     -F "file=@fault_data.xlsx" \
     -F 'conditions={"skipDuplicates": true, "defaultValues": {"status": 1}}'
```

#### 5.1.3 导出Excel数据
```http
POST /api/excel/export/{templateKey}
Content-Type: application/json
```

**请求体：**
```json
{
  "conditions": {                 // 业务查询条件（必填）
    "classificationId": 1,
    "status": "active",
    "createTimeStart": "2024-01-01",
    "createTimeEnd": "2024-12-31"
  },
  "visibleFields": ["code", "name", "classificationId"],  // 可选，不传导出全部可见字段
  "orderBy": "createTime DESC",                            // 可选，不传使用默认排序
  "limit": 1000                                           // 可选，不传使用系统默认限制
}
```

**参数说明：**
- `conditions` (必填): 业务查询条件，根据业务需求传入
- `visibleFields` (可选): 指定导出字段，不传时导出配置中所有可见字段
- `orderBy` (可选): 排序条件，不传时使用 "id DESC" 默认排序
- `limit` (可选): 导出行数限制，不传时使用系统配置的默认值

**响应：** Excel文件流

**示例：**
```bash
# 最简导出（只传业务条件）
curl -X POST "http://localhost:8080/api/excel/export/fault" \
     -H "Content-Type: application/json" \
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" \
     -d '{
       "conditions": {"classificationId": 1}
     }' \
     --output fault_export.xlsx

# 完整参数导出
curl -X POST "http://localhost:8080/api/excel/export/fault" \
     -H "Content-Type: application/json" \
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" \
     -d '{
       "conditions": {"classificationId": 1, "status": "active"},
       "visibleFields": ["code", "name", "classificationId"],
       "orderBy": "code ASC",
       "limit": 500
     }' \
     --output fault_export.xlsx

# 导出全部数据（无业务条件限制）
curl -X POST "http://localhost:8080/api/excel/export/fault" \
     -H "Content-Type: application/json" \
     -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" \
     -d '{
       "conditions": {}
     }' \
     --output fault_all_export.xlsx
```

### 5.2 配置管理接口

#### 5.2.1 获取配置列表
```http
GET /api/excel/config/list
```

**响应：**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "templateKey": "fault",
      "templateName": "故障数据模板",
      "entityClass": "com.framework.excel.entity.Fault",
      "tableName": "fault",
      "sheetName": "故障数据",
      "enabled": true
    }
  ]
}
```

#### 5.2.2 获取指定配置
```http
GET /api/excel/config/{templateKey}
```

**响应：**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "templateKey": "fault",
    "templateName": "故障数据模板",
    "entityClass": "com.framework.excel.entity.Fault",
    "tableName": "fault",
    "sheetName": "故障数据",
    "primaryKeyFields": "code",
    "updateMode": "INSERT_OR_UPDATE",
    "businessConfig": "{\"version\": \"v1.0\", \"customFields\": [\"priority\"]}",
    "fields": [
      {
        "fieldName": "code",
        "columnName": "故障编码",
        "columnIndex": 0,
        "dataType": "STRING",
        "required": true,
        "visible": true,
        "width": 20,
        "extraConfig": "{\"validator\": {\"pattern\": \"^[A-Z0-9]+$\"}}"
      }
    ]
  }
}
```

#### 5.2.3 保存配置
```http
POST /api/excel/config
Content-Type: application/json
```

**请求体：**
```json
{
  "templateKey": "product",
  "templateName": "产品数据模板",
  "entityClass": "com.example.entity.Product",
  "tableName": "product",
  "sheetName": "产品数据",
  "primaryKeyFields": "code",
  "updateMode": "INSERT_OR_UPDATE",
  "businessConfig": "{\"version\": \"v1.0\", \"department\": \"sales\"}",
  "fields": [
    {
      "fieldName": "code",
      "columnName": "产品编码",
      "columnIndex": 0,
      "dataType": "STRING",
      "required": true,
      "visible": true,
      "width": 20,
      "extraConfig": "{\"converter\": {\"type\": \"prefix\", \"prefix\": \"PRD-\"}}"
    }
  ]
}
```

## 🎨 前端调用示例

### 6.1 Vue.js示例

```vue
<template>
  <div class="excel-manager">
    <!-- 模板下载 -->
    <div class="template-section">
      <h3>Excel模板下载</h3>
      <el-select v-model="selectedTemplate" placeholder="选择模板">
        <el-option 
          v-for="template in templates" 
          :key="template.templateKey"
          :label="template.templateName" 
          :value="template.templateKey">
        </el-option>
      </el-select>
      <el-button @click="downloadTemplate" type="primary">下载模板</el-button>
    </div>

    <!-- 文件导入 -->
    <div class="import-section">
      <h3>数据导入</h3>
      
      <!-- 导入条件设置 -->
      <el-form :model="importForm" inline style="margin-bottom: 10px;">
        <el-form-item label="验证模式">
          <el-switch v-model="importForm.validateOnly" 
                    active-text="仅验证" 
                    inactive-text="导入数据">
          </el-switch>
        </el-form-item>
        <el-form-item label="跳过重复">
          <el-switch v-model="importForm.skipDuplicates"></el-switch>
        </el-form-item>
        <el-form-item label="默认状态">
          <el-select v-model="importForm.defaultValues.status" placeholder="选择默认状态">
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <el-upload
        ref="upload"
        :action="importUrl"
        :auto-upload="false"
        :data="uploadData"
        :on-change="handleFileChange"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        accept=".xlsx,.xls">
        <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
        <el-button 
          style="margin-left: 10px;" 
          size="small" 
          type="success" 
          @click="submitUpload">
          上传导入
        </el-button>
      </el-upload>
    </div>

    <!-- 数据导出 -->
    <div class="export-section">
      <h3>数据导出</h3>
      <el-form :model="exportForm" inline>
        <!-- 业务条件 -->
        <el-form-item label="分类">
          <el-select v-model="exportForm.conditions.classificationId" 
                    placeholder="选择分类" clearable>
            <el-option 
              v-for="item in classifications" 
              :key="item.id"
              :label="item.name" 
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="exportForm.conditions.status" 
                    placeholder="选择状态" clearable>
            <el-option label="启用" value="1"></el-option>
            <el-option label="禁用" value="0"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 可选参数 -->
        <el-form-item label="导出字段">
          <el-input v-model="exportFieldsText" 
                   placeholder="留空导出全部字段，多个字段用逗号分隔"
                   style="width: 200px;">
          </el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="exportForm.orderBy" 
                   placeholder="如：createTime DESC"
                   style="width: 150px;">
          </el-input>
        </el-form-item>
        <el-form-item label="限制条数">
          <el-input-number v-model="exportForm.limit" 
                          :min="1" 
                          :max="50000"
                          placeholder="不填使用默认值">
          </el-input-number>
        </el-form-item>
        
        <el-form-item>
          <el-button @click="exportData" type="success">导出数据</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 导入结果显示 -->
    <el-dialog title="导入结果" :visible.sync="resultVisible" width="60%">
      <div v-if="importResult">
        <p>总行数: {{ importResult.totalRows }}</p>
        <p>成功: {{ importResult.successCount }}</p>
        <p>失败: {{ importResult.errorCount }}</p>
        
        <el-table v-if="importResult.errors.length > 0" :data="importResult.errors">
          <el-table-column prop="row" label="行号" width="80"></el-table-column>
          <el-table-column prop="message" label="错误信息"></el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ExcelManager',
  data() {
    return {
      selectedTemplate: 'fault',
      templates: [],
      classifications: [],
      importForm: {
        validateOnly: false,
        skipDuplicates: true,
        defaultValues: {},
        businessParams: {}
      },
      exportForm: {
        conditions: {},
        visibleFields: [],
        orderBy: '',
        limit: null
      },
      importResult: null,
      resultVisible: false
    }
  },
  computed: {
    importUrl() {
      return `/api/excel/import/${this.selectedTemplate}`
    },
    uploadData() {
      // 构建导入条件
      const conditions = {
        validateOnly: this.importForm.validateOnly,
        skipDuplicates: this.importForm.skipDuplicates,
        defaultValues: this.importForm.defaultValues,
        businessParams: this.importForm.businessParams
      }
      return {
        conditions: JSON.stringify(conditions)
      }
    },
    exportFieldsText: {
      get() {
        return this.exportForm.visibleFields ? this.exportForm.visibleFields.join(',') : ''
      },
      set(value) {
        this.exportForm.visibleFields = value ? value.split(',').map(f => f.trim()) : []
      }
    }
  },
  mounted() {
    this.loadTemplates()
    this.loadClassifications()
  },
  methods: {
    // 加载模板列表
    async loadTemplates() {
      try {
        const response = await axios.get('/api/excel/config/list')
        this.templates = response.data.data
      } catch (error) {
        this.$message.error('加载模板列表失败')
      }
    },

    // 加载分类数据
    async loadClassifications() {
      try {
        const response = await axios.get('/api/data/fault-classifications')
        this.classifications = response.data.data
      } catch (error) {
        this.$message.error('加载分类数据失败')
      }
    },

    // 下载模板
    async downloadTemplate() {
      if (!this.selectedTemplate) {
        this.$message.warning('请选择模板')
        return
      }

      try {
        const response = await axios.get(`/api/excel/template/${this.selectedTemplate}`, {
          responseType: 'blob'
        })
        
        const blob = new Blob([response.data])
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${this.selectedTemplate}_template.xlsx`
        a.click()
        window.URL.revokeObjectURL(url)
        
        this.$message.success('模板下载成功')
      } catch (error) {
        this.$message.error('模板下载失败')
      }
    },

    // 处理文件选择
    handleFileChange(file, fileList) {
      // 可以在这里做文件格式验证
      const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
      if (!isExcel) {
        this.$message.error('只能上传Excel文件')
        return false
      }
    },

    // 提交上传
    submitUpload() {
      if (!this.selectedTemplate) {
        this.$message.warning('请选择模板')
        return
      }
      this.$refs.upload.submit()
    },

    // 导入成功回调
    handleImportSuccess(response) {
      this.importResult = response.data
      this.resultVisible = true
      
      if (response.data.errorCount === 0) {
        this.$message.success('导入成功')
      } else {
        this.$message.warning(`导入完成，${response.data.errorCount}条数据失败`)
      }
    },

    // 导入失败回调
    handleImportError(error) {
      this.$message.error('导入失败: ' + error.message)
    },

    // 导出数据
    async exportData() {
      try {
        // 构建导出请求
        const exportRequest = {
          conditions: this.exportForm.conditions || {}
        }
        
        // 可选参数
        if (this.exportForm.visibleFields && this.exportForm.visibleFields.length > 0) {
          exportRequest.visibleFields = this.exportForm.visibleFields
        }
        
        if (this.exportForm.orderBy) {
          exportRequest.orderBy = this.exportForm.orderBy
        }
        
        if (this.exportForm.limit) {
          exportRequest.limit = this.exportForm.limit
        }

        const response = await axios.post(`/api/excel/export/${this.selectedTemplate}`, exportRequest, {
          responseType: 'blob'
        })
        
        const blob = new Blob([response.data])
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${this.selectedTemplate}_export_${new Date().getTime()}.xlsx`
        a.click()
        window.URL.revokeObjectURL(url)
        
        this.$message.success('导出成功')
      } catch (error) {
        this.$message.error('导出失败：' + (error.response?.data?.message || error.message))
      }
    }
  }
}
</script>
```

### 6.2 React示例

```jsx
import React, { useState, useEffect } from 'react';
import { Upload, Button, Select, Table, Modal, Form, Switch, InputNumber, Input, message } from 'antd';
import { UploadOutlined, DownloadOutlined } from '@ant-design/icons';
import axios from 'axios';

const ExcelManager = () => {
  const [templates, setTemplates] = useState([]);
  const [selectedTemplate, setSelectedTemplate] = useState('fault');
  const [classifications, setClassifications] = useState([]);
  const [importResult, setImportResult] = useState(null);
  const [resultVisible, setResultVisible] = useState(false);
  const [importForm] = Form.useForm();
  const [exportForm] = Form.useForm();

  // 导入条件状态
  const [importConditions, setImportConditions] = useState({
    validateOnly: false,
    skipDuplicates: true,
    defaultValues: {},
    businessParams: {}
  });

  useEffect(() => {
    loadTemplates();
    loadClassifications();
  }, []);

  // 加载模板列表
  const loadTemplates = async () => {
    try {
      const response = await axios.get('/api/excel/config/list');
      setTemplates(response.data.data);
    } catch (error) {
      message.error('加载模板列表失败');
    }
  };

  // 加载分类数据
  const loadClassifications = async () => {
    try {
      const response = await axios.get('/api/data/fault-classifications');
      setClassifications(response.data.data);
    } catch (error) {
      message.error('加载分类数据失败');
    }
  };

  // 下载模板
  const downloadTemplate = async () => {
    if (!selectedTemplate) {
      message.warning('请选择模板');
      return;
    }

    try {
      const response = await axios.get(`/api/excel/template/${selectedTemplate}`, {
        responseType: 'blob'
      });
      
      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${selectedTemplate}_template.xlsx`;
      a.click();
      window.URL.revokeObjectURL(url);
      
      message.success('模板下载成功');
    } catch (error) {
      message.error('模板下载失败');
    }
  };

  // 自定义上传
  const customUpload = async (options) => {
    const { file, onSuccess, onError } = options;
    
    const formData = new FormData();
    formData.append('file', file);
    
    // 添加导入条件
    formData.append('conditions', JSON.stringify(importConditions));

    try {
      const response = await axios.post(`/api/excel/import/${selectedTemplate}`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      
      setImportResult(response.data.data);
      setResultVisible(true);
      onSuccess(response.data);
      
      if (response.data.data.errorCount === 0) {
        message.success('导入成功');
      } else {
        message.warning(`导入完成，${response.data.data.errorCount}条数据失败`);
      }
    } catch (error) {
      onError(error);
      message.error('导入失败：' + (error.response?.data?.message || error.message));
    }
  };

  // 导出数据
  const exportData = async (values) => {
    try {
      // 构建导出请求
      const exportRequest = {
        conditions: values || {}
      };
      
      // 处理可选参数
      if (values.visibleFields) {
        exportRequest.visibleFields = values.visibleFields.split(',').map(f => f.trim()).filter(f => f);
      }
      
      if (values.orderBy) {
        exportRequest.orderBy = values.orderBy;
      }
      
      if (values.limit) {
        exportRequest.limit = values.limit;
      }

      const response = await axios.post(`/api/excel/export/${selectedTemplate}`, exportRequest, {
        responseType: 'blob'
      });
      
      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${selectedTemplate}_export_${new Date().getTime()}.xlsx`;
      a.click();
      window.URL.revokeObjectURL(url);
      
      message.success('导出成功');
    } catch (error) {
      message.error('导出失败：' + (error.response?.data?.message || error.message));
    }
  };

  const errorColumns = [
    {
      title: '行号',
      dataIndex: 'row',
      key: 'row'
    },
    {
      title: '错误信息',
      dataIndex: 'message',
      key: 'message'
    }
  ];

  return (
    <div style={{ padding: '20px' }}>
      {/* 模板选择和下载 */}
      <div style={{ marginBottom: '20px' }}>
        <h3>Excel模板下载</h3>
        <Select
          value={selectedTemplate}
          onChange={setSelectedTemplate}
          style={{ width: 200, marginRight: 10 }}
          placeholder="选择模板"
        >
          {templates.map(template => (
            <Select.Option key={template.templateKey} value={template.templateKey}>
              {template.templateName}
            </Select.Option>
          ))}
        </Select>
        <Button 
          type="primary" 
          icon={<DownloadOutlined />}
          onClick={downloadTemplate}
        >
          下载模板
        </Button>
      </div>

      {/* 导入条件设置 */}
      <div style={{ marginBottom: '20px' }}>
        <h3>导入设置</h3>
        <Form layout="inline">
          <Form.Item label="验证模式">
            <Switch
              checked={importConditions.validateOnly}
              onChange={(checked) => setImportConditions({...importConditions, validateOnly: checked})}
              checkedChildren="仅验证"
              unCheckedChildren="导入数据"
            />
          </Form.Item>
          <Form.Item label="跳过重复">
            <Switch
              checked={importConditions.skipDuplicates}
              onChange={(checked) => setImportConditions({...importConditions, skipDuplicates: checked})}
            />
          </Form.Item>
          <Form.Item label="默认状态">
            <Select
              value={importConditions.defaultValues?.status}
              onChange={(value) => setImportConditions({
                ...importConditions, 
                defaultValues: {...importConditions.defaultValues, status: value}
              })}
              style={{ width: 120 }}
              placeholder="选择状态"
              allowClear
            >
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </div>

      {/* 文件导入 */}
      <div style={{ marginBottom: '20px' }}>
        <h3>数据导入</h3>
        <Upload
          customRequest={customUpload}
          accept=".xlsx,.xls"
          showUploadList={false}
        >
          <Button icon={<UploadOutlined />}>选择文件并导入</Button>
        </Upload>
      </div>

      {/* 数据导出 */}
      <div style={{ marginBottom: '20px' }}>
        <h3>数据导出</h3>
        <Form form={exportForm} layout="inline" onFinish={exportData}>
          {/* 业务条件 */}
          <Form.Item name="classificationId" label="分类">
            <Select style={{ width: 200 }} allowClear placeholder="选择分类">
              {classifications.map(item => (
                <Select.Option key={item.id} value={item.id}>
                  {item.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select style={{ width: 120 }} allowClear placeholder="选择状态">
              <Select.Option value="1">启用</Select.Option>
              <Select.Option value="0">禁用</Select.Option>
            </Select>
          </Form.Item>
          
          {/* 可选参数 */}
          <Form.Item name="visibleFields" label="导出字段">
            <Input 
              style={{ width: 200 }} 
              placeholder="留空导出全部，多个用逗号分隔"
            />
          </Form.Item>
          <Form.Item name="orderBy" label="排序">
            <Input 
              style={{ width: 150 }} 
              placeholder="如：createTime DESC"
            />
          </Form.Item>
          <Form.Item name="limit" label="限制条数">
            <InputNumber 
              min={1} 
              max={50000}
              placeholder="不填使用默认值"
            />
          </Form.Item>
          
          <Form.Item>
            <Button type="primary" htmlType="submit">
              导出数据
            </Button>
          </Form.Item>
        </Form>
      </div>

      {/* 导入结果弹窗 */}
      <Modal
        title="导入结果"
        visible={resultVisible}
        onCancel={() => setResultVisible(false)}
        footer={null}
        width={600}
      >
        {importResult && (
          <div>
            <p>总行数: {importResult.totalRows}</p>
            <p>成功: {importResult.successCount}</p>
            <p>失败: {importResult.errorCount}</p>
            
            {importResult.errors && importResult.errors.length > 0 && (
              <Table
                columns={errorColumns}
                dataSource={importResult.errors}
                pagination={false}
                size="small"
              />
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default ExcelManager;
```

## 🎯 业务扩展配置示例

### 7.1 模板级别扩展配置
```json
{
  "version": "v2.0",
  "department": "manufacturing",
  "autoMapping": {
    "enabled": true,
    "mappingTable": "fault_code_mapping",
    "sourceField": "old_code",
    "targetField": "new_code"
  },
  "validation": {
    "crossFieldRules": [
      {
        "rule": "priority_category_check",
        "fields": ["priority", "categoryId"],
        "condition": "priority > 3 && categoryId == null"
      }
    ]
  }
}
```

### 7.2 字段级别扩展配置
```json
{
  "converter": {
    "type": "enum_mapping",
    "mapping": {
      "高": "HIGH",
      "中": "MEDIUM", 
      "低": "LOW"
    }
  },
  "validator": {
    "pattern": "^[A-Z]{2,3}-\\d{4}$",
    "message": "格式应为：AA-0000"
  },
  "businessLogic": {
    "autoGenerate": {
      "enabled": true,
      "pattern": "FAULT-{date:yyyyMM}-{seq:0000}"
    }
  }
}
```

## 📊 总结

### 优势特点
1. **简化架构**：去除复杂的缓存和配置源切换，直接数据库配置
2. **内存处理**：高效的内存处理方式，适合中小规模数据
3. **工具独立**：核心Excel工具类可独立使用，便于复制到其他项目
4. **业务扩展**：灵活的JSON扩展配置，支持不同业务需求
5. **类型转换**：强大的数据类型转换机制，支持自定义转换规则

### 适用场景
- 中小型企业的Excel数据处理需求
- 需要快速集成Excel功能的项目
- 业务变化频繁，需要灵活配置的场景
- 多项目间需要复用Excel处理能力

通过这个简化版框架，您可以快速实现Excel的导入导出功能，同时保持良好的扩展性和维护性。