# 分布式定时任务系统API文档

## 1. 任务管理API

### 1.1 创建任务

**接口说明**：创建一个新的定时任务

**请求方法**：POST

**请求路径**：/api/tasks

**请求参数**：

```json
{
  "name": "任务名称",
  "description": "任务描述",
  "type": "HTTP",
  "target": "http://example.com/api/test",
  "parameters": {
    "method": "POST",
    "headers": {
      "Content-Type": "application/json",
      "Authorization": "Bearer token"
    },
    "body": "{\"key\":\"value\"}"
  },
  "scheduleType": "CRON",
  "cronExpression": "0 0/30 * * * ?",
  "fixedRate": null,
  "fixedDelay": null,
  "initialDelay": 0,
  "timeout": 30000,
  "retryCount": 3,
  "retryInterval": 60000
}
```

**响应结果**：

```json
{
  "id": 1,
  "name": "任务名称",
  "description": "任务描述",
  "type": "HTTP",
  "target": "http://example.com/api/test",
  "parameters": {
    "method": "POST",
    "headers": {
      "Content-Type": "application/json",
      "Authorization": "Bearer token"
    },
    "body": "{\"key\":\"value\"}"
  },
  "scheduleType": "CRON",
  "cronExpression": "0 0/30 * * * ?",
  "fixedRate": null,
  "fixedDelay": null,
  "initialDelay": 0,
  "timeout": 30000,
  "retryCount": 3,
  "retryInterval": 60000,
  "status": "CREATED",
  "createdTime": "2025-04-21T10:00:00",
  "updatedTime": "2025-04-21T10:00:00"
}
```

### 1.2 更新任务

**接口说明**：更新现有定时任务

**请求方法**：PUT

**请求路径**：/api/tasks/{taskId}

**请求参数**：同创建任务

**响应结果**：同创建任务

### 1.3 删除任务

**接口说明**：删除定时任务

**请求方法**：DELETE

**请求路径**：/api/tasks/{taskId}

**响应结果**：HTTP状态码204（无内容）表示成功

### 1.4 获取任务详情

**接口说明**：获取定时任务详情

**请求方法**：GET

**请求路径**：/api/tasks/{taskId}

**响应结果**：同创建任务响应

### 1.5 查询任务列表

**接口说明**：分页查询任务列表

**请求方法**：GET

**请求路径**：/api/tasks

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认10
- status: 任务状态，可选
- type: 任务类型，可选
- name: 任务名称，模糊匹配，可选

**响应结果**：

