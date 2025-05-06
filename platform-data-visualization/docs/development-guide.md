# 数据可视化模块开发指南

## 1. 项目架构

本项目采用DDD（领域驱动设计）和六边形架构（端口与适配器模式），代码结构如下：

### 1.1 领域层 (Domain)

- **领域模型**：核心业务实体和值对象，与技术设施无关
- **领域服务**：处理跨实体的业务逻辑
- **仓储接口**：定义数据访问的抽象
- **领域事件**：业务事件定义

### 1.2 应用层 (Application)

- **应用服务**：协调领域对象执行用例
- **DTO**：数据传输对象，与外部接口交互
- **装配器**：DTO与领域对象的转换

### 1.3 基础设施层 (Infrastructure)

- **仓储实现**：数据持久化的具体实现
- **持久化实体**：映射到数据库的实体类
- **外部服务适配器**：集成外部系统

### 1.4 接口层 (Interfaces)

- **REST控制器**：处理HTTP请求
- **请求/响应对象**：定义API交互的数据结构
- **WebSocket处理器**：处理实时数据通信
- **定时任务**：处理计划任务

## 2. 开发规范

### 2.1 命名规范

- **包名**：小写，如 `com.platform.visualization.domain.model`
- **类名**：大驼峰，如 `DataSourceRepository`
- **方法名**：小驼峰，如 `findByName`
- **常量**：全大写下划线分隔，如 `MAX_CONNECTION_COUNT`

### 2.2 代码风格

- 方法长度控制在80行以内
- 类长度控制在600行以内
- 使用空行分隔逻辑块
- 优先使用Java 21的新特性
- 使用Optional代替null返回

### 2.3 注释规范

- 所有公共方法必须有Javadoc注释
- 注释说明"为什么"而非"是什么"
- 复杂算法需要详细注释

## 3. 开发流程

### 3.1 功能开发

1. **理解需求**：明确功能的业务目标和技术要求
2. **领域建模**：识别核心领域概念和关系
3. **接口设计**：设计API接口和交互方式
4. **实现**：按照"由内向外"的顺序实现
   - 先实现领域模型和服务
   - 然后实现应用服务
   - 最后实现接口和基础设施
5. **单元测试**：编写测试验证功能

### 3.2 提交规范

提交信息格式如下：

```
<类型>(<范围>): <主题>

<详情>

<关闭的问题>
```

类型包括：
- feat: 新功能
- fix: 错误修复
- docs: 文档更新
- style: 代码风格改变
- refactor: 重构
- perf: 性能优化
- test: 测试相关
- build: 构建系统或外部依赖
- ci: 持续集成

## 4. 模块扩展

### 4.1 添加新的数据源类型

1. 在 `domain.model.datasource` 包中扩展 `DataSourceType` 枚举
2. 在 `infrastructure.datasource` 包中添加新的数据源连接器实现
3. 在适当的配置类中注册新的连接器

### 4.2 添加新的图表类型

1. 在 `domain.model.chart` 包中扩展 `ChartType` 枚举
2. 在 `infrastructure.chart` 包中添加新的图表渲染器实现
3. 更新前端组件以支持新图表类型

### 4.3 添加新的数据分析功能

1. 在 `domain.service` 包中添加新的领域服务
2. 在 `application.service` 包中添加相应的应用服务
3. 在 `interfaces.rest` 包中添加新的控制器端点

## 5. 调试与测试

### 5.1 本地开发环境

- 推荐使用IntelliJ IDEA作为IDE
- 使用JDK 21
- 数据库使用MySQL 5.7或更高版本
- Redis 7.x或更高版本
- Docker环境(可选)

### 5.2 单元测试

- 使用JUnit 5编写测试
- 使用Mockito模拟依赖
- 使用H2内存数据库进行测试

### 5.3 集成测试

- 使用Spring Boot Test进行集成测试
- 使用TestContainers管理测试容器
- 使用Postman或JMeter进行API测试

## 6. 常见问题

### 6.1 依赖注入

项目使用Spring的构造器注入方式，避免使用字段注入，例如：

```java
@Service
public class DataSetApplicationService {
    
    private final DataSetRepository dataSetRepository;
    private final DataSourceRepository dataSourceRepository;
    
    public DataSetApplicationService(DataSetRepository dataSetRepository,
                                  DataSourceRepository dataSourceRepository) {
        this.dataSetRepository = dataSetRepository;
        this.dataSourceRepository = dataSourceRepository;
    }
    
    // ...
}
```

### 6.2 异常处理

项目使用统一的异常处理机制：

- 领域异常：用于表示业务规则违反
- 应用异常：用于表示应用层错误
- 基础设施异常：用于表示技术设施错误

所有异常都会被全局异常处理器捕获并转换为适当的HTTP响应。

## 7. 参考资料

- [领域驱动设计参考](https://www.domainlanguage.com/ddd/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Spring Data JPA文档](https://spring.io/projects/spring-data-jpa)
- [ECharts官方文档](https://echarts.apache.org/zh/index.html)
