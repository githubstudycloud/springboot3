# 精简版动态Excel导入导出框架 - 系统设计文档

## 📋 项目概述

### 1.1 项目背景
基于Spring Boot的轻量级动态Excel导入导出框架，采用数据库配置驱动，支持内存处理和业务条件覆盖。设计简洁，核心工具类独立，便于复制到其他项目使用。

### 1.2 核心特性
- **数据库配置**：配置存储在数据库中，支持动态修改
- **内存处理**：直接在内存中处理Excel文件，无需临时文件
- **统一条件**：模板下载、数据导入、数据导出都支持业务条件参数
- **智能下拉**：支持默认配置与业务条件的继承和覆盖机制
- **工具独立**：核心工具类完全独立，可直接复制使用

### 1.3 技术栈
- **后端框架**：Spring Boot 2.7.18
- **数据库**：MySQL 8.0+ 
- **ORM框架**：MyBatis (Dao + XML)
- **Excel处理**：Apache POI 5.2.3
- **JSON处理**：FastJSON 1.2.83

## 🏗️ 精简项目结构

### 2.1 最小化目录结构
```
excel-framework-core/
├── pom.xml
└── src/main/java/com/framework/excel/
    ├── ExcelFrameworkApplication.java     # 启动类
    ├── config/                           # 基础配置
    │   ├── MybatisConfig.java
    │   └── WebConfig.java
    ├── entity/                           # 核心实体
    │   ├── ExcelTemplateConfig.java      # 模板配置
    │   ├── ExcelFieldConfig.java         # 字段配置
    │   └── dto/                          # 传输对象
    │       ├── BusinessConditions.java   # 业务条件
    │       ├── ImportResult.java         # 导入结果
    │       └── DropdownOption.java       # 下拉选项
    ├── mapper/                           # 数据访问
    │   ├── ExcelConfigMapper.java        # 配置Mapper
    │   └── DynamicMapper.java            # 动态Mapper
    ├── service/                          # 业务服务
    │   └── ExcelService.java             # 统一Excel服务
    ├── controller/                       # 控制器
    │   └── ExcelController.java          # 统一控制器
    ├── util/                            # 独立工具包
    │   ├── excel/                       # Excel工具(可独立复制)
    │   │   ├── ExcelTemplateGenerator.java # 模板生成器
    │   │   ├── ExcelDataReader.java       # 数据读取器
    │   │   ├── ExcelDataWriter.java       # 数据写入器
    │   │   └── DropdownResolver.java      # 下拉解析器
    │   └── JsonUtils.java               # JSON工具
    └── enums/                           # 枚举定义
        ├── DataType.java
        └── UpdateMode.java
```

## 💾 完整数据库设计

### 3.1 业务表结构

#### 3.1.1 故障相关表
```sql
-- 故障表 (已提供)
CREATE TABLE fault (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL COMMENT 'Hash code of function path + fault name',
    name VARCHAR(128) NOT NULL COMMENT 'Fault name',
    function_id BIGINT NOT NULL COMMENT 'Function ID',
    classification_id BIGINT COMMENT 'Fault classification ID',
    system_element_id BIGINT COMMENT 'System element ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code),
    INDEX idx_function_id (function_id),
    INDEX idx_classification_id (classification_id)
) COMMENT='故障表';

-- 故障分类表 (已提供)
CREATE TABLE fault_classification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT 'Classification name',
    scope_type TINYINT NOT NULL COMMENT '1: Global, 2: Local',
    function_tree_id BIGINT COMMENT 'Function/Domain ID if local',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_function_tree_id (function_tree_id)
) COMMENT='故障分类表';
```

#### 3.1.2 模型相关表
```sql
-- 模型分类表
CREATE TABLE model_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT 'Category name',
    description TEXT COMMENT 'Category description',
    parent_id BIGINT COMMENT 'Parent category ID',
    level_no TINYINT DEFAULT 1 COMMENT 'Category level',
    sort_order INT DEFAULT 0 COMMENT 'Sort order',
    enabled BOOLEAN DEFAULT TRUE COMMENT 'Is enabled',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id),
    INDEX idx_enabled (enabled)
) COMMENT='模型分类表';

-- 模型表
CREATE TABLE model (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL COMMENT 'Model code',
    name VARCHAR(128) NOT NULL COMMENT 'Model name',
    category_id BIGINT COMMENT 'Model category ID (可为空)',
    description TEXT COMMENT 'Model description',
    version VARCHAR(32) COMMENT 'Model version',
    model_type VARCHAR(32) DEFAULT 'STANDARD' COMMENT 'Model type',
    status TINYINT DEFAULT 1 COMMENT '1: Active, 0: Inactive',
    priority TINYINT DEFAULT 5 COMMENT 'Priority level 1-10',
    tags JSON COMMENT 'Model tags',
    create_user VARCHAR(64) COMMENT 'Creator',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_model_type (model_type)
) COMMENT='模型表';
```

### 3.2 配置表结构 (精简版)
```sql
-- Excel模板配置表
CREATE TABLE excel_template_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_key VARCHAR(64) NOT NULL COMMENT '模板标识',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    table_name VARCHAR(64) NOT NULL COMMENT '表名',
    sheet_name VARCHAR(64) NOT NULL COMMENT 'Sheet名称',
    primary_key_fields VARCHAR(255) NOT NULL COMMENT '主键字段,逗号分隔',
    update_mode VARCHAR(32) DEFAULT 'INSERT_OR_UPDATE' COMMENT '更新模式',
    business_config TEXT COMMENT '业务扩展配置(JSON)',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_key (template_key)
) COMMENT='Excel模板配置表';

-- Excel字段配置表
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
    dropdown_config TEXT COMMENT '下拉配置(JSON)',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_field (template_id, field_name),
    FOREIGN KEY (template_id) REFERENCES excel_template_config(id) ON DELETE CASCADE
) COMMENT='Excel字段配置表';
```

