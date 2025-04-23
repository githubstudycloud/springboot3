# 多数据源支持功能指南

## 概述

调度系统在企业应用场景中常常需要连接多个不同的数据源，以便在不同的系统之间协调和执行任务。本系统实现了多数据源支持功能，允许动态管理多个数据源，实现在不同数据库之间灵活切换，提高了系统的适应性和扩展能力。

## 功能特性

1. **动态数据源管理**
   - 支持在运行时动态注册、更新和删除数据源
   - 提供图形化界面和RESTful API接口进行管理
   - 持久化数据源配置，应用重启后自动恢复

2. **数据源健康监控**
   - 定期检查所有数据源的连接状态
   - 自动检测异常数据源并记录日志
   - 提供数据源连接测试功能

3. **动态数据源切换**
   - 基于AOP实现声明式数据源切换
   - 支持方法级和类级数据源指定
   - 支持编程式数据源切换

4. **多种数据源类型**
   - 支持MySQL、Oracle、SQL Server等关系型数据库
   - 支持不同版本的数据库系统
   - 可扩展支持更多数据源类型

## 架构设计

多数据源支持功能的核心架构由以下几个部分组成：

1. **动态数据源（DynamicDataSource）**
   - 继承Spring的AbstractRoutingDataSource实现数据源路由
   - 使用ThreadLocal存储当前线程使用的数据源标识
   - 提供静态方法便捷切换数据源

2. **数据源注册表（DataSourceRegistry）**
   - 管理所有数据源的定义和实例
   - 创建和关闭数据源连接池
   - 检查数据源连接状态

3. **数据源服务（DataSourceService）**
   - 提供业务层面的数据源管理功能
   - 负责数据源配置的持久化和加载
   - 连接数据源注册表和API控制器

4. **数据源切换注解（DataSourceSwitch）**
   - 声明式数据源切换的核心注解
   - 支持指定数据源名称
   - 结合AOP实现自动切换

5. **SQL执行工具（SqlExecutor）**
   - 在指定数据源中执行SQL操作
   - 自动处理数据源切换和恢复
   - 提供丰富的查询和更新方法

## 配置说明

### 应用配置

在application.yml中添加多数据源相关配置：

```yaml
scheduler:
  datasource:
    config-file: ./config/datasources.json  # 数据源配置文件路径
    auto-load: true  # 是否自动加载数据源配置
    health-check-rate: 60000  # 数据源健康检查频率，单位毫秒
```

### 数据源定义

数据源定义包含以下信息：

```json
{
  "id": "mysql1",
  "name": "MySQL主数据库",
  "type": "MySQL",
  "driverClassName": "com.mysql.cj.jdbc.Driver",
  "url": "jdbc:mysql://localhost:3306/mydb",
  "username": "root",
  "password": "password",
  "defaultDataSource": true,
  "poolConfig": {
    "minimumIdle": 5,
    "maximumPoolSize": 20,
    "idleTimeout": 30000,
    "maxLifetime": 1800000,
    "connectionTimeout": 30000
  },
  "properties": {
    "useSSL": "false",
    "serverTimezone": "Asia/Shanghai"
  },
  "description": "主应用数据库"
}
```

## 使用指南

### 注册数据源

通过API注册新的数据源：

```java
@Autowired
private DataSourceService dataSourceService;

// 创建数据源定义
DataSourceDefinition definition = new DataSourceDefinition();
definition.setId("oracle1");
definition.setName("Oracle财务库");
definition.setType("Oracle");
definition.setDriverClassName("oracle.jdbc.OracleDriver");
definition.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
definition.setUsername("finance");
definition.setPassword("password");

// 设置连接池配置
Map<String, Object> poolConfig = new HashMap<>();
poolConfig.put("minimumIdle", 3);
poolConfig.put("maximumPoolSize", 10);
definition.setPoolConfig(poolConfig);

// 注册数据源
boolean result = dataSourceService.registerDataSource(definition);
```