```json
{
  "content": [
    {
      "id": 1,
      "name": "任务名称",
      "description": "任务描述",
      "type": "HTTP",
      "status": "RUNNING",
      "lastExecutionTime": "2025-04-21T09:30:00",
      "nextExecutionTime": "2025-04-21T10:00:00",
      "createdTime": "2025-04-20T10:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

### 1.6 启用任务

**接口说明**：启用一个任务

**请求方法**：PUT

**请求路径**：/api/tasks/{taskId}/enable

**响应结果**：更新后的任务详情

### 1.7 禁用任务

**接口说明**：禁用一个任务

**请求方法**：PUT

**请求路径**：/api/tasks/{taskId}/disable

**响应结果**：更新后的任务详情

### 1.8 暂停任务

**接口说明**：暂停一个启用的任务

**请求方法**：PUT

**请求路径**：/api/tasks/{taskId}/pause

**响应结果**：更新后的任务详情

### 1.9 恢复任务

**接口说明**：恢复一个暂停的任务

**请求方法**：PUT

**请求路径**：/api/tasks/{taskId}/resume

**响应结果**：更新后的任务详情

### 1.10 触发任务

**接口说明**：手动触发任务执行

**请求方法**：POST

**请求路径**：/api/tasks/{taskId}/trigger

**响应结果**：执行ID

### 1.11 更新任务状态

**接口说明**：更新任务状态

**请求方法**：PUT

**请求路径**：/api/tasks/{taskId}/status

**请求参数**：status=ENABLED（请求参数）

**响应结果**：HTTP状态码200表示成功

### 1.12 更新任务下次执行时间

**接口说明**：更新任务下次执行时间

**请求方法**：PUT

**请求路径**：/api/tasks/{taskId}/next-execution-time

**请求参数**：nextExecutionTime=2025-04-22T10:00:00（请求参数）

**响应结果**：HTTP状态码200表示成功

## 2. 任务执行管理API

### 2.1 创建执行记录

**接口说明**：创建任务执行记录

**请求方法**：POST

**请求路径**：/api/executions

**请求参数**：

- taskId: 任务ID
- nodeId: 节点ID
- triggerType: 触发类型，可选值：SCHEDULED, MANUAL

**响应结果**：执行ID

### 2.2 完成执行

**接口说明**：标记执行为完成状态

**请求方法**：PUT

**请求路径**：/api/executions/{executionId}/complete

**请求参数**：

```json
{
  "success": true,
  "data": "执行结果数据"
}
```

**响应结果**：HTTP状态码200表示成功

### 2.3 标记执行失败

**接口说明**：标记执行为失败状态

**请求方法**：PUT

**请求路径**：/api/executions/{executionId}/fail

**请求参数**：errorMessage=执行失败原因（请求参数）

**响应结果**：HTTP状态码200表示成功

### 2.4 标记执行超时

**接口说明**：标记执行为超时状态

**请求方法**：PUT

**请求路径**：/api/executions/{executionId}/timeout

**响应结果**：HTTP状态码200表示成功

### 2.5 终止执行

**接口说明**：终止正在进行的执行

**请求方法**：PUT

**请求路径**：/api/executions/{executionId}/terminate

**响应结果**：HTTP状态码200表示成功

### 2.6 获取执行详情

**接口说明**：获取执行记录详情

**请求方法**：GET

**请求路径**：/api/executions/{executionId}

**响应结果**：

```json
{
  "id": 100,
  "taskId": 1,
  "nodeId": "scheduler-node-1",
  "triggerType": "SCHEDULED",
  "startTime": "2025-04-21T09:30:00",
  "endTime": "2025-04-21T09:31:20",
  "status": "SUCCESS",
  "result": "{\"success\":true,\"data\":{...}}",
  "errorMessage": null
}
```

### 2.7 查询任务的执行记录

**接口说明**：查询某个任务的执行记录

**请求方法**：GET

**请求路径**：/api/executions/task/{taskId}

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认10
- status: 执行状态，可选

**响应结果**：执行记录分页结果

### 2.8 查询任务的最近一次执行

**接口说明**：查询某个任务的最近一次执行记录

**请求方法**：GET

**请求路径**：/api/executions/task/{taskId}/latest

**响应结果**：执行记录详情

### 2.9 查询节点的执行记录

**接口说明**：查询某个节点的执行记录

**请求方法**：GET

**请求路径**：/api/executions/node/{nodeId}

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认10

**响应结果**：执行记录分页结果

### 2.10 查询特定状态的执行记录

**接口说明**：查询特定状态的执行记录

**请求方法**：GET

**请求路径**：/api/executions/status/{status}

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认10

**响应结果**：执行记录分页结果

### 2.11 查询时间范围内的执行记录

**接口说明**：查询时间范围内的执行记录

**请求方法**：GET

**请求路径**：/api/executions/time-range

**请求参数**：

- startTime: 开始时间，格式：ISO 8601
- endTime: 结束时间，格式：ISO 8601
- page: 页码，默认0
- size: 每页大小，默认10

**响应结果**：执行记录分页结果

### 2.12 查询任务在时间范围内的执行记录

**接口说明**：查询任务在时间范围内的执行记录

**请求方法**：GET

**请求路径**：/api/executions/task/{taskId}/time-range

**请求参数**：

- startTime: 开始时间，格式：ISO 8601
- endTime: 结束时间，格式：ISO 8601
- page: 页码，默认0
- size: 每页大小，默认10

**响应结果**：执行记录分页结果

### 2.13 计算任务执行成功率

**接口说明**：计算任务在时间范围内的执行成功率

**请求方法**：GET

**请求路径**：/api/executions/task/{taskId}/success-rate

**请求参数**：

- startTime: 开始时间，格式：ISO 8601
- endTime: 结束时间，格式：ISO 8601

**响应结果**：成功率（0-100）

### 2.14 计算任务平均执行时间

**接口说明**：计算任务在时间范围内的平均执行时间

**请求方法**：GET

**请求路径**：/api/executions/task/{taskId}/average-execution-time

**请求参数**：

- startTime: 开始时间，格式：ISO 8601
- endTime: 结束时间，格式：ISO 8601

**响应结果**：平均执行时间（毫秒）

## 3. 日志管理API

### 3.1 记录信息日志

**接口说明**：记录信息级别日志

**请求方法**：POST

**请求路径**：/api/logs/info

**请求参数**：

```json
{
  "taskId": 1,
  "executionId": 100,
  "nodeId": "scheduler-node-1",
  "message": "任务开始执行"
}
```

**响应结果**：日志ID

### 3.2 记录警告日志

**接口说明**：记录警告级别日志

**请求方法**：POST

**请求路径**：/api/logs/warning

**请求参数**：同记录信息日志

**响应结果**：日志ID

### 3.3 记录错误日志

**接口说明**：记录错误级别日志

**请求方法**：POST

**请求路径**：/api/logs/error

**请求参数**：同记录信息日志

**响应结果**：日志ID

### 3.4 查询任务日志

**接口说明**：查询任务的日志

**请求方法**：GET

**请求路径**：/api/logs/task/{taskId}

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认20

**响应结果**：日志分页结果

### 3.5 查询执行日志

**接口说明**：查询执行记录的日志

**请求方法**：GET

**请求路径**：/api/logs/execution/{executionId}

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认20

**响应结果**：日志分页结果

### 3.6 查询级别日志

**接口说明**：查询特定级别的日志

**请求方法**：GET

**请求路径**：/api/logs/level/{level}

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认20

**响应结果**：日志分页结果

### 3.7 多条件查询日志

**接口说明**：根据多条件查询日志

**请求方法**：GET

**请求路径**：/api/logs

**请求参数**：

```json
{
  "taskId": 1,
  "executionId": 100,
  "level": "INFO",
  "startTime": "2025-04-21T00:00:00",
  "endTime": "2025-04-21T23:59:59",
  "keyword": "执行"
}
```

**响应结果**：日志分页结果

### 3.8 清理过期日志

**接口说明**：清理指定天数前的日志

**请求方法**：POST

**请求路径**：/api/logs/cleanup

**请求参数**：retentionDays=30（请求参数）

**响应结果**：清理的记录数

### 3.9 导出日志

**接口说明**：导出日志

**请求方法**：POST

**请求路径**：/api/logs/export

**请求参数**：同多条件查询日志

**响应结果**：导出文件路径

## 4. 调度节点管理API

### 4.1 注册节点

**接口说明**：注册调度节点

**请求方法**：POST

**请求路径**：/api/nodes/register

**请求参数**：

```json
{
  "id": "scheduler-node-1",
  "host": "scheduler-host-1",
  "ip": "192.168.1.100",
  "port": 8080
}
```

**响应结果**：注册后的节点信息

### 4.2 更新节点心跳

**接口说明**：更新节点心跳

**请求方法**：PUT

**请求路径**：/api/nodes/{nodeId}/heartbeat

**响应结果**：HTTP状态码200表示成功

### 4.3 节点下线

**接口说明**：下线节点

**请求方法**：PUT

**请求路径**：/api/nodes/{nodeId}/offline

**响应结果**：HTTP状态码200表示成功

### 4.4 获取节点详情

**接口说明**：获取节点详情

**请求方法**：GET

**请求路径**：/api/nodes/{nodeId}

**响应结果**：节点详情

### 4.5 查询在线节点

**接口说明**：查询所有在线节点

**请求方法**：GET

**请求路径**：/api/nodes/online

**响应结果**：节点列表

### 4.6 查询所有节点

**接口说明**：分页查询所有节点

**请求方法**：GET

**请求路径**：/api/nodes

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认10

**响应结果**：节点分页结果

### 4.7 查询特定状态节点

**接口说明**：查询特定状态的节点

**请求方法**：GET

**请求路径**：/api/nodes/status/{status}

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认10

**响应结果**：节点分页结果

### 4.8 处理超时节点

**接口说明**：处理心跳超时的节点

**请求方法**：POST

**请求路径**：/api/nodes/handle-timeout

**请求参数**：timeoutSeconds=60（请求参数）

**响应结果**：处理的节点数

### 4.9 获取当前节点

**接口说明**：获取当前节点信息

**请求方法**：GET

**请求路径**：/api/nodes/current

**响应结果**：当前节点信息

### 4.10 选择节点执行任务

**接口说明**：为任务选择可用的执行节点

**请求方法**：GET

**请求路径**：/api/nodes/select

**请求参数**：taskId=1（请求参数）

**响应结果**：选中的节点信息

### 4.11 重新分配节点任务

**接口说明**：重新分配节点上的任务

**请求方法**：POST

**请求路径**：/api/nodes/{nodeId}/reassign-tasks

**响应结果**：重分配的任务数

## 5. 任务进度管理API

### 5.1 更新任务进度

**接口说明**：更新任务进度

**请求方法**：PUT

**请求路径**：/api/progress

**请求参数**：

- taskId: 任务ID
- executionId: 执行ID
- progress: 进度百分比(0-100)
- statusDesc: 状态描述（可选）

**响应结果**：进度ID

### 5.2 完成任务进度

**接口说明**：完成任务进度

**请求方法**：PUT

**请求路径**：/api/progress/complete

**请求参数**：

- taskId: 任务ID
- executionId: 执行ID

**响应结果**：HTTP状态码200表示成功

### 5.3 获取任务进度

**接口说明**：获取任务特定执行的进度

**请求方法**：GET

**请求路径**：/api/progress/task/{taskId}/execution/{executionId}

**响应结果**：任务进度信息

### 5.4 获取任务最新进度

**接口说明**：获取任务的最新进度

**请求方法**：GET

**请求路径**：/api/progress/task/{taskId}/latest

**响应结果**：任务进度信息

### 5.5 获取执行进度

**接口说明**：获取执行记录的进度

**请求方法**：GET

**请求路径**：/api/progress/execution/{executionId}

**响应结果**：任务进度信息

### 5.6 查询任务进度历史

**接口说明**：查询任务的进度历史

**请求方法**：GET

**请求路径**：/api/progress/task/{taskId}/history

**请求参数**：

- page: 页码，默认0
- size: 每页大小，默认10

**响应结果**：进度历史分页结果

### 5.7 清理进度记录

**接口说明**：清理已完成的进度记录

**请求方法**：POST

**请求路径**：/api/progress/cleanup

**请求参数**：retentionCount=10（请求参数）

**响应结果**：清理的记录数