### 3.3 示例配置数据

#### 3.3.1 故障模板配置
```sql
-- 故障模板配置
INSERT INTO excel_template_config (template_key, template_name, table_name, sheet_name, primary_key_fields, update_mode, business_config) VALUES
('fault', '故障数据模板', 'fault', '故障数据', 'code', 'INSERT_OR_UPDATE', '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "code", "requiredConditions": ["function_id"]}'),
('fault_by_id', '故障数据模板(按ID)', 'fault', '故障数据', 'id', 'INSERT_OR_UPDATE', '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "id"}'),
('fault_by_name', '故障数据模板(按名称)', 'fault', '故障数据', 'name', 'UPDATE_ONLY', '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "name"}');

-- 故障字段配置
INSERT INTO excel_field_config (template_id, field_name, column_name, column_index, data_type, required, visible, width, dropdown_config, sort_order) VALUES
-- 故障模板字段
(1, 'id', 'ID', 0, 'LONG', FALSE, FALSE, 10, NULL, 0),
(1, 'code', '故障编码', 1, 'STRING', TRUE, TRUE, 20, NULL, 1),
(1, 'name', '故障名称', 2, 'STRING', TRUE, TRUE, 30, NULL, 2),
(1, 'function_id', '功能ID', 3, 'LONG', TRUE, FALSE, 15, NULL, 3),
(1, 'classification_id', '故障分类', 4, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "fault_classification", "valueField": "id", "displayField": "name", "whereClause": "1=1", "allowEmpty": true}', 4),
(1, 'system_element_id', '系统元素ID', 5, 'LONG', FALSE, FALSE, 15, NULL, 5),

-- 故障模板(按ID)字段 - 复制配置但主键不同
(2, 'id', 'ID', 0, 'LONG', TRUE, TRUE, 10, NULL, 0),
(2, 'code', '故障编码', 1, 'STRING', FALSE, TRUE, 20, NULL, 1),
(2, 'name', '故障名称', 2, 'STRING', TRUE, TRUE, 30, NULL, 2),
(2, 'function_id', '功能ID', 3, 'LONG', TRUE, FALSE, 15, NULL, 3),
(2, 'classification_id', '故障分类', 4, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "fault_classification", "valueField": "id", "displayField": "name", "whereClause": "1=1", "allowEmpty": true}', 4),

-- 故障模板(按名称)字段 - 仅用于更新
(3, 'name', '故障名称', 0, 'STRING', TRUE, TRUE, 30, NULL, 0),
(3, 'classification_id', '故障分类', 1, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "fault_classification", "valueField": "id", "displayField": "name", "whereClause": "1=1", "allowEmpty": true}', 1),
(3, 'system_element_id', '系统元素ID', 2, 'LONG', FALSE, TRUE, 15, NULL, 2);
```

#### 3.3.2 模型模板配置
```sql
-- 模型模板配置
INSERT INTO excel_template_config (template_key, template_name, table_name, sheet_name, primary_key_fields, update_mode, business_config) VALUES
('model', '模型数据模板', 'model', '模型数据', 'code', 'INSERT_OR_UPDATE', '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "code", "supportNullCategory": true}'),
('model_simple', '模型简化模板', 'model', '模型数据', 'code', 'INSERT_ONLY', '{"allowedPrimaryKeys": ["code"], "visibleFields": ["code", "name", "category_id", "description"]}');

-- 模型字段配置
INSERT INTO excel_field_config (template_id, field_name, column_name, column_index, data_type, required, visible, width, dropdown_config, sort_order) VALUES
-- 完整模型模板
(4, 'id', 'ID', 0, 'LONG', FALSE, FALSE, 10, NULL, 0),
(4, 'code', '模型编码', 1, 'STRING', TRUE, TRUE, 20, NULL, 1),
(4, 'name', '模型名称', 2, 'STRING', TRUE, TRUE, 30, NULL, 2),
(4, 'category_id', '模型分类', 3, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "model_category", "valueField": "id", "displayField": "name", "whereClause": "enabled = 1", "allowEmpty": true}', 3),
(4, 'description', '描述', 4, 'STRING', FALSE, TRUE, 40, NULL, 4),
(4, 'version', '版本', 5, 'STRING', FALSE, TRUE, 15, NULL, 5),
(4, 'model_type', '模型类型', 6, 'STRING', FALSE, TRUE, 20, '{"type": "STATIC", "staticOptions": [{"value": "STANDARD", "label": "标准模型"}, {"value": "CUSTOM", "label": "自定义模型"}, {"value": "TEMPLATE", "label": "模板模型"}]}', 6),
(4, 'status', '状态', 7, 'INTEGER', FALSE, TRUE, 10, '{"type": "STATIC", "staticOptions": [{"value": 1, "label": "启用"}, {"value": 0, "label": "禁用"}]}', 7),
(4, 'priority', '优先级', 8, 'INTEGER', FALSE, TRUE, 10, NULL, 8),
(4, 'create_user', '创建人', 9, 'STRING', FALSE, FALSE, 15, NULL, 9),

-- 简化模型模板
(5, 'code', '模型编码', 0, 'STRING', TRUE, TRUE, 20, NULL, 0),
(5, 'name', '模型名称', 1, 'STRING', TRUE, TRUE, 30, NULL, 1),
(5, 'category_id', '模型分类', 2, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "model_category", "valueField": "id", "displayField": "name", "whereClause": "enabled = 1", "allowEmpty": true}', 2),
(5, 'description', '描述', 3, 'STRING', FALSE, TRUE, 40, NULL, 3);
```

