# V6 - 开发规范

为了确保平台代码的质量、一致性、可维护性和安全性，V6版本制定了全面的开发规范。

## 1. 代码规范

- **基础规范**: 强制遵循《阿里巴巴Java开发手册》(最新版) 作为基础编码规范。
- **Google Java Style Guide**: 参考Google的Java风格指南，特别是在格式化、命名约定等方面。
- **增强规则集**: （可选）引入如华为Java增强编程规则集的部分规则，侧重代码健壮性和性能。
- **静态代码分析**: 
    - **Checkstyle**: 用于强制执行代码风格和格式化规则。
    - **SpotBugs / PMD**: 用于检测潜在的bug、不良实践和性能问题。
    - **SonarQube**: （推荐）集成以上工具，提供统一的代码质量管理平台和持续检查。
- **代码格式化**: 必须使用统一的IDE代码格式化模板（如基于Google Style或阿里规范定制）。
- **注释要求**: 
    - 公共API（类、方法）必须有清晰的JavaDoc注释。
    - 复杂或非显而易见的逻辑必须有行内或块注释说明。
    - 目标注释覆盖率 > 30% （根据项目实际情况调整）。
- **命名规范**: 
    - 类名：UpperCamelCase。
    - 方法名、变量名：lowerCamelCase。
    - 常量名：UPPER_SNAKE_CASE。
    - 包名：全小写，反域名方式。
    - 遵循"名副其实"原则，避免使用无意义或过于简写的名称。
- **日志规范**: 
    - 使用SLF4j作为日志门面，Logback或Log4j2作为实现。
    - 定义清晰的日志级别（ERROR, WARN, INFO, DEBUG, TRACE）。
    - 关键操作、入口出口、异常情况必须记录日志。
    - 日志内容应包含关键上下文信息（如请求ID、用户ID、订单号等）。
    - 避免在日志中打印敏感信息。

## 2. API 设计规范

- **协议**: 主要使用RESTful风格的HTTP/JSON API，特定场景可使用gRPC。
- **资源路径(URL)**: 
    - 使用名词复数表示资源集合（如 `/users`, `/orders`）。
    - 使用ID表示特定资源（如 `/users/{userId}`）。
    - 使用嵌套路径表示资源关系（如 `/users/{userId}/orders`）。
    - 动词应通过HTTP方法表达（GET, POST, PUT, DELETE, PATCH）。
- **HTTP方法**: 
    - `GET`: 获取资源。
    - `POST`: 创建资源或执行非幂等操作。
    - `PUT`: 完整替换资源（幂等）。
    - `DELETE`: 删除资源。
    - `PATCH`: 部分更新资源（幂等）。
- **请求体/响应体**: 
    - 使用JSON格式。
    - 请求体用于POST, PUT, PATCH。
    - 遵循统一的响应体结构，包含状态码、消息、数据等字段：
      ```json
      {
        "code": 200, // 业务状态码，非HTTP状态码
        "message": "Success",
        "data": { ... } // 或 [ ... ]
        // 可选 traceId 等
      }
      ```
- **状态码**: 
    - **HTTP状态码**: 严格遵循HTTP标准（2xx成功, 4xx客户端错误, 5xx服务端错误）。
    - **业务状态码**: 在响应体中定义更详细的业务处理结果。
- **参数传递**: 
    - URL路径参数: 用于标识资源ID。
    - URL查询参数: 用于过滤、排序、分页（如 `?status=active&sort=createdAt&page=1&size=20`）。
    - 请求体: 用于传递复杂数据。
- **版本控制**: 
    - 推荐在URL路径中包含版本号（如 `/v1/users`, `/v2/users`）。
    - 或者使用Header（如 `Accept: application/vnd.myapi.v1+json`）。
- **文档化**: 
    - 使用OpenAPI 3.0规范（Swagger）描述API。
    - 集成Swagger UI或类似工具自动生成API文档。
    - API变更时必须同步更新文档。
- **安全性**: 
    - 所有API必须通过网关进行认证和授权。
    - 防止敏感信息在URL参数中传递。
    - 对输入进行严格校验。

## 3. 测试规范

- **测试金字塔**: 遵循测试金字塔原则，重点投入单元测试，逐步减少集成测试和端到端测试。
- **单元测试 (Unit Test)**: 
    - 使用JUnit 5 / TestNG作为测试框架。
    - 使用Mockito / MockK进行Mock和Stub。
    - 核心业务逻辑、复杂算法、工具类必须有单元测试。
    - 目标代码覆盖率 > 80% （对核心模块要求更高）。
    - 测试应遵循AIR原则（Automatic, Independent, Repeatable）。
- **集成测试 (Integration Test)**: 
    - 测试服务内部模块之间或服务与外部依赖（数据库、消息队列）的交互。
    - 使用Testcontainers或内存数据库/消息队列进行环境模拟。
    - 覆盖关键的业务流程和数据交互。
- **契约测试 (Contract Test)**: 
    - （推荐）使用Spring Cloud Contract等工具，验证服务间API调用的契约一致性。
    - 用于保证超模块化微服务间的接口兼容性。
- **组件测试 (Component Test)**: 
    - 测试单个微服务的完整功能，将其作为黑盒，通过其API进行交互。
- **端到端测试 (End-to-End Test)**: 
    - 模拟真实用户场景，测试跨多个服务的完整业务流程。
    - 数量应严格控制，重点覆盖核心业务链路。
    - 使用Selenium, Playwright, Cypress等UI自动化工具或API测试工具。
- **性能测试 (Performance Test)**: 
    - 使用JMeter, k6, Gatling等工具。
    - 定义明确的性能基准（TPS, 响应时间, 资源利用率）。
    - 定期进行性能测试，特别是在发布前和重大变更后。
- **自动化**: 
    - 所有测试应尽可能自动化，并集成到CI/CD流水线中。
    - 测试失败应阻塞流水线。 