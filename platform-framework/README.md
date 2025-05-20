# platform-framework

## 项目概述

`platform-framework` 是企业级数据平台的框架层核心模块，提供了基于领域驱动设计(DDD)和六边形架构的实现基础。该模块封装了框架相关的通用组件，使业务开发者能够专注于领域逻辑而非技术细节，同时确保框架与业务的有效隔离。

## 模块结构

`platform-framework` 是一个多模块项目，由以下子模块组成：

1. **platform-framework-core**: 核心框架组件
2. **platform-framework-starter**: 自动配置starter
3. **platform-framework-domain**: 领域框架支持
4. **platform-framework-test**: 测试支持框架

```
platform-framework/
├── platform-framework-core/               # 核心框架组件
│   ├── src/main/java/com/platform/framework/core/
│   │   ├── annotation/                    # 框架注解
│   │   ├── context/                       # 应用上下文
│   │   ├── domain/                        # 领域基础框架
│   │   │   ├── model/                     # 领域模型基类
│   │   │   ├── repository/                # 仓储基类
│   │   │   ├── service/                   # 领域服务基类
│   │   │   └── event/                     # 领域事件框架
│   │   ├── application/                   # 应用层框架
│   │   │   ├── command/                   # 命令处理框架
│   │   │   ├── query/                     # 查询处理框架
│   │   │   └── service/                   # 应用服务基类
│   │   ├── infrastructure/                # 基础设施层框架
│   │   │   ├── repository/                # 仓储实现基类
│   │   │   ├── persistence/               # 持久化框架
│   │   │   └── messaging/                 # 消息框架
│   │   └── util/                          # 框架工具类
│   └── pom.xml
├── platform-framework-starter/            # 自动配置starter
│   ├── src/main/java/com/platform/framework/starter/
│   │   ├── config/                        # 自动配置类
│   │   ├── properties/                    # 配置属性
│   │   └── initializer/                   # 初始化器
│   └── pom.xml
├── platform-framework-domain/             # 领域框架支持
│   ├── src/main/java/com/platform/framework/domain/
│   │   ├── aggregate/                     # 聚合模型支持
│   │   ├── entity/                        # 实体支持
│   │   ├── valueobject/                   # 值对象支持
│   │   ├── specification/                 # 规格模式支持
│   │   └── factory/                       # 工厂模式支持
│   └── pom.xml
├── platform-framework-test/               # 测试支持框架
│   ├── src/main/java/com/platform/framework/test/
│   │   ├── annotation/                    # 测试注解
│   │   ├── context/                       # 测试上下文
│   │   ├── mock/                          # 模拟工具
│   │   └── assertion/                     # 断言工具
│   └── pom.xml
├── pom.xml                                # 父POM
└── README.md                              # 模块说明
```

## 核心功能

### 1. platform-framework-core

核心框架组件提供基础的框架支持，实现DDD和六边形架构的基础设施：

- **领域模型基类**：提供实体、值对象、聚合根等基础抽象类
- **领域事件框架**：实现领域事件的发布和订阅
- **应用层框架**：命令查询职责分离(CQRS)支持
- **仓储抽象**：统一的仓储接口和基类

#### 领域模型基类示例

```java
/**
 * 实体基类
 * @param <ID> ID类型
 */
public abstract class Entity<ID extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 获取实体ID
     */
    public abstract ID getId();
    
    /**
     * 判断实体是否相等，基于ID比较
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Entity<?> entity = (Entity<?>) o;
        
        ID id = getId();
        if (id == null) {
            return false;
        }
        
        return id.equals(entity.getId());
    }
    
    /**
     * 生成实体哈希码，基于ID
     */
    @Override
    public int hashCode() {
        ID id = getId();
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
    
    /**
     * 实体的字符串表示
     */
    @Override
    public String toString() {
        return String.format("%s[id=%s]", getClass().getSimpleName(), getId());
    }
}

/**
 * 聚合根基类
 * @param <ID> ID类型
 */
public abstract class AggregateRoot<ID extends Serializable> extends Entity<ID> {
    private transient final List<DomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * 注册领域事件
     * @param event 领域事件
     */
    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }
    
    /**
     * 清除领域事件
     * @return 已清除的领域事件列表
     */
    public List<DomainEvent> clearEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }
    
    /**
     * 获取未清除的领域事件
     * @return 领域事件列表
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}

/**
 * 值对象基类
 */
public abstract class ValueObject implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 值对象相等性比较必须考虑所有属性
     * 子类必须重写该方法实现正确的相等性语义
     */
    @Override
    public abstract boolean equals(Object o);
    
    /**
     * 哈希码必须与equals保持一致
     * 子类必须重写该方法实现正确的哈希语义
     */
    @Override
    public abstract int hashCode();
    
    /**
     * 值对象的字符串表示
     */
    @Override
    public String toString() {
        return String.format("%s{}", getClass().getSimpleName());
    }
}
```