### 3.4 示例测试数据

#### 3.4.1 故障分类测试数据
```sql
INSERT INTO fault_classification (name, scope_type, function_tree_id) VALUES
('电气故障', 1, NULL),
('机械故障', 1, NULL),
('软件故障', 1, NULL),
('传感器故障', 2, 1001),
('执行器故障', 2, 1001),
('控制器故障', 2, 1002),
('通讯故障', 1, NULL),
('安全故障', 1, NULL);
```

#### 3.4.2 故障测试数据
```sql
INSERT INTO fault (code, name, function_id, classification_id, system_element_id) VALUES
('FAULT_001', '电机过热故障', 1001, 1, 2001),
('FAULT_002', '传感器失效', 1002, 4, 2002),
('FAULT_003', '控制系统异常', 1003, 3, 2003),
('FAULT_004', '齿轮磨损', 1004, 2, 2004),
('FAULT_005', '液压泄漏', 1005, 2, 2005),
('FAULT_006', '温度传感器故障', 1001, 4, 2006),
('FAULT_007', '压力传感器故障', 1001, 4, 2007),
('FAULT_008', '通讯中断', 1002, 7, 2008),
('FAULT_009', '安全回路故障', 1003, 8, 2009),
('FAULT_010', '驱动器故障', 1004, 5, 2010);
```

#### 3.4.3 模型分类测试数据
```sql
INSERT INTO model_category (name, description, parent_id, level_no, sort_order, enabled) VALUES
('控制模型', '控制系统相关模型', NULL, 1, 1, TRUE),
('安全模型', '安全系统相关模型', NULL, 1, 2, TRUE),
('诊断模型', '故障诊断相关模型', NULL, 1, 3, TRUE),
('预测模型', '预测性维护模型', 3, 2, 1, TRUE),
('分类模型', '故障分类模型', 3, 2, 2, TRUE),
('监控模型', '实时监控模型', 1, 2, 1, TRUE),
('优化模型', '性能优化模型', 1, 2, 2, TRUE),
('风险评估', '风险评估模型', 2, 2, 1, TRUE);
```

#### 3.4.4 模型测试数据
```sql
INSERT INTO model (code, name, category_id, description, version, model_type, status, priority, create_user) VALUES
('MODEL_001', 'PID控制器模型', 6, '比例积分微分控制器模型', '1.0.0', 'STANDARD', 1, 8, 'admin'),
('MODEL_002', '故障检测模型', 4, '基于机器学习的故障检测模型', '2.1.0', 'CUSTOM', 1, 9, 'admin'),
('MODEL_003', '安全评估模型', 8, '系统安全性评估模型', '1.5.0', 'STANDARD', 1, 7, 'admin'),
('MODEL_004', '无分类模型', NULL, '未分类的测试模型', '1.0.0', 'TEMPLATE', 1, 5, 'admin'),
('MODEL_005', '预测维护模型', 4, '设备寿命预测模型', '3.0.0', 'CUSTOM', 1, 10, 'admin'),
('MODEL_006', '性能优化模型', 7, '系统性能实时优化', '2.0.0', 'STANDARD', 1, 6, 'admin'),
('MODEL_007', '温度监控模型', 6, '温度实时监控和报警', '1.2.0', 'STANDARD', 1, 7, 'admin'),
('MODEL_008', '振动分析模型', 5, '设备振动模式识别', '1.8.0', 'CUSTOM', 0, 6, 'admin'),
('MODEL_009', '能耗优化模型', 7, '系统能耗分析和优化', '2.5.0', 'STANDARD', 1, 8, 'admin'),
('MODEL_010', '质量检测模型', NULL, '产品质量自动检测', '1.1.0', 'TEMPLATE', 1, 4, 'admin');
```

### 3.5 配置使用示例

#### 3.5.1 动态主键切换示例
```bash
# 使用故障编码作为主键导入
curl -X POST "/api/excel/import/fault" \
     -F "file=@fault_data.xlsx" \
     -F 'conditions={"conditions": {"function_id": 1001}}'

# 使用ID作为主键导入(更新模式)
curl -X POST "/api/excel/import/fault_by_id" \
     -F "file=@fault_update.xlsx"

# 使用名称作为主键仅更新分类
curl -X POST "/api/excel/import/fault_by_name" \
     -F "file=@fault_classification_update.xlsx"
```

#### 3.5.2 字段动态显示示例
```bash
# 导出时隐藏敏感字段
curl -X POST "/api/excel/export/model" \
     -H "Content-Type: application/json" \
     -d '{
       "conditions": {"status": 1},
       "visibleFields": ["code", "name", "category_id", "description"],
       "dropdownParams": {"enabled": 1}
     }'

# 使用简化模板导出
curl -X POST "/api/excel/export/model_simple" \
     -H "Content-Type: application/json" \
     -d '{
       "conditions": {"model_type": "STANDARD"}
     }'
```

