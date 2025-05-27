# 精简版动态Excel导入导出框架

基于Spring Boot的轻量级动态Excel导入导出框架，采用数据库配置驱动，支持内存处理和业务条件覆盖。

## 🚀 核心特性

- **数据库配置**：配置存储在数据库中，支持动态修改
- **内存处理**：直接在内存中处理Excel文件，无需临时文件
- **统一条件**：模板下载、数据导入、数据导出都支持业务条件参数
- **智能下拉**：支持默认配置与业务条件的继承和覆盖机制
- **工具独立**：核心工具类完全独立，可直接复制使用

## 🏗️ 技术栈

- **后端框架**：Spring Boot 2.7.18
- **数据库**：MySQL 8.0+
- **ORM框架**：MyBatis
- **Excel处理**：Apache POI 5.2.3
- **JSON处理**：FastJSON 1.2.83

## 📦 项目结构

```
excel-framework-parent/
├── excel-framework-dependencies/     # 依赖管理
├── excel-framework-core/            # 核心模块
│   ├── src/main/java/com/framework/excel/
│   │   ├── ExcelFrameworkApplication.java
│   │   ├── config/                  # 配置类
│   │   ├── controller/              # 控制器
│   │   ├── service/                 # 服务层
│   │   ├── mapper/                  # 数据访问层
│   │   ├── entity/                  # 实体类
│   │   │   ├── business/            # 业务实体
│   │   │   ├── config/              # 配置实体
│   │   │   └── dto/                 # 传输对象
│   │   ├── util/excel/              # Excel工具包(可独立复制)
│   │   ├── enums/                   # 枚举定义
│   │   └── exception/               # 异常定义
│   └── src/main/resources/
│       ├── mapper/                  # MyBatis映射文件
│       ├── sql/                     # 数据库脚本
│       └── application.yml          # 配置文件
└── README.md
```

## 🛠️ 快速开始

### 1. 环境要求

- JDK 11+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库初始化

```bash
# 执行数据库初始化脚本
mysql -u root -p < excel-framework-core/src/main/resources/sql/init.sql
```

### 3. 配置数据库连接

修改 `excel-framework-core/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/excel_framework?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 启动应用

```bash
cd excel-framework-core
mvn spring-boot:run
```

应用将在 `http://localhost:8080/excel-framework` 启动。

## 📖 API 使用指南

### 1. 下载Excel模板

```bash
# 基础模板下载
curl -X POST "http://localhost:8080/excel-framework/api/excel/template/fault" \
     -H "Content-Type: application/json" \
     -d '{}' \
     --output fault_template.xlsx

# 带业务条件的模板下载
curl -X POST "http://localhost:8080/excel-framework/api/excel/template/fault" \
     -H "Content-Type: application/json" \
     -d '{
       "conditions": {"function_id": 1001},
       "dropdownParams": {"function_tree_id": 1001, "scope_type": 2},
       "visibleFields": ["code", "name", "classification_id"]
     }' \
     --output fault_template_filtered.xlsx
```

### 2. 导入Excel数据

```bash
# 基础数据导入
curl -X POST "http://localhost:8080/excel-framework/api/excel/import/fault" \
     -F "file=@fault_data.xlsx" \
     -F 'conditions={"conditions": {"function_id": 1001}}'

# 带默认值和验证的导入
curl -X POST "http://localhost:8080/excel-framework/api/excel/import/model" \
     -F "file=@model_data.xlsx" \
     -F 'conditions={
       "defaultValues": {"status": 1, "create_user": "system"},
       "validateOnly": false,
       "skipDuplicates": true
     }'
```

### 3. 导出Excel数据

```bash
# 基础数据导出
curl -X POST "http://localhost:8080/excel-framework/api/excel/export/fault" \
     -H "Content-Type: application/json" \
     -d '{
       "conditions": {"classification_id": 1},
       "orderBy": "create_time DESC",
       "limit": 1000
     }' \
     --output fault_export.xlsx

# 指定字段导出
curl -X POST "http://localhost:8080/excel-framework/api/excel/export/model" \
     -H "Content-Type: application/json" \
     -d '{
       "conditions": {"status": 1},
       "visibleFields": ["code", "name", "category_id", "description"],
       "orderBy": "priority DESC"
     }' \
     --output model_export.xlsx
```