#### 领域事件框架示例

```java
/**
 * 领域事件接口
 */
public interface DomainEvent extends Serializable {
    /**
     * 获取事件发生时间
     * @return 事件时间
     */
    Instant getOccurredAt();
    
    /**
     * 获取事件类型
     * @return 事件类型
     */
    default String getEventType() {
        return this.getClass().getSimpleName();
    }
}

/**
 * 抽象领域事件基类
 */
public abstract class AbstractDomainEvent implements DomainEvent {
    private static final long serialVersionUID = 1L;
    
    private final Instant occurredAt;
    private final String eventId;
    
    protected AbstractDomainEvent() {
        this.occurredAt = Instant.now();
        this.eventId = UUID.randomUUID().toString();
    }
    
    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
    
    public String getEventId() {
        return eventId;
    }
}

/**
 * 领域事件发布器接口
 */
public interface DomainEventPublisher {
    /**
     * 发布单个领域事件
     * @param event 领域事件
     */
    void publish(DomainEvent event);
    
    /**
     * 发布多个领域事件
     * @param events 领域事件列表
     */
    default void publishAll(Collection<DomainEvent> events) {
        if (events != null && !events.isEmpty()) {
            for (DomainEvent event : events) {
                publish(event);
            }
        }
    }
}

/**
 * 基于Spring的领域事件发布器实现
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public SpringDomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void publish(DomainEvent event) {
        if (event != null) {
            eventPublisher.publishEvent(event);
        }
    }
}
```

#### 应用层框架示例

```java
/**
 * 命令接口
 */
public interface Command extends Serializable {
}

/**
 * 命令处理器接口
 * @param <C> 命令类型
 * @param <R> 结果类型
 */
public interface CommandHandler<C extends Command, R> {
    /**
     * 处理命令
     * @param command 命令
     * @return 处理结果
     */
    R handle(C command);
}

/**
 * 命令总线接口
 */
public interface CommandBus {
    /**
     * 发送命令
     * @param command 命令
     * @param <R> 结果类型
     * @return 处理结果
     */
    <C extends Command, R> R send(C command);
}

/**
 * 基于Spring的命令总线实现
 */
@Component
public class SpringCommandBus implements CommandBus {
    private final Map<Class<? extends Command>, CommandHandler<?, ?>> handlers;
    
    @Autowired
    public SpringCommandBus(List<CommandHandler<?, ?>> handlerList) {
        handlers = new HashMap<>();
        
        for (CommandHandler<?, ?> handler : handlerList) {
            Class<?> commandType = ResolvableType.forClass(CommandHandler.class, handler.getClass())
                .resolveGeneric(0);
            
            if (commandType != null) {
                @SuppressWarnings("unchecked")
                Class<? extends Command> commandClass = (Class<? extends Command>) commandType;
                handlers.put(commandClass, handler);
            }
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <C extends Command, R> R send(C command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        
        CommandHandler<C, R> handler = (CommandHandler<C, R>) handlers.get(command.getClass());
        
        if (handler == null) {
            throw new IllegalStateException("No handler registered for command: " + command.getClass().getName());
        }
        
        return handler.handle(command);
    }
}
```

#### 仓储抽象示例