#### 3.5.3 业务条件过滤示例
```bash
# 根据功能树ID过滤故障分类下拉框
curl -X POST "/api/excel/template/fault" \
     -H "Content-Type: application/json" \
     -d '{
       "conditions": {"function_id": 1001},
       "dropdownParams": {"function_tree_id": 1001, "scope_type": 2}
     }'

# 根据分类层级过滤模型分类
curl -X POST "/api/excel/template/model" \
     -H "Content-Type: application/json" \
     -d '{
       "dropdownParams": {"enabled": 1, "level_no": 1}
     }'
```

## 🔧 核心实体设计

### 4.1 配置实体
```java
package com.framework.excel.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class ExcelTemplateConfig {
    private Long id;
    private String templateKey;
    private String templateName;
    private String tableName;
    private String sheetName;
    private String primaryKeyFields;
    private String updateMode;
    private String businessConfig;
    private Boolean enabled;
    private Date createTime;
    private Date updateTime;
    
    private List<ExcelFieldConfig> fields;
    
    public List<String> getPrimaryKeyFieldList() {
        return Arrays.asList(primaryKeyFields.split(","));
    }
    
    public JSONObject getBusinessConfigJson() {
        return businessConfig != null ? JSON.parseObject(businessConfig) : new JSONObject();
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
    private String dropdownConfig;
    private Integer sortOrder;
    private Date createTime;
    
    public DropdownConfig getDropdownConfigObject() {
        return dropdownConfig != null ? JSON.parseObject(dropdownConfig, DropdownConfig.class) : null;
    }
}

@Data
public class DropdownConfig {
    private String type;              // STATIC, RELATED_TABLE
    private String tableName;         // 关联表名
    private String valueField;        // 值字段
    private String displayField;      // 显示字段
    private String whereClause;       // 默认查询条件
    private Boolean allowEmpty;       // 是否允许为空
    private List<DropdownOption> staticOptions; // 静态选项
}
```

### 4.2 业务条件DTO
```java
package com.framework.excel.entity.dto;

import lombok.Data;
import java.util.Map;

@Data
public class BusinessConditions {
    private Map<String, Object> conditions;      // 业务查询条件
    private Map<String, Object> dropdownParams;  // 下拉框参数覆盖
    private Map<String, Object> defaultValues;   // 默认值设置
    private Boolean validateOnly = false;        // 是否仅验证(导入用)
    private Boolean skipDuplicates = false;      // 是否跳过重复(导入用)
    private List<String> visibleFields;          // 可见字段(导出用)
    private String orderBy;                      // 排序条件(导出用)
    private Integer limit;                       // 行数限制(导出用)
}

@Data
public class ImportResult {
    private Integer totalRows = 0;
    private Integer successCount = 0;
    private Integer errorCount = 0;
    private Integer skippedCount = 0;
    private List<ErrorInfo> errors = new ArrayList<>();
    
    @Data
    @AllArgsConstructor
    public static class ErrorInfo {
        private Integer row;
        private String message;
    }
}

@Data
public class DropdownOption {
    private Object value;
    private String label;
}
```

### 4.3 业务条件使用场景
```java
// 场景1：模板下载 - 根据部门生成专属下拉框
BusinessConditions templateConditions = new BusinessConditions();
templateConditions.setConditions(Map.of("departmentId", 1));
templateConditions.setDropdownParams(Map.of("departmentId", 1, "status", "active"));

// 场景2：数据导入 - 设置默认值和验证规则
BusinessConditions importConditions = new BusinessConditions();
importConditions.setConditions(Map.of("projectId", 100));
importConditions.setValidateOnly(false);
importConditions.setSkipDuplicates(true);
importConditions.setDefaultValues(Map.of("status", 1, "createUser", "system"));
importConditions.setDropdownParams(Map.of("departmentId", 1));

// 场景3：数据导出 - 查询条件和字段控制
BusinessConditions exportConditions = new BusinessConditions();
exportConditions.setConditions(Map.of("classificationId", 1, "status", "active"));
exportConditions.setVisibleFields(Arrays.asList("code", "name", "classificationId"));
exportConditions.setOrderBy("createTime DESC");
exportConditions.setLimit(1000);
```

## 🔌 精简API设计

### 5.1 统一Excel控制器
```java
package com.framework.excel.controller;

import com.framework.excel.entity.dto.BusinessConditions;
import com.framework.excel.entity.dto.ImportResult;
import com.framework.excel.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    
    @Autowired
    private ExcelService excelService;
    
    /**
     * 下载Excel模板(支持业务条件)
     */
    @PostMapping("/template/{templateKey}")
    public void downloadTemplate(
        @PathVariable String templateKey,
        @RequestBody(required = false) BusinessConditions conditions,
        HttpServletResponse response
    ) {
        try {
            byte[] templateData = excelService.generateTemplate(templateKey, conditions);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=" + templateKey + "_template.xlsx");
            response.getOutputStream().write(templateData);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new RuntimeException("模板下载失败", e);
        }
    }
    
    /**
     * 导入Excel数据(支持业务条件)
     */
    @PostMapping("/import/{templateKey}")
    public ResponseEntity<ImportResult> importData(
        @PathVariable String templateKey,
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "conditions", required = false) String conditionsJson
    ) {
        try {
            BusinessConditions conditions = parseConditions(conditionsJson);
            ImportResult result = excelService.importData(templateKey, file, conditions);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    /**
     * 导出Excel数据
     */
    @PostMapping("/export/{templateKey}")
    public void exportData(
        @PathVariable String templateKey,
        @RequestBody BusinessConditions conditions,
        HttpServletResponse response
    ) {
        try {
            byte[] excelData = excelService.exportData(templateKey, conditions);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=" + templateKey + "_export_" + System.currentTimeMillis() + ".xlsx");
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }
    
    private BusinessConditions parseConditions(String conditionsJson) {
        if (conditionsJson == null || conditionsJson.trim().isEmpty()) {
            return new BusinessConditions();
        }
        return JSON.parseObject(conditionsJson, BusinessConditions.class);
    }
}
```

