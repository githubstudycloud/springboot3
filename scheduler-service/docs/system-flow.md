# 分布式定时任务系统流程设计

## 1. 系统整体架构

```
+---------------------+        +---------------------+
|                     |        |                     |
|   调度节点1          |        |   调度节点2          |
|                     |        |                     |
+----------+----------+        +----------+----------+
           |                              |
           |      +----------------+      |
           |      |                |      |
           +----->|  Redis 集群     |<-----+
           |      |                |      |
           |      +----------------+      |
           |                              |
           |      +----------------+      |
           |      |                |      |
           +----->|  MySQL 数据库   |<-----+
           |      |                |      |
           |      +----------------+      |
           |                              |
           v                              v
    +----------------+           +----------------+
    |                |           |                |
    |  HTTP 任务目标  |           |  HTTP 任务目标  |
    |                |           |                |
    +----------------+           +----------------+
```

## 2. 核心流程图

### 2.1 任务调度流程

```mermaid
sequenceDiagram
    participant Client
    participant API as API服务
    participant Scheduler as 调度器
    participant Redis
    participant DB as MySQL
    participant Executor as 任务执行器
    participant Target as 目标系统

    Client->>API: 创建/更新任务
    API->>DB: 保存任务信息
    API->>Redis: 更新任务缓存
    API->>Client: 返回任务信息

    loop 定时扫描 (每5秒)
        Scheduler->>DB: 查询待执行任务
        DB->>Scheduler: 返回任务列表
        
        loop 每个待执行任务
            Scheduler->>Redis: 尝试获取分布式锁
            alt 获取锁成功
                Redis->>Scheduler: 锁获取成功
                Scheduler->>DB: 更新任务状态为RUNNING
                Scheduler->>Executor: 异步执行任务
                
                Executor->>DB: 创建执行记录
                Executor->>Redis: 更新任务进度(0%)
                Executor->>Target: 发送HTTP请求
                Target->>Executor: 返回响应
                
                alt 执行成功
                    Executor->>DB: 更新执行记录(SUCCESS)
                    Executor->>Redis: 更新任务进度(100%)
                    Executor->>DB: 更新任务状态为ENABLED
                    Executor->>DB: 计算下次执行时间
                else 执行失败
                    Executor->>DB: 更新执行记录(FAILED)
                    Executor->>Redis: 更新任务进度(失败)
                    
                    alt 需要重试
                        Executor->>DB: 更新任务状态为RETRY_PENDING
                        Executor->>DB: 增加重试次数
                        Executor->>DB: 计算重试时间
                    else 不需要重试
                        Executor->>DB: 更新任务状态为FAILED
                    end
                end
                
                Executor->>Redis: 释放分布式锁
            else 获取锁失败
                Redis->>Scheduler: 锁获取失败
                Note over Scheduler: 跳过该任务
            end
        end
    end
```

### 2.2 任务手动触发流程

```mermaid
sequenceDiagram
    participant Client
    participant API as API服务
    participant Redis
    participant DB as MySQL
    participant Executor as 任务执行器
    participant Target as 目标系统

    Client->>API: 请求立即执行任务
    API->>Redis: 尝试获取分布式锁
    
    alt 获取锁成功
        Redis->>API: 锁获取成功
        API->>DB: 创建执行记录(MANUAL触发)
        API->>Client: 返回执行ID
        
        API->>Executor: 异步执行任务
        Executor->>Redis: 更新任务进度(0%)
        Executor->>Target: 发送HTTP请求
        Target->>Executor: 返回响应
        
        alt 执行成功
            Executor->>DB: 更新执行记录(SUCCESS)
            Executor->>Redis: 更新任务进度(100%)
        else 执行失败
            Executor->>DB: 更新执行记录(FAILED)
            Executor->>Redis: 更新任务进度(失败)
        end
        
        Executor->>Redis: 释放分布式锁
    else 获取锁失败
        Redis->>API: 锁获取失败
        API->>Client: 返回任务正在执行中
    end
```

### 2.3 任务状态查询流程

```mermaid
sequenceDiagram
    participant Client
    participant API as API服务
    participant Redis
    participant DB as MySQL

    Client->>API: 请求任务状态
    API->>Redis: 查询任务缓存状态
    
    alt Redis有缓存
        Redis->>API: 返回缓存状态
        API->>Client: 返回任务状态
    else Redis无缓存
        Redis->>API: 缓存未命中
        API->>DB: 查询数据库
        DB->>API: 返回任务状态
        API->>Redis: 更新缓存
        API->>Client: 返回任务状态
    end
```