```java
/**
 * 仓储接口
 * @param <T> 聚合根类型
 * @param <ID> ID类型
 */
public interface Repository<T extends AggregateRoot<ID>, ID extends Serializable> {
    /**
     * 保存聚合根
     * @param aggregate 聚合根实例
     * @return 保存后的聚合根
     */
    T save(T aggregate);
    
    /**
     * 通过ID查找聚合根
     * @param id 聚合根ID
     * @return 找到的聚合根，如果不存在则返回空
     */
    Optional<T> findById(ID id);
    
    /**
     * 检查具有指定ID的聚合根是否存在
     * @param id 聚合根ID
     * @return 如果存在则返回true，否则返回false
     */
    boolean existsById(ID id);
    
    /**
     * 删除聚合根
     * @param aggregate 要删除的聚合根
     */
    void delete(T aggregate);
    
    /**
     * 通过ID删除聚合根
     * @param id 聚合根ID
     */
    void deleteById(ID id);
}
```

### 2. platform-framework-starter

自动配置starter模块为框架提供自动配置功能，使开发者能够快速集成框架：

- **自动配置**：根据classpath中的依赖自动配置框架组件
- **属性配置**：灵活的属性配置支持
- **条件装配**：根据不同条件选择不同配置

#### 自动配置类示例

```java
/**
 * 领域框架自动配置
 */
@Configuration
@ConditionalOnClass(DomainEventPublisher.class)
@EnableConfigurationProperties(DomainFrameworkProperties.class)
public class DomainFrameworkAutoConfiguration {
    
    private final DomainFrameworkProperties properties;
    
    public DomainFrameworkAutoConfiguration(DomainFrameworkProperties properties) {
        this.properties = properties;
    }
    
    /**
     * 配置领域事件发布器
     */
    @Bean
    @ConditionalOnMissingBean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher eventPublisher) {
        return new SpringDomainEventPublisher(eventPublisher);
    }
    
    /**
     * 配置命令总线
     */
    @Bean
    @ConditionalOnMissingBean
    public CommandBus commandBus(List<CommandHandler<?, ?>> handlers) {
        return new SpringCommandBus(handlers);
    }
    
    /**
     * 配置查询总线
     */
    @Bean
    @ConditionalOnMissingBean
    public QueryBus queryBus(List<QueryHandler<?, ?>> handlers) {
        return new SpringQueryBus(handlers);
    }
    
    /**
     * 配置领域事件发布切面
     */
    @Bean
    @ConditionalOnProperty(prefix = "platform.domain", name = "event-publishing", havingValue = "true", matchIfMissing = true)
    public DomainEventPublishingAspect domainEventPublishingAspect(DomainEventPublisher eventPublisher) {
        return new DomainEventPublishingAspect(eventPublisher);
    }
    
    /**
     * 配置事务管理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(PlatformTransactionManager.class)
    public TransactionalCommandBus transactionalCommandBus(CommandBus commandBus, PlatformTransactionManager transactionManager) {
        return new TransactionalCommandBus(commandBus, transactionManager);
    }
}
```

#### 配置属性类示例

```java
/**
 * 领域框架配置属性
 */
@ConfigurationProperties(prefix = "platform.domain")
public class DomainFrameworkProperties {
    
    /**
     * 是否启用领域事件发布
     */
    private boolean eventPublishing = true;
    
    /**
     * 是否启用事务性命令处理
     */
    private boolean transactionalCommands = true;
    
    /**
     * 是否启用审计功能
     */
    private boolean auditing = false;
    
    /**
     * 领域事件配置
     */
    private EventProperties event = new EventProperties();
    
    /**
     * 领域事件配置属性
     */
    public static class EventProperties {
        /**
         * 事件发布模式
         */
        private PublishingMode publishingMode = PublishingMode.SYNCHRONOUS;
        
        /**
         * 事件处理超时时间（毫秒）
         */
        private long timeout = 5000;
        
        // getter/setter 方法...
    }
    
    /**
     * 事件发布模式枚举
     */
    public enum PublishingMode {
        /**
         * 同步发布
         */
        SYNCHRONOUS,
        
        /**
         * 异步发布
         */
        ASYNCHRONOUS,
        
        /**
         * 事务后发布
         */
        TRANSACTIONAL
    }
    
    // getter/setter 方法...
}
```

### 3. platform-framework-domain

领域框架支持模块提供DDD战术设计元素的实现：

- **聚合模型**：聚合相关的模型和接口
- **实体支持**：各种实体类型和生命周期管理
- **值对象支持**：值对象创建和验证
- **规格模式**：业务规则封装
- **工厂模式**：对象创建封装

#### 规格模式实现示例

