# 报告功能增强总结

## 概述

根据用户需求，对监控系统的报告功能进行了以下两项重要增强：

1. **锁等待每30分钟报告一次新增的**
2. **长时间SQL异常报告要打印State和info，并且info要有值才报告**

## 实现的功能增强

### 🔒 锁等待增量监控 (已完善)

#### 现有实现
- ✅ **每30分钟定时执行**: 使用cron表达式 `"0 0,30 * * * *"`
- ✅ **新增锁等待计算**: 计算过去30分钟的锁等待增长差值，而非累计值
- ✅ **MySQL重启检测**: 自动处理MySQL重启导致的累计值重置

#### 本次增强
- ✅ **报告内容优化**: 明确强调报告的是"新增"锁等待数量
- ✅ **状态说明**: 在报告中添加监控说明，解释报告的是增量而非累计值
- ✅ **视觉标识**: 使用emoji区分不同状态（🔴新增、🟢正常、⚪未知）
- ✅ **操作建议**: 为有锁等待新增的情况提供具体的排查建议

#### 报告示例
```
⚠️ 锁等待新增警告
时间: 2024-01-20 14:30:05
检查周期: 每30分钟
监控说明: 报告的是过去30分钟内新增的锁等待数量，非累计值
检查结果:
  - db1: 🔴 过去30分钟新增锁等待 5 个 (当前累计: 120)
  - db2: 🟢 过去30分钟无新增锁等待 (当前累计: 85)

⚠️ 建议操作:
1. 检查相关SQL语句是否存在长时间锁定
2. 分析数据库并发访问模式
3. 考虑优化事务处理逻辑
4. 检查是否有死锁情况发生
```

### 🐌 长时间SQL监控 (已增强)

#### 原有功能
- ✅ 检测运行超过30分钟的SQL查询
- ✅ 基本的进程信息显示

#### 本次增强

##### 1. 严格过滤条件
- ✅ **Info字段验证**: 只报告info字段有实际SQL内容的进程
- ✅ **使用ProcessInfo.hasValidInfo()**: 确保info不为null、不为空字符串、且去除空白后有内容

##### 2. 详细信息显示
- ✅ **明确标注字段**: 清楚地标注State和Info字段
- ✅ **时间格式优化**: 显示小时和分钟的组合格式
- ✅ **更长SQL显示**: 从100字符增加到200字符显示长度
- ✅ **数据库信息**: 包含查询所在的数据库名称

##### 3. 报告格式优化
- ✅ **结构化显示**: 每个字段单独一行，层次清晰
- ✅ **操作建议**: 提供SQL优化建议

#### 报告示例
```
⚠️ 长时间SQL异常报告
时间: 2024-01-20 14:30:05
发现超过30分钟且有有效SQL内容的查询:

数据源: db1
  - ID: 12345, 用户: app_user, 运行时间: 1小时25分钟
    状态(State): Sorting result
    SQL内容(Info): SELECT * FROM large_table t1 JOIN another_table t2 ON t1.id = t2.ref_id WHERE t1.created_date >= '2024-01-01' ORDER BY t1.created_date DESC LIMIT 1000...
    数据库: production_db

  - ID: 12346, 用户: report_user, 运行时间: 45分钟  
    状态(State): Sending data
    SQL内容(Info): SELECT COUNT(*) as total, AVG(amount) as avg_amount FROM transactions WHERE date_range BETWEEN '2023-01-01' AND '2024-01-20' GROUP BY customer_id...
    数据库: analytics_db

建议: 检查这些长时间运行的SQL语句，可能需要优化或添加适当的索引。
```

## 代码修改位置

### 修改的文件
- `src/main/java/com/example/dbmonitor/service/ScheduledReportService.java`

### 修改的方法
1. **analyzeLongRunningSql()**: 添加info字段验证过滤
2. **buildLongRunningSqlMessage()**: 增强报告格式，明确显示State和Info
3. **buildLockWaitReportMessage()**: 优化锁等待报告说明

## 技术实现细节

### Info字段验证逻辑
```java
.filter(p -> p.hasValidInfo()) // 使用ProcessInfo类的hasValidInfo()方法
```

### State和Info显示逻辑
```java
// 明确打印State字段
String state = process.getState();
sb.append(String.format("    状态(State): %s\n", 
        state != null && !state.trim().isEmpty() ? state : "未知"));

// 明确打印Info字段
String info = process.getInfo();
if (info != null && !info.trim().isEmpty()) {
    String displaySql = info.length() > 200 ? info.substring(0, 200) + "..." : info;
    sb.append("    SQL内容(Info): ").append(displaySql).append("\n");
}
```

### 时间显示优化
```java
long hours = minutes / 60;
long remainingMinutes = minutes % 60;

String timeDisplay = hours > 0 
    ? String.format("%d小时%d分钟", hours, remainingMinutes)
    : String.format("%d分钟", minutes);
```

## 预期效果

### 锁等待监控
- 📊 **精确监控**: 准确反映数据库锁等待的增长趋势
- 🚨 **及时预警**: 30分钟周期确保问题及时发现
- 💡 **可操作性**: 提供具体的排查和优化建议

### 长时间SQL监控  
- 🎯 **精准过滤**: 只关注真正有SQL内容的异常查询
- 📋 **信息完整**: State和Info字段提供完整的诊断信息
- 🔍 **易于分析**: 结构化显示便于快速定位问题

## 总结

本次增强确保了监控系统能够：
1. **准确报告锁等待增量**: 避免累计值造成的误判
2. **精确定位SQL问题**: 只关注有实际执行内容的长时间查询
3. **提供完整诊断信息**: State和Info字段助力问题分析
4. **增强可操作性**: 提供具体的问题排查建议

这些改进显著提升了监控系统的实用性和准确性。 