## 🛠️ 独立工具类设计

### 6.1 Excel模板生成器(可独立复制)
```java
package com.framework.excel.util.excel;

import com.framework.excel.entity.ExcelTemplateConfig;
import com.framework.excel.entity.ExcelFieldConfig;
import com.framework.excel.entity.dto.BusinessConditions;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Excel模板生成器 - 可独立使用
 */
public class ExcelTemplateGenerator {
    
    private DropdownResolver dropdownResolver;
    
    public ExcelTemplateGenerator(DropdownResolver dropdownResolver) {
        this.dropdownResolver = dropdownResolver;
    }
    
    /**
     * 生成Excel模板
     */
    public byte[] generateTemplate(ExcelTemplateConfig config, BusinessConditions conditions) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(config.getSheetName());
            
            // 获取可见字段
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields());
            
            // 创建表头
            createHeader(sheet, workbook, visibleFields);
            
            // 添加下拉数据验证
            addDataValidations(sheet, visibleFields, conditions);
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("生成Excel模板失败", e);
        }
    }
    
    private List<ExcelFieldConfig> getVisibleFields(List<ExcelFieldConfig> fields) {
        return fields.stream()
                .filter(ExcelFieldConfig::getVisible)
                .sorted((a, b) -> a.getColumnIndex().compareTo(b.getColumnIndex()))
                .collect(Collectors.toList());
    }
    
    private void createHeader(Sheet sheet, Workbook workbook, List<ExcelFieldConfig> fields) {
        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        
        // 创建表头行
        Row headerRow = sheet.createRow(0);
        
        for (int i = 0; i < fields.size(); i++) {
            ExcelFieldConfig field = fields.get(i);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(field.getColumnName());
            cell.setCellStyle(headerStyle);
            
            // 设置列宽
            sheet.setColumnWidth(i, field.getWidth() * 256);
        }
    }
    
    private void addDataValidations(Sheet sheet, List<ExcelFieldConfig> fields, BusinessConditions conditions) {
        for (int i = 0; i < fields.size(); i++) {
            ExcelFieldConfig field = fields.get(i);
            if (field.getDropdownConfigObject() != null) {
                List<DropdownOption> options = dropdownResolver.resolveDropdownOptions(
                    field.getDropdownConfigObject(), conditions);
                
                if (options != null && !options.isEmpty()) {
                    String[] optionArray = options.stream()
                            .map(DropdownOption::getLabel)
                            .toArray(String[]::new);
                    
                    addDropdownValidation(sheet, i, optionArray);
                }
            }
        }
    }
    
    private void addDropdownValidation(Sheet sheet, int columnIndex, String[] options) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
        CellRangeAddressList range = new CellRangeAddressList(1, 1000, columnIndex, columnIndex);
        DataValidation validation = helper.createValidation(constraint, range);
        sheet.addValidationData(validation);
    }
}
```

### 6.2 下拉框解析器(支持业务条件覆盖)
```java
package com.framework.excel.util.excel;

import com.framework.excel.entity.DropdownConfig;
import com.framework.excel.entity.dto.BusinessConditions;
import com.framework.excel.entity.dto.DropdownOption;
import com.framework.excel.mapper.DynamicMapper;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * 下拉框解析器 - 支持配置继承和业务条件覆盖
 */
@Component
public class DropdownResolver {
    
    private DynamicMapper dynamicMapper;
    
    public DropdownResolver(DynamicMapper dynamicMapper) {
        this.dynamicMapper = dynamicMapper;
    }
    
    /**
     * 解析下拉选项(支持业务条件覆盖)
     */
    public List<DropdownOption> resolveDropdownOptions(DropdownConfig config, BusinessConditions conditions) {
        if (config.getType().equals("STATIC")) {
            return config.getStaticOptions();
        }
        
        if (config.getType().equals("RELATED_TABLE")) {
            return resolveRelatedTableOptions(config, conditions);
        }
        
        return null;
    }
    
    private List<DropdownOption> resolveRelatedTableOptions(DropdownConfig config, BusinessConditions conditions) {
        // 构建查询条件
        String whereClause = buildWhereClause(config, conditions);
        
        // 查询数据
        List<Map<String, Object>> dataList = dynamicMapper.selectDropdownOptions(
            config.getTableName(),
            config.getValueField(),
            config.getDisplayField(),
            whereClause
        );
        
        // 转换为下拉选项
        return dataList.stream()
                .map(row -> new DropdownOption(
                    row.get(config.getValueField()),
                    row.get(config.getDisplayField()).toString()
                ))
                .collect(Collectors.toList());
    }
    
    private String buildWhereClause(DropdownConfig config, BusinessConditions conditions) {
        StringBuilder whereClause = new StringBuilder();
        
        // 添加默认条件
        if (config.getWhereClause() != null && !config.getWhereClause().trim().isEmpty()) {
            whereClause.append(config.getWhereClause());
        }
        
        // 添加业务条件覆盖
        if (conditions != null && conditions.getDropdownParams() != null) {
            for (Map.Entry<String, Object> entry : conditions.getDropdownParams().entrySet()) {
                if (whereClause.length() > 0) {
                    whereClause.append(" AND ");
                }
                whereClause.append(entry.getKey())
                          .append(" = ")
                          .append(formatValue(entry.getValue()));
            }
        }
        
        return whereClause.toString();
    }
    
    private String formatValue(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value.toString();
    }
}
```