```java
/**
 * 规格接口
 * @param <T> 被检查的对象类型
 */
@FunctionalInterface
public interface Specification<T> {
    /**
     * 检查对象是否满足规格
     * @param target 被检查的对象
     * @return 如果满足规格返回true，否则返回false
     */
    boolean isSatisfiedBy(T target);
    
    /**
     * 与另一个规格组合形成"与"关系
     * @param other 另一个规格
     * @return 组合后的规格
     */
    default Specification<T> and(Specification<T> other) {
        return target -> isSatisfiedBy(target) && other.isSatisfiedBy(target);
    }
    
    /**
     * 与另一个规格组合形成"或"关系
     * @param other 另一个规格
     * @return 组合后的规格
     */
    default Specification<T> or(Specification<T> other) {
        return target -> isSatisfiedBy(target) || other.isSatisfiedBy(target);
    }
    
    /**
     * 对当前规格取反
     * @return 取反后的规格
     */
    default Specification<T> not() {
        return target -> !isSatisfiedBy(target);
    }
}

/**
 * 组合规格基类
 * @param <T> 被检查的对象类型
 */
public abstract class CompositeSpecification<T> implements Specification<T> {
    /**
     * 创建与规格
     * @param other 另一个规格
     * @return 与规格
     */
    @Override
    public Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(this, other);
    }
    
    /**
     * 创建或规格
     * @param other 另一个规格
     * @return 或规格
     */
    @Override
    public Specification<T> or(Specification<T> other) {
        return new OrSpecification<>(this, other);
    }
    
    /**
     * 创建非规格
     * @return 非规格
     */
    @Override
    public Specification<T> not() {
        return new NotSpecification<>(this);
    }
    
    /**
     * 与规格实现
     * @param <T> 被检查的对象类型
     */
    private static class AndSpecification<T> extends CompositeSpecification<T> {
        private final Specification<T> left;
        private final Specification<T> right;
        
        AndSpecification(Specification<T> left, Specification<T> right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public boolean isSatisfiedBy(T target) {
            return left.isSatisfiedBy(target) && right.isSatisfiedBy(target);
        }
    }
    
    /**
     * 或规格实现
     * @param <T> 被检查的对象类型
     */
    private static class OrSpecification<T> extends CompositeSpecification<T> {
        private final Specification<T> left;
        private final Specification<T> right;
        
        OrSpecification(Specification<T> left, Specification<T> right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public boolean isSatisfiedBy(T target) {
            return left.isSatisfiedBy(target) || right.isSatisfiedBy(target);
        }
    }
    
    /**
     * 非规格实现
     * @param <T> 被检查的对象类型
     */
    private static class NotSpecification<T> extends CompositeSpecification<T> {
        private final Specification<T> spec;
        
        NotSpecification(Specification<T> spec) {
            this.spec = spec;
        }
        
        @Override
        public boolean isSatisfiedBy(T target) {
            return !spec.isSatisfiedBy(target);
        }
    }
}
```

#### 工厂模式示例

```java
/**
 * 抽象工厂接口
 * @param <T> 创建的对象类型
 */
public interface Factory<T> {
    /**
     * 创建对象
     * @return 创建的对象
     */
    T create();
}

/**
 * 聚合工厂接口
 * @param <T> 聚合根类型
 * @param <ID> ID类型
 */
public interface AggregateFactory<T extends AggregateRoot<ID>, ID extends Serializable> {
    /**
     * 创建新的聚合根
     * @return 创建的聚合根
     */
    T create();
    
    /**
     * 从存储数据重建聚合根
     * @param data 存储数据
     * @return 重建的聚合根
     */
    T reconstitute(Map<String, Object> data);
}

/**
 * 领域工厂基类
 * @param <T> 领域对象类型
 */
public abstract class AbstractFactory<T> implements Factory<T> {
    private final ApplicationContext applicationContext;
    
    protected AbstractFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    /**
     * 获取Spring应用上下文
     * @return Spring应用上下文
     */
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    /**
     * 获取Bean
     * @param clazz Bean类型
     * @param <B> Bean类型
     * @return Bean实例
     */
    protected <B> B getBean(Class<B> clazz) {
        return applicationContext.getBean(clazz);
    }
    
    /**
     * 获取Bean
     * @param name Bean名称
     * @param clazz Bean类型
     * @param <B> Bean类型
     * @return Bean实例
     */
    protected <B> B getBean(String name, Class<B> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}
```