## 🔧 配置说明

### 1. 模板配置

模板配置存储在 `excel_template_config` 表中：

```sql
INSERT INTO excel_template_config (
    template_key, template_name, table_name, sheet_name, 
    primary_key_fields, update_mode, business_config
) VALUES (
    'fault', '故障数据模板', 'fault', '故障数据', 
    'code', 'INSERT_OR_UPDATE', 
    '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "code"}'
);
```

### 2. 字段配置

字段配置存储在 `excel_field_config` 表中：

```sql
INSERT INTO excel_field_config (
    template_id, field_name, column_name, column_index, 
    data_type, required, visible, width, dropdown_config
) VALUES (
    1, 'classification_id', '故障分类', 4, 'LONG', FALSE, TRUE, 25,
    '{"type": "RELATED_TABLE", "tableName": "fault_classification", 
      "valueField": "id", "displayField": "name", "allowEmpty": true}'
);
```

### 3. 业务条件参数

`BusinessConditions` 支持以下参数：

- `conditions`: 业务查询条件
- `dropdownParams`: 下拉框参数覆盖
- `defaultValues`: 默认值设置
- `validateOnly`: 是否仅验证(导入用)
- `skipDuplicates`: 是否跳过重复(导入用)
- `visibleFields`: 可见字段(导出用)
- `orderBy`: 排序条件(导出用)
- `limit`: 行数限制(导出用)

## 🎯 使用场景示例

### 场景1：动态主键切换

```bash
# 使用故障编码作为主键导入
curl -X POST "/api/excel/import/fault" -F "file=@data.xlsx"

# 使用ID作为主键更新
curl -X POST "/api/excel/import/fault_by_id" -F "file=@data.xlsx"

# 使用名称作为主键仅更新分类
curl -X POST "/api/excel/import/fault_by_name" -F "file=@data.xlsx"
```

### 场景2：条件化下拉框

```bash
# 根据功能树ID过滤故障分类下拉框
curl -X POST "/api/excel/template/fault" \
     -d '{
       "conditions": {"function_id": 1001},
       "dropdownParams": {"function_tree_id": 1001, "scope_type": 2}
     }'
```

### 场景3：字段动态显示

```bash
# 导出时隐藏敏感字段
curl -X POST "/api/excel/export/model" \
     -d '{
       "conditions": {"status": 1},
       "visibleFields": ["code", "name", "category_id", "description"]
     }'
```

## 🔌 独立工具类

核心Excel处理工具类位于 `util/excel/` 包下，可以独立复制到其他项目使用：

- `ExcelTemplateGenerator`: Excel模板生成器
- `ExcelDataReader`: Excel数据读取器
- `ExcelDataWriter`: Excel数据写入器
- `DropdownResolver`: 下拉框解析器
- `DataConverter`: 数据转换器
- `ValidationUtils`: 验证工具类

## 📝 扩展开发

### 1. 添加新的业务表

1. 创建业务实体类
2. 在数据库中创建对应表
3. 配置Excel模板和字段
4. 即可使用统一的API进行操作

### 2. 自定义验证规则

在 `ValidationUtils` 中添加自定义验证方法：

```java
public static void validateCustomRule(Object value, ExcelFieldConfig field, Object rowIndex) {
    // 自定义验证逻辑
}
```

### 3. 扩展数据转换

在 `DataConverter` 中添加自定义转换器：

```java
private static Object applyCustomConverter(Object value, JSONObject converterConfig) {
    String type = converterConfig.getString("type");
    switch (type) {
        case "your_custom_type":
            return yourCustomConversion(value, converterConfig);
        // ...
    }
}
```

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 项目维护者：Framework Team
- 邮箱：framework@example.com
- 项目链接：https://github.com/your-org/excel-framework 