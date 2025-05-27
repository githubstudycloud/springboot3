# Excel配置管理使用指南

## 概述

本项目已完成改造，支持灵活的Excel导入模板配置管理，包括：

1. **每个导入模板单独一个文件** - 支持独立的YAML配置文件
2. **与配置文件放一起** - 支持在application.yml中配置
3. **数据库读取配置** - 支持从数据库动态读取配置
4. **混合配置模式** - 优先数据库，降级到文件配置

## 配置源类型

### 1. 文件配置源 (yaml_file)

#### 方式1：独立配置文件
在 `src/main/resources/templates/` 目录下创建独立的YAML文件：

```yaml
# fault.yml - 故障表配置
entity-class: com.framework.excel.entity.Fault
table-name: fault
sheet-name: 故障数据
description: 故障信息导入导出模板

primary-key-strategy:
  key-fields: ["code"]
  update-mode: INSERT_OR_UPDATE

fields:
  - field-name: code
    column-name: 故障编码
    data-type: STRING
    required: true
    visible: true
    width: 20
  - field-name: classificationId
    column-name: 故障分类
    data-type: LONG
    visible: true
    dropdown-provider:
      type: RELATED_TABLE
      table-name: fault_classification
      value-field: id
      display-field: name
      allow-empty: true
```

#### 方式2：application.yml配置
```yaml
excel:
  templates:
    fault:
      entity-class: com.framework.excel.entity.Fault
      table-name: fault
      # ... 其他配置
```

### 2. 数据库配置源 (database)

配置存储在数据库表中：
- `excel_template_config` - 模板配置表
- `excel_field_config` - 字段配置表

### 3. 混合配置源 (hybrid) - 默认

优先从数据库读取，数据库不可用时降级到文件配置。

## 配置切换

### 通过配置文件切换
```yaml
excel:
  framework:
    config-source: hybrid  # yaml_file, database, hybrid
```

### 通过API动态切换
```bash
# 切换到数据库配置
PUT /api/excel/config/source/database

# 切换到文件配置
PUT /api/excel/config/source/yaml_file

# 切换到混合配置
PUT /api/excel/config/source/hybrid
```

## API接口

### 配置管理
```bash
# 获取所有配置
GET /api/excel/config/list

# 获取启用的配置
GET /api/excel/config/enabled

# 获取指定配置
GET /api/excel/config/{templateKey}

# 保存配置
POST /api/excel/config

# 更新配置
PUT /api/excel/config/{templateKey}

# 删除配置
DELETE /api/excel/config/{templateKey}

# 启用/禁用配置
PUT /api/excel/config/{templateKey}/enable
PUT /api/excel/config/{templateKey}/disable
```

### 配置源管理
```bash
# 获取配置源状态
GET /api/excel/config/source/status

# 获取当前配置源类型
GET /api/excel/config/source/current

# 同步文件配置到数据库
POST /api/excel/config/sync

# 刷新配置缓存
POST /api/excel/config/refresh
```

## 使用示例

### 1. 创建新的导入模板

#### 方式1：创建独立配置文件
在 `templates/` 目录下创建 `product.yml`：

```yaml
entity-class: com.example.entity.Product
table-name: product
sheet-name: 产品数据
description: 产品信息导入导出模板

primary-key-strategy:
  key-fields: ["code"]
  update-mode: INSERT_OR_UPDATE

fields:
  - field-name: code
    column-name: 产品编码
    data-type: STRING
    required: true
    visible: true
  - field-name: name
    column-name: 产品名称
    data-type: STRING
    required: true
    visible: true
  - field-name: categoryId
    column-name: 产品分类
    data-type: LONG
    visible: true
    dropdown-provider:
      type: RELATED_TABLE
      table-name: product_category
      value-field: id
      display-field: name
      allow-empty: false
```

#### 方式2：通过API创建
```bash
POST /api/excel/config
Content-Type: application/json

{
  "templateKey": "product",
  "templateName": "产品模板",
  "entityClass": "com.example.entity.Product",
  "tableName": "product",
  "sheetName": "产品数据",
  "primaryKeyFields": ["code"],
  "updateMode": "INSERT_OR_UPDATE",
  "fields": [
    {
      "fieldName": "code",
      "columnName": "产品编码",
      "columnIndex": 0,
      "dataType": "STRING",
      "required": true,
      "visible": true,
      "width": 20
    }
  ]
}
```

### 2. 动态调整字段可见性

```bash
# 获取当前配置
GET /api/excel/config/fault

# 修改配置中的字段可见性
PUT /api/excel/config/fault
{
  "fields": [
    {
      "fieldName": "id",
      "visible": false  # 隐藏ID字段
    },
    {
      "fieldName": "createTime", 
      "visible": false  # 隐藏创建时间
    }
  ]
}
```

### 3. 切换主键策略

```bash
PUT /api/excel/config/fault
{
  "primaryKeyFields": ["name"],  # 从code切换到name
  "updateMode": "UPDATE_ONLY"    # 改为仅更新模式
}
```

### 4. 同步配置

```bash
# 将文件配置同步到数据库
POST /api/excel/config/sync

# 刷新文件配置缓存
POST /api/excel/config/refresh
```

## 配置字段说明

### 模板配置
- `templateKey`: 模板标识，唯一
- `templateName`: 模板名称
- `entityClass`: 实体类全限定名
- `tableName`: 数据库表名
- `sheetName`: Excel Sheet名称
- `primaryKeyFields`: 主键字段列表，支持复合主键
- `updateMode`: 更新模式 (INSERT_ONLY, UPDATE_ONLY, INSERT_OR_UPDATE)
- `description`: 模板描述
- `status`: 状态 (1-启用, 0-禁用)

### 字段配置
- `fieldName`: 实体字段名
- `columnName`: Excel列名
- `columnIndex`: 列索引(从0开始)
- `dataType`: 数据类型 (STRING, INTEGER, LONG, DOUBLE, DATE, DATETIME, BOOLEAN)
- `required`: 是否必填
- `visible`: 是否可见(导出时)
- `width`: 列宽
- `dateFormat`: 日期格式
- `dropdownConfig`: 下拉配置
- `validationRules`: 验证规则
- `sortOrder`: 排序顺序

### 下拉配置
```yaml
dropdown-provider:
  type: RELATED_TABLE        # 关联表类型
  table-name: fault_classification
  value-field: id           # 值字段
  display-field: name       # 显示字段
  allow-empty: true         # 是否允许为空
  where-clause: "status = 1" # 查询条件(可选)
```

## 最佳实践

1. **开发阶段**：使用文件配置，便于版本控制和快速调试
2. **生产环境**：使用混合配置，支持运行时动态调整
3. **配置管理**：定期同步文件配置到数据库，确保一致性
4. **性能优化**：合理使用缓存刷新，避免频繁重载配置

## 故障排除

### 配置不生效
1. 检查配置源类型设置
2. 验证配置文件语法
3. 查看应用日志

### 数据库连接失败
1. 检查数据库连接配置
2. 确认数据库表结构
3. 系统会自动降级到文件配置

### 文件配置加载失败
1. 检查文件路径配置
2. 验证YAML语法
3. 确认文件权限

通过以上改造，项目现在支持灵活的配置管理方式，满足不同场景的需求。 