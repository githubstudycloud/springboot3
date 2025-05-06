# Platform Data Governance - 数据治理框架

## 概述

Platform Data Governance 是一个全面的数据治理框架，专注于数据标准化、元数据管理、数据质量控制和数据清洗规则管理。该框架基于领域驱动设计(DDD)和六边形架构，为企业数据资产提供统一的治理能力。

## 核心特性

- **元数据管理**：全面的数据资产目录，管理各类数据源、数据集和字段
- **数据标准管理**：统一的数据标准定义，确保数据一致性
- **数据质量规则引擎**：可配置的数据质量规则和评估机制
- **数据清洗框架**：强大的数据清洗规则引擎，支持多种清洗策略
- **数据血缘追踪**：完整的数据血缘关系图谱，透明数据流转路径
- **规则管理系统**：版本化的规则管理，支持规则的生命周期管理
- **与采集系统集成**：为数据采集提供数据清洗和质量控制能力
- **与调度系统集成**：利用平台统一调度器执行定期的数据质量检查

## 模块结构

- **platform-data-governance-core**：核心接口和领域模型
- **platform-data-governance-metadata**：元数据管理模块
- **platform-data-governance-standard**：数据标准管理模块
- **platform-data-governance-quality**：数据质量管理模块
- **platform-data-governance-cleansing**：数据清洗规则模块
- **platform-data-governance-lineage**：数据血缘追踪模块
- **platform-data-governance-api**：API和集成接口模块

## 架构设计

Platform Data Governance 遵循六边形架构原则：

```
+----------------------------------------------------------+
|                      接口适配器层                         |
|  +----------------+  +----------------+  +-------------+ |
|  |    REST API    |  |   集成适配器    |  |   管理界面   | |
|  +----------------+  +----------------+  +-------------+ |
+----------------------------------------------------------+
|                        应用服务层                         |
|  +----------------+  +----------------+  +-------------+ |
|  |  元数据服务    |  |   质量规则服务  |  |  清洗服务   | |
|  +----------------+  +----------------+  +-------------+ |
+----------------------------------------------------------+
|                         领域层                           |
|  +----------------+  +----------------+  +-------------+ |
|  |   数据资产     |  |    质量规则    |  |  清洗规则    | |
|  +----------------+  +----------------+  +-------------+ |
+----------------------------------------------------------+
|                       基础设施层                         |
|  +----------------+  +----------------+  +-------------+ |
|  |  存储适配器    |  |    规则引擎    |  |  图数据库    | |
|  +----------------+  +----------------+  +-------------+ |
+----------------------------------------------------------+
```

## 使用场景

Platform Data Governance 适用于以下场景：

1. **数据标准统一**：定义和管理企业级数据标准
2. **数据质量管控**：设置数据质量规则并监控数据质量
3. **数据清洗流程**：为数据采集和处理提供标准化清洗规则
4. **元数据管理**：建立统一的数据资产目录
5. **数据血缘分析**：追踪数据来源和流转路径
6. **规则生命周期管理**：管理数据治理规则的版本和生命周期
7. **数据采集支持**：为采集系统提供清洗规则和元数据注册

## 快速开始

### 环境要求

- Java 21+
- Maven 3.8+
- MongoDB 4.4+
- Neo4j 5.x (可选，用于数据血缘)

### 构建与安装

```bash
# 克隆仓库
git clone https://github.com/yourusername/platform-data-governance.git

# 构建项目
cd platform-data-governance
mvn clean install
```

### 配置数据清洗规则

```yaml
platform:
  data-governance:
    cleansing:
      ruleSets:
        - id: customer-data-cleansing
          name: "客户数据清洗规则集"
          version: "1.0.0"
          rules:
            - id: r001
              name: "手机号码格式化"
              field: "phone_number"
              action: "FORMAT"
              pattern: "^1[3-9]\\d{9}$"
              format: "${REGEX_REPLACE:([3-9]\\d{2})(\\d{4})(\\d{4}),1\\1\\2\\3}"
```

### 配置数据质量规则

```yaml
platform:
  data-governance:
    quality:
      ruleSets:
        - id: customer-data-quality
          name: "客户数据质量规则集"
          version: "1.0.0"
          rules:
            - id: q001
              name: "身份证号有效性"
              field: "id_number"
              type: "REGEX"
              pattern: "^\\d{17}[\\dXx]$"
              severity: "ERROR"
            - id: q002
              name: "年龄范围检查"
              field: "age"
              type: "RANGE"
              min: 0
              max: 120
              severity: "WARNING"
```

## 与其他模块集成

### 与采集系统(platform-collect)集成

数据治理框架为数据采集系统提供数据清洗和质量控制能力：

```java
// 在采集流水线中应用清洗规则
@Component
public class CleansingProcessor implements Processor {
    
    private final CleansingRuleService cleansingRuleService;
    
    @Override
    public ProcessResult process(ProcessRequest request) {
        String ruleSetId = request.getParam("ruleSetId");
        CleansingRuleSet ruleSet = cleansingRuleService.getRuleSet(ruleSetId);
        
        return cleansingRuleService.applyRules(ruleSet, request.getData());
    }
}
```

### 与调度系统(platform-scheduler)集成

数据治理框架利用平台统一调度器执行定期的数据质量检查：

```java
// 使用调度系统的注解执行数据质量检查
@SchedulableTask(name = "data-quality-check")
public class DataQualityCheckTask implements ScheduledTaskHandler {
    
    private final QualityCheckService qualityCheckService;
    
    @Override
    public TaskResult execute(TaskContext context) {
        String datasetId = context.getParam("datasetId");
        String ruleSetId = context.getParam("ruleSetId");
        
        return qualityCheckService.checkQuality(datasetId, ruleSetId);
    }
}
```

## 数据清洗流程

Platform Data Governance 提供完整的数据清洗流程：

1. **结构验证**：验证数据结构是否符合预期
2. **数据类型转换**：确保数据类型的一致性
3. **值域检查**：验证数据是否在合法范围内
4. **数据标准化**：按照数据标准进行格式化
5. **数据去重**：识别和处理重复数据
6. **数据补全**：处理缺失值和默认值
7. **业务规则校验**：应用业务逻辑进行数据校验
8. **数据脱敏**：处理敏感信息
9. **数据关联**：与主数据或参考数据关联验证
10. **数据标记**：标记数据质量和处理状态

## 可扩展性设计

### 添加新的清洗规则类型

实现`CleansingRule`接口并注册为Spring组件：

```java
@Component
@RuleType("CUSTOM_FORMAT")
public class CustomFormatRule implements CleansingRule {
    @Override
    public CleansingResult apply(Object value, RuleConfig config) {
        // 实现自定义格式化逻辑
    }
}
```

### 添加新的质量规则类型

实现`QualityRule`接口并注册为Spring组件：

```java
@Component
@RuleType("CUSTOM_VALIDATION")
public class CustomValidationRule implements QualityRule {
    @Override
    public QualityResult validate(Object value, RuleConfig config) {
        // 实现自定义验证逻辑
    }
}
```

## 贡献指南

我们欢迎社区贡献，请参阅[CONTRIBUTING.md](CONTRIBUTING.md)了解贡献流程。

## 许可证

本项目采用 [MIT 许可证](LICENSE)。