### 2.4 节点注册与心跳流程

```mermaid
sequenceDiagram
    participant Node as 调度节点
    participant Redis
    participant DB as MySQL

    Note over Node: 节点启动
    Node->>Redis: 注册节点信息
    Node->>DB: 保存节点记录
    
    loop 每30秒
        Node->>Redis: 更新心跳时间
        Node->>DB: 更新心跳记录
    end
    
    Note over Node: 节点关闭
    Node->>Redis: 移除节点信息
    Node->>DB: 更新节点状态为OFFLINE
```

### 2.5 任务失败重试流程

```mermaid
sequenceDiagram
    participant Scheduler as 调度器
    participant Redis
    participant DB as MySQL
    participant Executor as 任务执行器
    participant Target as 目标系统

    Note over Scheduler: 扫描重试任务
    Scheduler->>DB: 查询需要重试的任务
    DB->>Scheduler: 返回重试任务列表
    
    loop 每个重试任务
        Scheduler->>Redis: 尝试获取分布式锁
        alt 获取锁成功
            Redis->>Scheduler: 锁获取成功
            Scheduler->>DB: 更新任务状态为RUNNING
            Scheduler->>Executor: 异步执行任务
            
            Executor->>DB: 创建执行记录(RETRY)
            Executor->>Redis: 更新任务进度(0%)
            Executor->>Target: 发送HTTP请求
            Target->>Executor: 返回响应
            
            alt 执行成功
                Executor->>DB: 更新执行记录(SUCCESS)
                Executor->>Redis: 更新任务进度(100%)
                Executor->>DB: 重置重试次数
                Executor->>DB: 更新任务状态为ENABLED
                Executor->>DB: 计算下次执行时间
            else 执行失败
                Executor->>DB: 更新执行记录(FAILED)
                Executor->>Redis: 更新任务进度(失败)
                
                alt 未达最大重试次数
                    Executor->>DB: 更新任务状态为RETRY_PENDING
                    Executor->>DB: 增加重试次数
                    Executor->>DB: 计算下次重试时间(指数退避)
                else 已达最大重试次数
                    Executor->>DB: 更新任务状态为FAILED
                end
            end
            
            Executor->>Redis: 释放分布式锁
        else 获取锁失败
            Redis->>Scheduler: 锁获取失败
            Note over Scheduler: 跳过该任务
        end
    end
```

## 3. 状态转换图

### 3.1 任务状态转换

```mermaid
stateDiagram-v2
    [*] --> CREATED: 创建任务
    CREATED --> ENABLED: 启用任务
    ENABLED --> RUNNING: 调度执行
    RUNNING --> ENABLED: 执行成功
    RUNNING --> RETRY_PENDING: 执行失败(需重试)
    RUNNING --> FAILED: 执行失败(不重试)
    RETRY_PENDING --> RUNNING: 重试时间到
    RETRY_PENDING --> FAILED: 达到最大重试次数
    ENABLED --> PAUSED: 暂停任务
    PAUSED --> ENABLED: 恢复任务
    ENABLED --> DISABLED: 禁用任务
    DISABLED --> ENABLED: 启用任务
    FAILED --> ENABLED: 手动恢复
    DISABLED --> [*]: 删除任务
    FAILED --> [*]: 删除任务
    CREATED --> [*]: 删除任务
    PAUSED --> [*]: 删除任务
```

### 3.2 执行状态转换

```mermaid
stateDiagram-v2
    [*] --> CREATED: 创建执行记录
    CREATED --> RUNNING: 开始执行
    RUNNING --> SUCCESS: 执行成功
    RUNNING --> FAILED: 执行失败
    RUNNING --> TIMEOUT: 执行超时
    RUNNING --> TERMINATED: 手动终止
    SUCCESS --> [*]
    FAILED --> [*]
    TIMEOUT --> [*]
    TERMINATED --> [*]
```

## 4. 数据流图

### 4.1 任务创建与执行数据流