### 6.3 Excel数据读取器
```java
package com.framework.excel.util.excel;

import com.framework.excel.entity.ExcelTemplateConfig;
import com.framework.excel.entity.ExcelFieldConfig;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.*;

/**
 * Excel数据读取器 - 可独立使用
 */
public class ExcelDataReader {
    
    /**
     * 读取Excel数据
     */
    public List<Map<String, Object>> readExcelData(MultipartFile file, ExcelTemplateConfig config) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields());
            
            // 跳过标题行
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, Object> rowData = parseRow(row, visibleFields);
                if (!isEmptyRow(rowData)) {
                    dataList.add(rowData);
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("读取Excel文件失败", e);
        }
        
        return dataList;
    }
    
    private List<ExcelFieldConfig> getVisibleFields(List<ExcelFieldConfig> fields) {
        return fields.stream()
                .filter(ExcelFieldConfig::getVisible)
                .sorted((a, b) -> a.getColumnIndex().compareTo(b.getColumnIndex()))
                .collect(Collectors.toList());
    }
    
    private Map<String, Object> parseRow(Row row, List<ExcelFieldConfig> fields) {
        Map<String, Object> rowData = new HashMap<>();
        
        for (int i = 0; i < fields.size(); i++) {
            ExcelFieldConfig field = fields.get(i);
            Cell cell = row.getCell(i);
            Object value = getCellValue(cell, field);
            rowData.put(field.getFieldName(), value);
        }
        
        return rowData;
    }
    
    private Object getCellValue(Cell cell, ExcelFieldConfig field) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return cell.toString();
        }
    }
    
    private boolean isEmptyRow(Map<String, Object> rowData) {
        return rowData.values().stream().allMatch(value -> 
            value == null || value.toString().trim().isEmpty());
    }
}
```

## 🌐 API接口文档

### 7.1 模板下载(支持业务条件)
```http
POST /api/excel/template/{templateKey}
Content-Type: application/json
```

**请求体(可选)：**
```json
{
  "conditions": {
    "departmentId": 1,
    "projectId": 100
  },
  "dropdownParams": {
    "status": "active",
    "regionId": 2
  }
}
```

**响应：** Excel文件流

**示例：**
```bash
# 基础模板下载
curl -X POST "http://localhost:8080/api/excel/template/fault" \
     --output fault_template.xlsx

# 带业务条件的模板下载
curl -X POST "http://localhost:8080/api/excel/template/fault" \
     -H "Content-Type: application/json" \
     -d '{
       "conditions": {"departmentId": 1},
       "dropdownParams": {"status": "active"}
     }' \
     --output fault_template.xlsx
```

### 7.2 数据导入(支持业务条件)
```http
POST /api/excel/import/{templateKey}
Content-Type: multipart/form-data
```

**请求参数：**
- `templateKey` (path): 模板标识
- `file` (form-data): Excel文件
- `conditions` (form-data, optional): 业务条件参数，JSON字符串格式

**条件参数示例：**
```json
{
  "conditions": {
    "departmentId": 1,
    "projectId": 100
  },
  "validateOnly": false,
  "skipDuplicates": true,
  "defaultValues": {
    "status": 1,
    "createUser": "system"
  },
  "dropdownParams": {
    "regionId": 2,
    "categoryType": "technical"
  }
}
```

**响应：**
```json
{
  "totalRows": 100,
  "successCount": 95,
  "errorCount": 5,
  "skippedCount": 0,
  "errors": [
    {
      "row": 5,
      "message": "故障编码不能为空"
    }
  ]
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
     -F 'conditions={
       "conditions": {"departmentId": 1},
       "skipDuplicates": true,
       "defaultValues": {"status": 1},
       "dropdownParams": {"regionId": 2}
     }'
```

### 7.3 数据导出(支持业务条件)
```http
POST /api/excel/export/{templateKey}
Content-Type: application/json
```

**请求体：**
```json
{
  "conditions": {
    "classificationId": 1,
    "status": "active",
    "departmentId": 1
  },
  "visibleFields": ["code", "name", "classificationId"],
  "orderBy": "createTime DESC",
  "limit": 1000,
  "dropdownParams": {
    "regionId": 2,
    "categoryType": "technical"
  }
}
```

**响应：** Excel文件流

**示例：**
```bash
# 基础导出
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
       "conditions": {"classificationId": 1, "departmentId": 1},
       "visibleFields": ["code", "name", "classificationId"],
       "orderBy": "code ASC",
       "limit": 500,
       "dropdownParams": {"regionId": 2}
     }' \
     --output fault_export.xlsx
```

## 🎨 前端调用示例