### 使用注解切换数据源

在服务方法上使用@DataSourceSwitch注解：

```java
@Service
public class UserService {
    
    // 使用指定数据源
    @DataSourceSwitch("oracle1")
    public List<User> findUsersByDepartment(String department) {
        // 方法中的所有数据库操作将使用oracle1数据源
        // ...
    }
    
    // 使用默认数据源
    public List<User> findAllUsers() {
        // 方法中的所有数据库操作将使用默认数据源
        // ...
    }
}
```

在类级别使用@DataSourceSwitch注解：

```java
@Service
@DataSourceSwitch("mysql1")
public class ProductService {
    
    // 类中的所有方法都将使用mysql1数据源
    public List<Product> findAllProducts() {
        // ...
    }
    
    // 除非方法上有不同的数据源注解
    @DataSourceSwitch("oracle1")
    public void syncProductsToFinance() {
        // 此方法将使用oracle1数据源
        // ...
    }
}
```

### 编程式切换数据源

直接使用DynamicDataSource类进行数据源切换：

```java
// 切换到指定数据源
DynamicDataSource.setDataSource("mysql1");

try {
    // 执行数据库操作
    // ...
} finally {
    // 恢复默认数据源
    DynamicDataSource.clearDataSource();
}
```

### 使用SqlExecutor执行SQL

使用SqlExecutor在不同数据源中执行SQL：

```java
@Autowired
private SqlExecutor sqlExecutor;

// 在指定数据源中查询
List<Map<String, Object>> results = sqlExecutor.queryForList(
    "mysql1", 
    "SELECT * FROM users WHERE department = ?", 
    "IT"
);

// 在指定数据源中更新
int updated = sqlExecutor.update(
    "oracle1",
    "UPDATE products SET price = ? WHERE id = ?",
    99.99, 1001
);
```

### 管理数据源

通过API管理数据源：

```java
// 获取所有数据源
List<DataSourceDefinition> allDataSources = dataSourceService.getAllDataSources();

// 获取指定数据源
DataSourceDefinition ds = dataSourceService.getDataSource("mysql1");

// 测试数据源连接
boolean connected = dataSourceService.checkDataSource("oracle1");

// 设置默认数据源
dataSourceService.setDefaultDataSource("mysql1");

// 保存数据源配置
dataSourceService.saveDataSourceConfig();
```

## 注意事项

1. **数据源切换作用域**
   - 数据源切换基于ThreadLocal实现，仅在当前线程内有效
   - 在创建新线程时，需要手动传递数据源信息
   - 在异步方法中，需要特别注意数据源传递问题

2. **事务管理**
   - 数据源切换会影响事务管理
   - 同一事务内不支持跨数据源操作
   - 需要使用分布式事务处理跨数据源事务

3. **性能考虑**
   - 避免频繁切换数据源
   - 合理设置连接池参数
   - 注意监控数据源连接泄漏

4. **安全性**
   - 数据源配置包含敏感信息，确保配置文件安全
   - 建议对密码进行加密
   - 限制数据源管理API的访问权限

## 故障排除

1. **无法连接数据源**
   - 检查网络连接
   - 验证用户名密码
   - 确认数据库服务是否正常运行
   - 检查数据库权限

2. **数据源切换失败**
   - 确认数据源已正确注册
   - 检查数据源ID是否正确
   - 验证线程上下文是否被意外清除

3. **连接池溢出**
   - 增加最大连接数
   - 优化SQL减少连接占用时间
   - 检查是否存在连接泄漏问题

## 扩展方向

1. **支持更多数据源类型**
   - 增加NoSQL数据库支持
   - 支持消息队列作为任务来源
   - 支持文件系统作为数据来源

2. **增强监控功能**
   - 添加数据源性能监控
   - 实时显示连接池状态
   - 提供数据源使用统计

3. **增强安全性**
   - 实现数据源访问权限控制
   - 增加敏感数据脱敏功能
   - 支持数据源配置加密
