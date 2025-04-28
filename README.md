# Spring Boot 3.x 平台基础模块

这是一个基于Spring Boot 3.x开发的现代化微服务平台基础模块，采用超模块化架构设计，提供了一套灵活、可扩展的基础设施。

## 模块结构

平台基础设施包含以下核心模块：

1. **platform-dependencies**：统一管理项目所有依赖及其版本。
2. **platform-common**：提供通用工具类、常量、基础DTO等。
3. **platform-framework**：框架核心抽象，提供可扩展的基础能力。

## 技术栈

- Java 21
- Spring Boot 3.2.x
- Spring Cloud 2023.x
- JUnit 5

## 快速开始

### 1. 依赖引入

在你的项目中引入以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>platform-dependencies</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<dependency>
    <groupId>com.example</groupId>
    <artifactId>platform-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>com.example</groupId>
    <artifactId>platform-framework</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置文件

在 `application.yml` 中添加平台相关配置：

```yaml
platform:
  app-name: your-app-name
  app-description: Your Application Description
  app-version: 1.0.0
  debug: false
  enable-swagger: true
  enable-cors: true
  
  cors:
    allowed-origins: "*"
    allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
    allowed-headers: "*"
    allow-credentials: true
    max-age: 3600
    
  cache:
    enabled: true
    type: local
    local-maximum-size: 10000
    local-expire-after-write: 3600
    
  thread-pool:
    core-pool-size: 10
    max-pool-size: 100
    queue-capacity: 1000
    keep-alive-seconds: 60
    allow-core-thread-timeout: false
    thread-name-prefix: app-async-
```

### 3. 领域模型

基于框架提供的基类创建你的领域模型：

```java
import com.example.framework.domain.BaseAggregateRoot;

@Entity
@Table(name = "t_user")
public class User extends BaseAggregateRoot<Long> {
    private String username;
    private String email;
    private String password;
    private Boolean enabled;
    
    // getters and setters
}
```

### 4. 仓储接口

```java
import com.example.framework.domain.repository.Repository;

public interface UserRepository extends Repository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
}
```

### 5. 服务实现

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    
    @Override
    public User createUser(User user) {
        // 业务逻辑
        return userRepository.save(user);
    }
}
```

### 6. 控制器实现

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @PostMapping
    public R<User> createUser(@RequestBody @Valid UserCreateRequest request) {
        User user = new User();
        // 属性映射
        return R.success(userService.createUser(user));
    }
}
```

## 核心功能

### 统一响应

框架提供了统一的响应对象 `R<T>`，便于API接口返回统一格式的数据：

```java
// 成功响应
R.success();
R.success(data);
R.success("操作成功", data);

// 失败响应
R.error();
R.error("操作失败");
R.error(ErrorCode.BUSINESS_ERROR, "业务处理异常");
```

### 分页请求与响应

提供了标准化的分页请求和响应对象：

```java
// 分页请求
PageRequest pageRequest = PageRequest.builder()
    .pageNum(1)
    .pageSize(10)
    .sortField("createTime")
    .sortDirection("desc")
    .build();

// 分页响应
PageResponse<User> pageResponse = PageResponse.of(userList, total, pageRequest);
return R.success(pageResponse);
```

### 异常处理

框架定义了统一的异常体系和全局异常处理机制：

```java
// 业务异常
throw new BusinessException("用户名已存在");
throw BusinessException.notFound("用户不存在");

// 系统异常
throw new SystemException("系统错误");
throw SystemException.serviceUnavailable("服务暂时不可用");
```

### 工具类

框架提供了丰富的工具类：

```java
// 字符串工具
String uuid = StringUtil.uuid();
boolean containsChinese = StringUtil.containsChinese(text);

// JSON工具
String json = JsonUtil.toJson(object);
User user = JsonUtil.fromJson(json, User.class);

// 日期时间工具
LocalDateTime now = DateTimeUtil.getCurrentDateTime();
String formatted = DateTimeUtil.formatDateTime(now);
```

## 最佳实践

### 1. 领域驱动设计

推荐使用领域驱动设计(DDD)思想组织代码结构：

```
com.example.service
  ├── application       # 应用服务层
  ├── domain            # 领域层
  │   ├── model         # 领域模型
  │   ├── service       # 领域服务
  │   ├── event         # 领域事件
  │   └── repository    # 仓储接口
  ├── infrastructure    # 基础设施层
  │   ├── config        # 配置
  │   ├── repository    # 仓储实现
  │   ├── messaging     # 消息实现
  │   └── external      # 外部服务
  └── interfaces        # 接口层
      ├── api           # API接口
      ├── dto           # 数据传输对象
      └── facade        # 外观接口
```

### 2. 单元测试

使用JUnit 5和Mockito编写单元测试：

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void createUser_Success() {
        // 准备数据
        User user = new User();
        user.setUsername("test");
        
        // Mock依赖
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // 调用测试方法
        User result = userService.createUser(user);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("test", result.getUsername());
        
        // 验证交互
        verify(userRepository).save(any(User.class));
    }
}
```

## 后续开发

完成基础模块后，可以按照以下顺序开发其他平台组件：

1. **platform-gateway**：API网关服务
2. **platform-registry**：服务注册与发现中心
3. **platform-config**：分布式配置中心
4. **platform-security**：安全中心
5. **platform-monitor**：监控中心
6. **业务微服务**：各领域业务服务

## 贡献指南

1. Fork本仓库
2. 创建feature分支
3. 提交代码
4. 创建Pull Request

## 许可证

[Apache License 2.0](LICENSE)