### 8.1 Vue.js精简示例
```vue
<template>
  <div class="excel-manager">
    <!-- 模板下载 -->
    <div class="section">
      <h3>模板下载</h3>
      <el-form :model="templateForm" inline>
        <el-form-item label="部门">
          <el-select v-model="templateForm.departmentId">
            <el-option v-for="dept in departments" :key="dept.id" 
                      :label="dept.name" :value="dept.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="项目">
          <el-select v-model="templateForm.projectId">
            <el-option v-for="project in projects" :key="project.id" 
                      :label="project.name" :value="project.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="downloadTemplate" type="primary">下载模板</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据导入 -->
    <div class="section">
      <h3>数据导入</h3>
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
      </el-form>
      
      <el-upload
        :action="importUrl"
        :data="getImportData"
        :on-success="handleImportSuccess"
        accept=".xlsx,.xls">
        <el-button type="success">选择文件导入</el-button>
      </el-upload>
    </div>

    <!-- 数据导出 -->
    <div class="section">
      <h3>数据导出</h3>
      <el-form :model="exportForm" inline>
        <el-form-item label="分类">
          <el-select v-model="exportForm.classificationId">
            <el-option v-for="item in classifications" :key="item.id" 
                      :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="exportForm.status">
            <el-option label="启用" value="active"></el-option>
            <el-option label="禁用" value="inactive"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="exportData" type="warning">导出数据</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedTemplate: 'fault',
      departments: [],
      projects: [],
      classifications: [],
      templateForm: { 
        departmentId: null,
        projectId: null 
      },
      importForm: {
        validateOnly: false,
        skipDuplicates: true
      },
      exportForm: { 
        classificationId: null,
        status: null 
      }
    }
  },
  computed: {
    importUrl() {
      return `/api/excel/import/${this.selectedTemplate}`
    },
    getImportData() {
      return {
        conditions: JSON.stringify({
          conditions: {
            departmentId: this.templateForm.departmentId,
            projectId: this.templateForm.projectId
          },
          validateOnly: this.importForm.validateOnly,
          skipDuplicates: this.importForm.skipDuplicates,
          defaultValues: {
            status: 1,
            createUser: 'system'
          },
          dropdownParams: {
            departmentId: this.templateForm.departmentId,
            projectId: this.templateForm.projectId
          }
        })
      }
    }
  },
  methods: {
    async downloadTemplate() {
      const conditions = {
        conditions: {
          departmentId: this.templateForm.departmentId,
          projectId: this.templateForm.projectId
        },
        dropdownParams: {
          departmentId: this.templateForm.departmentId,
          projectId: this.templateForm.projectId,
          status: 'active'
        }
      }
      
      const response = await axios.post(`/api/excel/template/${this.selectedTemplate}`, 
        conditions, { responseType: 'blob' })
      
      this.downloadFile(response.data, `${this.selectedTemplate}_template.xlsx`)
    },
    
    async exportData() {
      const conditions = {
        conditions: { 
          classificationId: this.exportForm.classificationId,
          status: this.exportForm.status,
          departmentId: this.templateForm.departmentId
        },
        dropdownParams: {
          departmentId: this.templateForm.departmentId,
          projectId: this.templateForm.projectId
        }
      }
      
      const response = await axios.post(`/api/excel/export/${this.selectedTemplate}`, 
        conditions, { responseType: 'blob' })
      
      this.downloadFile(response.data, `${this.selectedTemplate}_export.xlsx`)
    },
    
    handleImportSuccess(response) {
      if (response.errorCount === 0) {
        this.$message.success(`导入成功：${response.successCount}条数据`)
      } else {
        this.$message.warning(`导入完成：成功${response.successCount}条，失败${response.errorCount}条`)
      }
    },
    
    downloadFile(data, filename) {
      const blob = new Blob([data])
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = filename
      a.click()
      window.URL.revokeObjectURL(url)
    }
  }
}
</script>
```