### 4. platform-framework-test

测试支持框架提供测试相关工具和功能：

- **单元测试工具**：领域对象单元测试支持
- **集成测试工具**：应用服务和仓储测试支持
- **测试辅助工具**：测试数据生成器、断言工具等
- **测试模拟工具**：模拟对象和存根创建

#### 测试注解示例

```java
/**
 * 领域测试注解
 * 用于标记领域对象的单元测试
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DomainTestExtension.class)
public @interface DomainTest {
}

/**
 * 应用服务测试注解
 * 用于标记应用服务的集成测试
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public @interface ApplicationServiceTest {
}

/**
 * 仓储测试注解
 * 用于标记仓储的集成测试
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public @interface RepositoryTest {
}
```

#### 测试辅助工具示例

```java
/**
 * 领域测试辅助类
 * 提供领域对象测试的实用方法
 */
public class DomainTestUtils {
    
    private DomainTestUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * 获取聚合根中的领域事件
     * @param aggregate 聚合根
     * @param <T> 事件类型
     * @return 指定类型的领域事件列表
     */
    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> List<T> getEventsOfType(AggregateRoot<?> aggregate, Class<T> eventType) {
        return aggregate.getDomainEvents().stream()
                .filter(event -> eventType.isAssignableFrom(event.getClass()))
                .map(event -> (T) event)
                .collect(Collectors.toList());
    }
    
    /**
     * 检查聚合根是否包含指定类型的事件
     * @param aggregate 聚合根
     * @param eventType 事件类型
     * @return 如果包含返回true，否则返回false
     */
    public static boolean hasEventOfType(AggregateRoot<?> aggregate, Class<? extends DomainEvent> eventType) {
        return aggregate.getDomainEvents().stream()
                .anyMatch(event -> eventType.isAssignableFrom(event.getClass()));
    }
    
    /**
     * 获取聚合根中的第一个指定类型的事件
     * @param aggregate 聚合根
     * @param eventType 事件类型
     * @param <T> 事件类型
     * @return 第一个指定类型的事件，如果不存在则返回空
     */
    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> Optional<T> getFirstEventOfType(AggregateRoot<?> aggregate, Class<T> eventType) {
        return aggregate.getDomainEvents().stream()
                .filter(event -> eventType.isAssignableFrom(event.getClass()))
                .map(event -> (T) event)
                .findFirst();
    }
    
    /**
     * 通过反射设置私有字段值
     * @param target 目标对象
     * @param fieldName 字段名
     * @param value 字段值
     */
    public static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field: " + fieldName, e);
        }
    }
    
    /**
     * 获取私有字段值
     * @param target 目标对象
     * @param fieldName 字段名
     * @param <T> 字段类型
     * @return 字段值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPrivateField(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get private field: " + fieldName, e);
        }
    }
    
    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> searchType = clazz;
        while (searchType != null) {
            try {
                return searchType.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                searchType = searchType.getSuperclass();
            }
        }
        throw new IllegalArgumentException("Field not found: " + fieldName + " in class " + clazz.getName());
    }
}
```

## 依赖管理

### 主POM依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.platform</groupId>
        <artifactId>platform-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../platform-parent/pom.xml</relativePath>
    </parent>
    
    <artifactId>platform-framework</artifactId>
    <packaging>pom</packaging>
    <n>Platform Framework</n>
    <description>Framework for Enterprise Data Platform based on DDD and Hexagonal Architecture</description>
    
    <modules>
        <module>platform-framework-core</module>
        <module>platform-framework-starter</module>
        <module>platform-framework-domain</module>
        <module>platform-framework-test</module>
    </modules>
    
    <dependencies>
        <!-- 所有模块共享的依赖放在这里 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        
        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### platform-framework-core 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.platform</groupId>
        <artifactId>platform-framework</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>platform-framework-core</artifactId>
    <packaging>jar</packaging>
    <n>Platform Framework Core</n>
    <description>Core framework for Enterprise Data Platform</description>
    
    <dependencies>
        <!-- 平台依赖 -->
        <dependency>
            <groupId>com.platform</groupId>
            <artifactId>platform-common-core</artifactId>
        </dependency>
        
        <!-- Spring依赖 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
        </dependency>
        
        <!-- AspectJ -->
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
        </dependency>
        
        <!-- Jakarta -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
        <dependency>
            <groupId>jakarta.annotation</groupId>
            <artifactId>jakarta.annotation-api</artifactId>
        </dependency>
        
        <!-- Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 设计原则