```
+-------------+     +--------------+     +----------------+
| 任务创建请求 | --> | 任务元数据处理 | --> | 任务持久化存储  |
+-------------+     +--------------+     +----------------+
                                             |
                                             v
+----------------+     +----------------+     +----------------+
| 任务执行结果处理 | <-- | 任务执行过程   | <-- | 任务调度触发   |
+----------------+     +----------------+     +----------------+
       |
       v
+----------------+     +----------------+
| 执行记录持久化  | --> | 任务状态更新   |
+----------------+     +----------------+
                            |
                            v
                     +----------------+
                     | 下次执行时间计算 |
                     +----------------+
```

### 4.2 日志处理数据流

```
+----------------+     +----------------+     +----------------+
| 任务执行事件   | --> | 日志消息生成   | --> | 日志分类处理   |
+----------------+     +----------------+     +----------------+
                                                    |
                                                    v
+----------------+     +----------------+     +----------------+
| 日志查询接口   | <-- | 日志检索索引   | <-- | 日志分表存储   |
+----------------+     +----------------+     +----------------+
```

## 5. 高可用设计

### 5.1 节点故障处理流程

```mermaid
sequenceDiagram
    participant Node1 as 调度节点1
    participant Node2 as 调度节点2
    participant Redis
    participant DB as MySQL
    
    Note over Node1: 节点1正常工作中
    loop 每30秒
        Node1->>Redis: 更新心跳
    end
    
    Note over Node1: 节点1突然宕机
    
    loop 心跳检测(每60秒)
        Node2->>Redis: 检查所有节点心跳
        Note over Node2: 发现节点1心跳超时
        Node2->>DB: 更新节点1状态为OFFLINE
        Node2->>DB: 查询节点1负责的任务
        DB->>Node2: 返回任务列表
        
        loop 每个任务
            alt 任务状态为RUNNING
                Node2->>DB: 检查执行是否真的在进行
                
                alt 执行已经结束但未更新
                    Node2->>DB: 更新任务状态为ENABLED或FAILED
                else 执行确实被中断
                    Node2->>DB: 标记执行为FAILED(节点故障)
                    Node2->>DB: 根据重试策略更新任务状态
                end
            end
        end
    end
```

### 5.2 数据一致性保障流程

```mermaid
sequenceDiagram
    participant Executor as 任务执行器
    participant Redis
    participant DB as MySQL
    
    Note over Executor: 执行任务前后的一致性保障
    
    Executor->>Redis: 尝试获取分布式锁
    alt 获取锁成功
        Redis->>Executor: 锁获取成功
        
        Executor->>DB: 开始事务
        Executor->>DB: 更新任务状态为RUNNING
        Executor->>DB: 创建执行记录
        Executor->>DB: 提交事务
        
        Note over Executor: 执行任务
        
        Executor->>DB: 开始事务
        Executor->>DB: 更新执行记录状态
        Executor->>DB: 更新任务状态和下次执行时间
        Executor->>DB: 提交事务
        
        Executor->>Redis: 释放分布式锁
    else 获取锁失败
        Redis->>Executor: 锁获取失败
        Note over Executor: 跳过任务执行
    end
```

## 6. 扩展与集成流程

### 6.1 多数据源集成流程

```mermaid
sequenceDiagram
    participant Client
    participant API as API服务
    participant DSM as 数据源管理器
    participant DS as 外部数据源
    participant Task as 任务执行器

    Client->>API: 注册外部数据源
    API->>DSM: 保存数据源配置
    API->>Client: 返回数据源ID
    
    Client->>API: 创建使用外部数据源的任务
    API->>DSM: 验证数据源可用性
    DSM->>DS: 测试连接
    DS->>DSM: 连接成功
    DSM->>API: 数据源可用
    API->>Client: 返回任务创建成功
    
    Note over Task: 任务执行时
    Task->>DSM: 获取数据源连接
    DSM->>DS: 建立连接
    DS->>DSM: 返回连接
    DSM->>Task: 返回数据源连接
    
    Task->>DS: 执行数据操作
    DS->>Task: 返回数据结果
    
    Task->>DSM: 关闭数据源连接
```

### 6.2 Redis集群状态同步流程

```mermaid
sequenceDiagram
    participant Executor as 任务执行器
    participant Redis
    participant Client

    Note over Executor: 任务执行过程中
    
    Executor->>Redis: 更新任务状态和进度
    
    loop 每秒
        Client->>Redis: 订阅任务状态更新
        Redis->>Client: 推送最新状态
        Client->>Client: 更新UI显示
    end
    
    Executor->>Redis: 发布任务完成事件
    Redis->>Client: 推送任务完成通知
    Client->>Client: 显示任务完成提醒
```
