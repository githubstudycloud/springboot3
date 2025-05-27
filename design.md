1.示例为场景导入导出，场景模板下载，单表，有code,id，能可选根据某个字段为主键更新，也可能是name,id导出时有可能可选隐藏，后期可能增删字段，导出的excel字段能动态调整。还有一个是模型表和模型分类表，模型可以没有分类，或者属于某一个分类，也就是导出和导入的表格有下拉框，模型一样有name，id。工具类要写的更加好，能适配其他类似格式，请先给出你的设计让我确认。示例表其中2个如下，其余的你自己补全CREATE TABLE fault (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
code VARCHAR(64) NOT NULL COMMENT 'Hash code of function path + fault name',
name VARCHAR(128) NOT NULL COMMENT 'Fault name',
function_id BIGINT NOT NULL COMMENT 'Function ID',
classification_id BIGINT COMMENT 'Fault classification ID',
system_element_id BIGINT COMMENT 'System element ID',
create_time DATETIME NOT NULL,
update_time DATETIME NOT NULL,
UNIQUE KEY uk_code (code),
INDEX idx_function_id (function_id)
);

-- Fault Classification Table
CREATE TABLE fault_classification (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(128) NOT NULL COMMENT 'Classification name',
scope_type TINYINT NOT NULL COMMENT '1: Global, 2: Local',
function_tree_id BIGINT COMMENT 'Function/Domain ID if local',
create_time DATETIME NOT NULL,
update_time DATETIME NOT NULL,
INDEX idx_function_tree_id (function_tree_id)
);

根据你的需求，我设计一个更灵活、通用的Excel导入导出方案。让我先给出整体设计思路供你确认：## 🎯 整体设计方案

基于你的需求，我设计了一个**配置驱动**的动态Excel导入导出方案，相比之前的注解方案更加灵活和通用。

## 📋 核心架构设计

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   配置层         │    │   服务层         │    │   工具层         │
│                 │    │                 │    │                 │
│ ExcelTemplateConfig │──▶│ ExcelConfigService │──▶│ DynamicExcelUtils│
│ ExcelFieldConfig   │    │ ExcelDataService  │    │ DataConverter   │
│ DropdownProvider   │    │ ValidationService │    │ FieldValidator  │
│ PrimaryKeyStrategy │    │                   │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🏗️ 核心组件设计

### 1. 配置类设计
```java
// 模板配置
public class ExcelTemplateConfig {
    private String templateKey;        // 模板标识
    private Class<?> entityClass;      // 实体类
    private String tableName;          // 表名
    private String sheetName;          // Sheet名称
    private PrimaryKeyStrategy primaryKeyStrategy; // 主键策略
    private List<ExcelFieldConfig> fields;         // 字段配置
}

// 字段配置
public class ExcelFieldConfig {
    private String fieldName;          // 实体字段名
    private String columnName;         // Excel列名
    private Integer columnIndex;       // 列索引
    private DataType dataType;         // 数据类型
    private Boolean required;          // 是否必填
    private Boolean visible;           // 是否可见(导出时)
    private Integer width;             // 列宽
    private DropdownProvider dropdownProvider; // 下拉数据提供者
    private String dateFormat;         // 日期格式
    private List<FieldValidator> validators; // 验证器
}

// 主键策略
public class PrimaryKeyStrategy {
    private List<String> keyFields;    // 主键字段(支持复合主键)
    private UpdateMode updateMode;     // 更新模式
}

public enum UpdateMode {
    INSERT_ONLY,           // 仅插入
    UPDATE_ONLY,           // 仅更新  
    INSERT_OR_UPDATE       // 插入或更新
}
```

### 2. 下拉数据提供者接口
```java
public interface DropdownProvider {
    List<DropdownOption> getOptions(Object... params);
}

// 关联表下拉提供者
public class RelatedTableDropdownProvider implements DropdownProvider {
    private String tableName;         // 关联表名
    private String valueField;        // 值字段
    private String displayField;      // 显示字段
    private String whereClause;       // 查询条件
    private boolean allowEmpty;       // 是否允许为空
}

// 静态下拉提供者
public class StaticDropdownProvider implements DropdownProvider {
    private List<DropdownOption> options; // 静态选项
}
```

## 📝 配置方式

### 方案1：YAML配置文件
```yaml
excel:
  templates:
    fault:
      entityClass: com.example.entity.Fault
      tableName: fault
      sheetName: 故障数据
      primaryKeyStrategy:
        keyFields: ["code"]  # 可动态切换为 ["id"], ["name"] 等
        updateMode: INSERT_OR_UPDATE
      fields:
        - fieldName: id
          columnName: ID
          columnIndex: 0
          dataType: LONG
          visible: false    # 导出时隐藏
        - fieldName: code
          columnName: 故障编码
          columnIndex: 1
          dataType: STRING
          required: true
          visible: true
        - fieldName: name
          columnName: 故障名称
          columnIndex: 2
          dataType: STRING
          required: true
          visible: true
        - fieldName: classificationId
          columnName: 故障分类
          columnIndex: 3
          dataType: LONG
          dropdownProvider:
            type: RELATED_TABLE
            tableName: fault_classification
            valueField: id
            displayField: name
            allowEmpty: true
            
    model:
      entityClass: com.example.entity.Model
      tableName: model
      sheetName: 模型数据
      primaryKeyStrategy:
        keyFields: ["code"]
        updateMode: INSERT_OR_UPDATE
      fields:
        - fieldName: id
          columnName: ID
          visible: false
        - fieldName: code
          columnName: 模型编码
          required: true
        - fieldName: name
          columnName: 模型名称
          required: true
        - fieldName: categoryId
          columnName: 模型分类
          dropdownProvider:
            type: RELATED_TABLE
            tableName: model_category
            valueField: id
            displayField: name
            allowEmpty: true  # 可以没有分类
```