`platform-framework`模块的设计遵循以下核心原则：

### 1. 框架与业务隔离

- **框架无关的领域模型**：领域模型不依赖框架类
- **纯净的领域层**：领域层不包含框架注解和API调用
- **依赖反转**：通过接口和依赖注入实现技术细节与领域逻辑的分离
- **防腐层模式**：使用适配器隔离外部系统

### 2. 领域驱动设计实践

- **明确的领域概念**：提供实体、值对象、聚合根等DDD概念的框架支持
- **通用语言**：统一的术语和接口命名，与业务语言一致
- **限界上下文**：支持模块化组织和边界划分
- **领域事件**：丰富的领域事件框架

### 3. 六边形架构支持

- **端口与适配器分离**：明确区分内部领域和外部世界
- **可替换性**：灵活更换技术实现而不影响核心逻辑
- **可测试性**：便于单元测试的设计

### 4. 扩展性设计

- **组合优于继承**：优先使用组合模式而非继承实现功能扩展
- **钩子机制**：提供扩展点允许自定义行为
- **配置驱动**：通过配置而非代码更改来调整框架行为

## 使用指南

### 1. 领域建模

```java
// 定义值对象
@Immutable
public class Email extends ValueObject {
    private final String address;
    
    private Email(String address) {
        this.address = address;
    }
    
    public static Email of(String address) {
        validateAddress(address);
        return new Email(address);
    }
    
    private static void validateAddress(String address) {
        if (address == null || !address.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Invalid email address: " + address);
        }
    }
    
    public String getAddress() {
        return address;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}

// 定义实体
public class User extends Entity<UserId> {
    private final UserId id;
    private Username username;
    private Email email;
    private boolean active;
    
    public User(UserId id, Username username, Email email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.active = false;
    }
    
    @Override
    public UserId getId() {
        return id;
    }
    
    public Username getUsername() {
        return username;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void activate() {
        if (!active) {
            this.active = true;
        }
    }
    
    public void changeEmail(Email newEmail) {
        this.email = newEmail;
    }
}

// 定义聚合根
public class Order extends AggregateRoot<OrderId> {
    private final OrderId id;
    private CustomerId customerId;
    private OrderStatus status;
    private Money totalAmount;
    private Set<OrderItem> items;
    
    private Order(OrderId id, CustomerId customerId) {
        this.id = id;
        this.customerId = customerId;
        this.status = OrderStatus.CREATED;
        this.totalAmount = Money.zero(Currency.getInstance("USD"));
        this.items = new HashSet<>();
        
        registerEvent(new OrderCreatedEvent(this.id, this.customerId));
    }
    
    public static Order create(OrderId id, CustomerId customerId) {
        return new Order(id, customerId);
    }
    
    @Override
    public OrderId getId() {
        return id;
    }
    
    public void addItem(ProductId productId, int quantity, Money unitPrice) {
        OrderItem newItem = OrderItem.create(productId, quantity, unitPrice);
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add items to an order that is not in CREATED status");
        }
        
        // 查找是否已存在相同产品的订单项
        Optional<OrderItem> existingItem = items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // 更新现有项的数量
            existingItem.get().updateQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            // 添加新项
            items.add(newItem);
        }
        
        // 更新总金额
        recalculateTotalAmount();
        
        // 注册领域事件
        registerEvent(new OrderItemAddedEvent(id, productId, quantity, unitPrice));
    }
    
    public void removeItem(ProductId productId) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot remove items from an order that is not in CREATED status");
        }
        
        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            recalculateTotalAmount();
            registerEvent(new OrderItemRemovedEvent(id, productId));
        }
    }
    
    public void placeOrder() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot place an order that is not in CREATED status");
        }
        
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot place an empty order");
        }
        
        status = OrderStatus.PLACED;
        registerEvent(new OrderPlacedEvent(id, customerId, totalAmount));
    }
    
    private void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(item -> item.getUnitPrice().multiply(item.getQuantity()))
                .reduce(Money.zero(Currency.getInstance("USD")), Money::add);
    }
    
    // 其他方法...
}
```

