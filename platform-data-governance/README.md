# Platform Data Governance - 数据治理框架

## 项目概述

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
  - 数据资产领域模型
  - 规则领域模型
  - 清洗规则领域模型
  
- **platform-data-governance-metadata**：元数据管理模块（已实现）
  - 元数据项管理
  - 元数据分类管理
  - 元数据API接口
  - MongoDB存储实现
  
- **platform-data-governance-standard**：数据标准管理模块
  - 数据标准定义
  - 标准版本管理
  - 标准合规检查

- **platform-data-governance-quality**：数据质量管理模块
  - 质量规则定义
  - 质量检查执行
  - 质量评分和报告

- **platform-data-governance-cleansing**：数据清洗规则模块
  - 清洗规则定义
  - 规则引擎实现
  - 清洗流程管理

- **platform-data-governance-lineage**：数据血缘追踪模块
  - 血缘关系建模
  - 血缘分析算法
  - 血缘可视化支持

- **platform-data-governance-api**：API和集成接口模块
  - 统一API管理
  - 采集系统集成
  - 调度系统集成

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

## 当前进度

已完成:
- 项目整体架构设计
- 核心领域模型定义
- 元数据管理模块完整实现，包括:
  - 元数据项领域模型
  - 元数据分类领域模型
  - 元数据服务和分类服务实现
  - MongoDB存储适配器
  - REST API接口

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

### 启动元数据服务

```bash
# 进入元数据模块目录
cd platform-data-governance-metadata

# 启动服务
mvn spring-boot:run
```

### 使用API

元数据项管理:
```
# 创建元数据项
POST /data-governance/api/v1/metadata

# 获取元数据项
GET /data-governance/api/v1/metadata/{id}

# 更新元数据项
PUT /data-governance/api/v1/metadata/{id}

# 删除元数据项
DELETE /data-governance/api/v1/metadata/{id}
```

元数据分类管理:
```
# 创建顶级分类
POST /data-governance/api/v1/metadata/categories

# 创建子分类
POST /data-governance/api/v1/metadata/categories/{parentId}/children

# 获取分类树
GET /data-governance/api/v1/metadata/categories/{rootId}/tree
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

## 贡献指南

欢迎贡献代码和提出问题。请遵循以下原则：

1. 遵循领域驱动设计和六边形架构
2. 编写单元测试，确保代码覆盖率
3. 遵循Java编码规范，使用Javadoc注释
4. 提交前进行代码格式化和静态检查

## 许可证

本项目采用 [MIT 许可证](LICENSE)。