### 方案2：数据库配置表
```sql
-- 模板配置表
CREATE TABLE excel_template_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_key VARCHAR(64) NOT NULL,
    entity_class VARCHAR(255) NOT NULL,
    table_name VARCHAR(64) NOT NULL,
    sheet_name VARCHAR(64) NOT NULL,
    primary_key_fields JSON,
    update_mode VARCHAR(32) DEFAULT 'INSERT_OR_UPDATE',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_template_key (template_key)
);

-- 字段配置表
CREATE TABLE excel_field_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id BIGINT NOT NULL,
    field_name VARCHAR(64) NOT NULL,
    column_name VARCHAR(64) NOT NULL,
    column_index INT NOT NULL,
    data_type VARCHAR(32) NOT NULL,
    required BOOLEAN DEFAULT FALSE,
    visible BOOLEAN DEFAULT TRUE,
    width INT DEFAULT 15,
    dropdown_config JSON,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_template_field (template_id, field_name)
);
```

## 🚀 API设计

```java
@RestController
@RequestMapping("/api/excel")
public class DynamicExcelController {
    
    // 下载模板
    @GetMapping("/template/{templateKey}")
    void downloadTemplate(@PathVariable String templateKey,
                          @RequestParam(required = false) List<String> visibleFields);
    
    // 导入数据
    @PostMapping("/import/{templateKey}")
    ResponseEntity<ImportResult> importData(@PathVariable String templateKey,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam(required = false) String primaryKey);
    
    // 导出数据  
    @PostMapping("/export/{templateKey}")
    void exportData(@PathVariable String templateKey,
                   @RequestBody ExportRequest request);
    
    // 动态调整字段可见性
    @PutMapping("/config/{templateKey}/fields/visibility")
    ResponseEntity<Void> updateFieldVisibility(@PathVariable String templateKey,
                                             @RequestBody Map<String, Boolean> fieldVisibility);
    
    // 动态调整主键策略
    @PutMapping("/config/{templateKey}/primary-key")
    ResponseEntity<Void> updatePrimaryKeyStrategy(@PathVariable String templateKey,
                                                @RequestBody PrimaryKeyStrategy strategy);
}
```

## 💡 使用示例

```java
// 1. 获取配置并动态调整
ExcelTemplateConfig config = excelConfigService.getConfig("fault");

// 动态隐藏字段
config.setFieldVisible("id", false);
config.setFieldVisible("createTime", false);

// 切换主键策略
config.setPrimaryKeyFields(Arrays.asList("name")); // 从code切换到name

// 2. 导出模板
DynamicExcelUtils.exportTemplate(config, response);

// 3. 导入数据(根据name字段更新)
ImportResult result = DynamicExcelUtils.importData(file, config);

// 4. 导出数据(只导出可见字段)
List<String> visibleFields = Arrays.asList("code", "name", "classificationId");
DynamicExcelUtils.exportData(dataList, config, visibleFields, response);
```

## 🔧 补全的表结构

```sql
-- 模型表
CREATE TABLE model (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL COMMENT 'Model code',
    name VARCHAR(128) NOT NULL COMMENT 'Model name',
    category_id BIGINT COMMENT 'Model category ID',
    description TEXT COMMENT 'Model description',
    version VARCHAR(32) COMMENT 'Model version',
    status TINYINT DEFAULT 1 COMMENT '1: Active, 0: Inactive',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_code (code),
    INDEX idx_category_id (category_id)
);

-- 模型分类表
CREATE TABLE model_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT 'Category name',
    description TEXT COMMENT 'Category description',
    parent_id BIGINT COMMENT 'Parent category ID',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_name (name)
);
```

## ✨ 核心优势

1. **配置驱动**：无需修改代码，通过配置实现灵活调整
2. **动态主键**：支持code/id/name等不同字段作为更新主键
3. **字段控制**：运行时动态控制字段的显示/隐藏
4. **关联下拉**：自动从数据库获取下拉选项，支持可选分类
5. **高度复用**：一套工具类适配所有表结构
6. **扩展性强**：支持自定义验证器、转换器、下拉提供者

## 🎯 解决的具体需求

✅ **场景导入导出**：支持fault表的导入导出
✅ **主键灵活选择**：可选择code、id、name作为更新主键  
✅ **字段动态隐藏**：导出时可隐藏id、createTime等字段
✅ **模型分类关联**：model表支持关联model_category，可为空
✅ **下拉框支持**：分类字段自动生成下拉选项
✅ **通用工具类**：适配其他类似表结构
✅ **后期扩展**：支持动态增删字段

