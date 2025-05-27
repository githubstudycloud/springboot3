# Dynamic Excel Import Export Framework

## 项目概述

基于SpringBoot 2.7.18 + MyBatis + POI的动态Excel导入导出框架，支持配置驱动的灵活导入导出功能。

## 技术栈

- **JDK**: 8
- **SpringBoot**: 2.7.18
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis (dao + xml)
- **Excel处理**: Apache POI 5.2.4
- **构建工具**: Maven 3.6+

## 项目结构

```
excel-framework-parent/
├── README.md                           # 项目总体说明
├── pom.xml                            # 父级POM
├── excel-framework-dependencies/      # 依赖管理模块
│   └── pom.xml                       # 依赖版本统一管理
└── excel-framework-core/             # 核心框架模块
    ├── pom.xml                       # 核心模块POM
    ├── src/main/java/                # Java源码
    │   └── com/framework/excel/      # 包结构
    │       ├── config/               # 配置类
    │       ├── entity/               # 实体类
    │       ├── mapper/               # MyBatis Mapper
    │       ├── service/              # 服务层
    │       ├── controller/           # 控制器
    │       ├── util/                 # 工具类
    │       └── ExcelFrameworkApplication.java
    ├── src/main/resources/           # 资源文件
    │   ├── mapper/                   # MyBatis XML
    │   ├── application.yml           # 配置文件
    │   └── sql/                      # 数据库脚本
    └── src/test/                     # 测试代码
```

## 实施计划

### Step 1: 创建父项目和依赖管理模块 ✅
- 创建父项目 `excel-framework-parent`
- 创建依赖管理模块 `excel-framework-dependencies`
- 配置统一的版本管理和基础依赖

### Step 2: 创建核心框架模块基础结构
- 创建核心模块 `excel-framework-core`
- 配置SpringBoot启动类和基础配置
- 创建包结构和基础文件

### Step 3: 数据库设计和实体创建
- 设计配置表结构
- 创建实体类和示例表实体
- 配置MyBatis环境

### Step 4: 配置管理模块开发
- 开发ExcelTemplateConfig相关类
- 开发配置管理服务
- 实现配置的CRUD操作

### Step 5: 动态Excel工具类开发
- 开发DynamicExcelUtils核心工具类
- 实现模板生成功能
- 实现数据导入导出功能

### Step 6: 下拉数据提供者开发
- 开发DropdownProvider接口和实现
- 实现关联表下拉数据获取
- 集成到Excel工具中

### Step 7: Web API开发
- 开发REST控制器
- 实现模板下载、数据导入导出接口
- 添加全局异常处理

### Step 8: 验证和测试
- 创建完整的测试用例
- 验证fault表和model表的导入导出
- 性能测试和优化

## 核心特性

### 🎯 配置驱动
- 基于YAML配置或数据库配置
- 支持运行时动态调整字段
- 灵活的主键策略配置

### 📊 Excel功能
- 带下拉框的Excel模板生成
- 智能数据类型转换
- 批量数据导入导出
- 数据验证和错误提示

### 🔗 关联表支持
- 动态下拉选项从数据库获取
- 支持可选关联（如model可无分类）
- 复杂查询条件支持

### 🚀 高性能
- 流式读取大文件
- 分批处理数据
- 内存优化

### 🛠️ 高扩展性
- 插件化验证器
- 自定义数据转换器
- 灵活的主键策略

## 快速开始

1. **环境准备**
   ```bash
   # 确保JDK8和Maven已安装
   java -version
   mvn -version
   ```

2. **克隆项目**
   ```bash
   git clone <repository-url>
   cd excel-framework-parent
   ```

3. **数据库初始化**
   ```bash
   # 执行SQL脚本创建数据库和表
   mysql -u root -p < src/main/resources/sql/init.sql
   ```

4. **启动应用**
   ```bash
   cd excel-framework-core
   mvn spring-boot:run
   ```

5. **访问应用**
   ```
   http://localhost:8080/swagger-ui.html
   ```

## API文档

启动后访问 Swagger UI: `http://localhost:8080/swagger-ui.html`

### 主要接口

- `GET /api/excel/template/{templateKey}` - 下载Excel模板
- `POST /api/excel/import/{templateKey}` - 导入Excel数据
- `POST /api/excel/export/{templateKey}` - 导出Excel数据
- `PUT /api/excel/config/{templateKey}/fields/visibility` - 动态调整字段可见性

## 配置示例

```yaml
excel:
  templates:
    fault:
      entityClass: com.framework.excel.entity.Fault
      tableName: fault
      sheetName: 故障数据
      primaryKeyStrategy:
        keyFields: ["code"]
        updateMode: INSERT_OR_UPDATE
      fields:
        - fieldName: code
          columnName: 故障编码
          required: true
        - fieldName: classificationId
          columnName: 故障分类
          dropdownProvider:
            type: RELATED_TABLE
            tableName: fault_classification
            valueField: id
            displayField: name
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目使用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目链接: [https://github.com/your-username/excel-framework-parent](https://github.com/your-username/excel-framework-parent)
- 问题反馈: [Issues](https://github.com/your-username/excel-framework-parent/issues)