### 2. 仓储实现

```java
// 仓储接口
public interface OrderRepository extends Repository<Order, OrderId> {
    List<Order> findByCustomerId(CustomerId customerId);
    List<Order> findByStatus(OrderStatus status);
}

// 仓储实现
@Repository
public class JpaOrderRepository implements OrderRepository {
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;
    
    @Autowired
    public JpaOrderRepository(OrderJpaRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        Order savedOrder = mapper.toDomain(savedEntity);
        
        // 处理领域事件
        List<DomainEvent> events = order.clearEvents();
        // 发布事件逻辑...
        
        return savedOrder;
    }
    
    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsById(OrderId id) {
        return jpaRepository.existsById(id.getValue());
    }
    
    @Override
    public void delete(Order order) {
        jpaRepository.deleteById(order.getId().getValue());
    }
    
    @Override
    public void deleteById(OrderId id) {
        jpaRepository.deleteById(id.getValue());
    }
    
    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return jpaRepository.findByCustomerId(customerId.getValue())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return jpaRepository.findByStatus(status.name())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
```

### 3. 应用服务实现

```java
// 命令定义
public class PlaceOrderCommand implements Command {
    private final String orderId;
    private final String customerId;
    private final List<OrderItemDTO> items;
    
    // 构造函数、getter等...
}

// 命令处理器
@Component
public class PlaceOrderCommandHandler implements CommandHandler<PlaceOrderCommand, OrderId> {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    
    @Autowired
    public PlaceOrderCommandHandler(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }
    
    @Override
    @Transactional
    public OrderId handle(PlaceOrderCommand command) {
        // 验证客户是否存在
        CustomerId customerId = new CustomerId(command.getCustomerId());
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found: " + customerId);
        }
        
        // 创建订单
        OrderId orderId = new OrderId(command.getOrderId());
        Order order = Order.create(orderId, customerId);
        
        // 添加订单项
        for (OrderItemDTO itemDTO : command.getItems()) {
            ProductId productId = new ProductId(itemDTO.getProductId());
            
            // 验证产品是否存在
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
            
            // 验证库存
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + productId);
            }
            
            // 添加到订单
            order.addItem(productId, itemDTO.getQuantity(), product.getPrice());
            
            // 更新库存
            product.decreaseStock(itemDTO.getQuantity());
            productRepository.save(product);
        }
        
        // 提交订单
        order.placeOrder();
        
        // 保存订单
        orderRepository.save(order);
        
        return orderId;
    }
}

// 应用服务
@Service
public class OrderApplicationService {
    private final CommandBus commandBus;
    private final QueryBus queryBus;
    
    @Autowired
    public OrderApplicationService(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    /**
     * 下订单
     * @param command 下单命令
     * @return 订单ID
     */
    public String placeOrder(PlaceOrderCommand command) {
        OrderId orderId = commandBus.send(command);
        return orderId.getValue();
    }
    
    /**
     * 查询订单
     * @param orderId 订单ID
     * @return 订单DTO
     */
    public OrderDTO getOrder(String orderId) {
        GetOrderQuery query = new GetOrderQuery(orderId);
        return queryBus.send(query);
    }
    
    /**
     * 查询客户订单
     * @param customerId 客户ID
     * @return 订单DTO列表
     */
    public List<OrderDTO> getCustomerOrders(String customerId) {
        GetCustomerOrdersQuery query = new GetCustomerOrdersQuery(customerId);
        return queryBus.send(query);
    }
}
```

### 4. 测试领域模型