### 8.2 React精简示例
```jsx
import React, { useState, useEffect } from 'react';
import { Upload, Button, Select, Form, Switch, message } from 'antd';
import axios from 'axios';

const ExcelManager = () => {
  const [selectedTemplate] = useState('fault');
  const [departments, setDepartments] = useState([]);
  const [projects, setProjects] = useState([]);
  const [classifications, setClassifications] = useState([]);
  
  const [templateForm, setTemplateForm] = useState({
    departmentId: null,
    projectId: null
  });
  
  const [importForm, setImportForm] = useState({
    validateOnly: false,
    skipDuplicates: true
  });
  
  const [exportForm] = Form.useForm();

  // 下载模板
  const downloadTemplate = async () => {
    const conditions = {
      conditions: {
        departmentId: templateForm.departmentId,
        projectId: templateForm.projectId
      },
      dropdownParams: {
        departmentId: templateForm.departmentId,
        projectId: templateForm.projectId,
        status: 'active'
      }
    };
    
    try {
      const response = await axios.post(`/api/excel/template/${selectedTemplate}`, 
        conditions, { responseType: 'blob' });
      
      downloadFile(response.data, `${selectedTemplate}_template.xlsx`);
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
    
    // 添加业务条件
    const conditions = {
      conditions: {
        departmentId: templateForm.departmentId,
        projectId: templateForm.projectId
      },
      validateOnly: importForm.validateOnly,
      skipDuplicates: importForm.skipDuplicates,
      defaultValues: {
        status: 1,
        createUser: 'system'
      },
      dropdownParams: {
        departmentId: templateForm.departmentId,
        projectId: templateForm.projectId
      }
    };
    
    formData.append('conditions', JSON.stringify(conditions));

    try {
      const response = await axios.post(`/api/excel/import/${selectedTemplate}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      
      onSuccess(response.data);
      
      if (response.data.errorCount === 0) {
        message.success(`导入成功：${response.data.successCount}条数据`);
      } else {
        message.warning(`导入完成：成功${response.data.successCount}条，失败${response.data.errorCount}条`);
      }
    } catch (error) {
      onError(error);
      message.error('导入失败');
    }
  };

  // 导出数据
  const exportData = async (values) => {
    const conditions = {
      conditions: { 
        ...values,
        departmentId: templateForm.departmentId
      },
      dropdownParams: {
        departmentId: templateForm.departmentId,
        projectId: templateForm.projectId
      }
    };
    
    try {
      const response = await axios.post(`/api/excel/export/${selectedTemplate}`, 
        conditions, { responseType: 'blob' });
      
      downloadFile(response.data, `${selectedTemplate}_export.xlsx`);
      message.success('导出成功');
    } catch (error) {
      message.error('导出失败');
    }
  };

  const downloadFile = (data, filename) => {
    const blob = new Blob([data]);
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  return (
    <div style={{ padding: '20px' }}>
      {/* 业务条件设置 */}
      <div style={{ marginBottom: '20px', border: '1px solid #d9d9d9', padding: '16px' }}>
        <h3>业务条件设置</h3>
        <Form layout="inline">
          <Form.Item label="部门">
            <Select
              value={templateForm.departmentId}
              onChange={(value) => setTemplateForm({...templateForm, departmentId: value})}
              style={{ width: 200 }}
              placeholder="选择部门"
            >
              {departments.map(dept => (
                <Select.Option key={dept.id} value={dept.id}>
                  {dept.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="项目">
            <Select
              value={templateForm.projectId}
              onChange={(value) => setTemplateForm({...templateForm, projectId: value})}
              style={{ width: 200 }}
              placeholder="选择项目"
            >
              {projects.map(project => (
                <Select.Option key={project.id} value={project.id}>
                  {project.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </div>

      {/* 模板下载 */}
      <div style={{ marginBottom: '20px' }}>
        <h3>模板下载</h3>
        <Button type="primary" onClick={downloadTemplate}>
          下载模板
        </Button>
      </div>

      {/* 导入设置 */}
      <div style={{ marginBottom: '20px' }}>
        <h3>数据导入</h3>
        <Form layout="inline" style={{ marginBottom: '10px' }}>
          <Form.Item label="验证模式">
            <Switch
              checked={importForm.validateOnly}
              onChange={(checked) => setImportForm({...importForm, validateOnly: checked})}
              checkedChildren="仅验证"
              unCheckedChildren="导入数据"
            />
          </Form.Item>
          <Form.Item label="跳过重复">
            <Switch
              checked={importForm.skipDuplicates}
              onChange={(checked) => setImportForm({...importForm, skipDuplicates: checked})}
            />
          </Form.Item>
        </Form>
        
        <Upload
          customRequest={customUpload}
          accept=".xlsx,.xls"
          showUploadList={false}
        >
          <Button type="success">选择文件导入</Button>
        </Upload>
      </div>

      {/* 数据导出 */}
      <div>
        <h3>数据导出</h3>
        <Form form={exportForm} layout="inline" onFinish={exportData}>
          <Form.Item name="classificationId" label="分类">
            <Select style={{ width: 200 }} placeholder="选择分类">
              {classifications.map(item => (
                <Select.Option key={item.id} value={item.id}>
                  {item.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select style={{ width: 120 }} placeholder="选择状态">
              <Select.Option value="active">启用</Select.Option>
              <Select.Option value="inactive">禁用</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              导出数据
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
};

export default ExcelManager;
```

## 📊 核心优势

### 设计优势
1. **极简架构**：只保留必要功能，去除冗余配置
2. **统一条件支持**：模板下载、数据导入、数据导出都支持业务条件
3. **智能下拉框**：支持配置继承和业务条件覆盖机制
4. **工具独立**：核心工具类完全独立，可直接复制到其他项目
5. **内存处理**：高效的内存处理，适合中小规模数据

### 业务场景支持
1. **多租户场景**：通过departmentId、projectId等条件实现数据隔离
2. **权限控制**：下拉框根据用户权限动态显示可选项
3. **分级管理**：支持区域、部门、项目等多级业务条件
4. **动态模板**：同一模板在不同业务上下文中生成不同的下拉选项

### API设计统一性
```bash
# 三个核心接口都支持BusinessConditions
POST /api/excel/template/{templateKey}   # 模板下载
POST /api/excel/import/{templateKey}     # 数据导入  
POST /api/excel/export/{templateKey}     # 数据导出
```

### 条件参数完整性
```json
{
  "conditions": {},          // 业务查询条件
  "dropdownParams": {},      // 下拉框参数覆盖
  "defaultValues": {},       // 默认值设置
  "validateOnly": false,     // 验证模式(导入用)
  "skipDuplicates": true,    // 跳过重复(导入用) 
  "visibleFields": [],       // 可见字段(导出用)
  "orderBy": "",            // 排序条件(导出用)
  "limit": 1000            // 行数限制(导出用)
}
```

### 使用场景
- 需要根据业务条件动态生成模板和下拉框
- 多部门、多项目的数据管理场景
- 需要权限控制的Excel处理需求
- 需要快速集成到其他项目的场景
- 中小型企业的Excel数据处理需求

### 核心价值
- **统一接口设计**：三个接口都支持条件参数，使用一致
- **业务条件灵活**：支持复杂的业务场景和权限控制
- **下拉框智能**：默认配置 + 业务覆盖的双重机制
- **开发效率高**：精简设计，快速集成和理解
- **工具复用性强**：独立工具类，便于项目间复制
- **维护成本低**：最小化实现，易于维护和扩展

通过这个统一的条件支持机制，您可以灵活地控制Excel模板生成、数据导入和导出的全过程，满足各种复杂的业务需求和权限控制场景。