```java
@DomainTest
class OrderTest {
    
    private OrderId orderId;
    private CustomerId customerId;
    private Order order;
    
    @BeforeEach
    void setUp() {
        orderId = new OrderId("order-123");
        customerId = new CustomerId("customer-456");
        order = Order.create(orderId, customerId);
    }
    
    @Test
    void shouldCreateOrderWithCorrectInitialState() {
        assertEquals(orderId, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertTrue(order.getItems().isEmpty());
        assertEquals(Money.zero(Currency.getInstance("USD")), order.getTotalAmount());
        
        // 验证领域事件
        assertTrue(DomainTestUtils.hasEventOfType(order, OrderCreatedEvent.class));
        
        OrderCreatedEvent event = DomainTestUtils.getFirstEventOfType(order, OrderCreatedEvent.class)
                .orElseThrow(() -> new AssertionError("OrderCreatedEvent not found"));
        assertEquals(orderId, event.getOrderId());
        assertEquals(customerId, event.getCustomerId());
    }
    
    @Test
    void shouldAddItemToOrder() {
        // 准备
        ProductId productId = new ProductId("product-789");
        int quantity = 2;
        Money unitPrice = Money.of(new BigDecimal("10.00"), Currency.getInstance("USD"));
        
        // 执行
        order.addItem(productId, quantity, unitPrice);
        
        // 验证
        assertEquals(1, order.getItems().size());
        OrderItem item = order.getItems().iterator().next();
        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
        
        // 验证总金额
        Money expectedTotal = unitPrice.multiply(quantity);
        assertEquals(expectedTotal, order.getTotalAmount());
        
        // 验证领域事件
        assertTrue(DomainTestUtils.hasEventOfType(order, OrderItemAddedEvent.class));
    }
    
    @Test
    void shouldRemoveItemFromOrder() {
        // 准备
        ProductId productId = new ProductId("product-789");
        order.addItem(productId, 2, Money.of(new BigDecimal("10.00"), Currency.getInstance("USD")));
        order.clearEvents(); // 清除添加事件
        
        // 执行
        order.removeItem(productId);
        
        // 验证
        assertTrue(order.getItems().isEmpty());
        assertEquals(Money.zero(Currency.getInstance("USD")), order.getTotalAmount());
        
        // 验证领域事件
        assertTrue(DomainTestUtils.hasEventOfType(order, OrderItemRemovedEvent.class));
    }
    
    @Test
    void shouldPlaceOrder() {
        // 准备
        ProductId productId = new ProductId("product-789");
        order.addItem(productId, 2, Money.of(new BigDecimal("10.00"), Currency.getInstance("USD")));
        order.clearEvents(); // 清除添加事件
        
        // 执行
        order.placeOrder();
        
        // 验证
        assertEquals(OrderStatus.PLACED, order.getStatus());
        
        // 验证领域事件
        assertTrue(DomainTestUtils.hasEventOfType(order, OrderPlacedEvent.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPlacingEmptyOrder() {
        // 执行和验证
        Exception exception = assertThrows(IllegalStateException.class, () -> order.placeOrder());
        assertEquals("Cannot place an empty order", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenAddingItemToPlacedOrder() {
        // 准备
        ProductId productId = new ProductId("product-789");
        order.addItem(productId, 2, Money.of(new BigDecimal("10.00"), Currency.getInstance("USD")));
        order.placeOrder();
        order.clearEvents(); // 清除之前的事件
        
        // 执行和验证
        ProductId newProductId = new ProductId("product-999");
        Exception exception = assertThrows(IllegalStateException.class, 
                () -> order.addItem(newProductId, 1, Money.of(new BigDecimal("5.00"), Currency.getInstance("USD"))));
        assertEquals("Cannot add items to an order that is not in CREATED status", exception.getMessage());
    }
}
```

## 最佳实践和注意事项

1. **领域模型设计**
   - 使用充血模型，将业务逻辑封装在领域对象中
   - 确保实体的不变性约束在构造时即被验证
   - 通过工厂方法创建复杂对象，避免复杂的构造函数

2. **值对象使用**
   - 值对象应该是不可变的
   - 使用静态工厂方法而非构造函数创建值对象
   - 验证逻辑应在创建值对象时执行

3. **仓储实现**
   - 仓储应当面向聚合根设计
   - 使用防腐层隔离持久化技术
   - 聚合的持久化应作为一个单元，确保数据一致性

4. **应用服务设计**
   - 应用服务应该是薄的协调层，不包含业务逻辑
   - 使用命令和查询分离模式区分写操作和读操作
   - 事务边界应在应用服务层定义

5. **领域事件处理**
   - 及时处理聚合根产生的领域事件
   - 考虑事件的异步处理和事务一致性
   - 明确定义事件的消费者和处理逻辑

6. **测试策略**
   - 领域模型应有全面的单元测试
   - 使用测试夹具创建测试数据
   - 验证领域事件的产生和内容
