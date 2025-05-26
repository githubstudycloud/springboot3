# 结构化输出功能指南

## 概述

Spring AI 1.0的结构化输出功能允许将AI响应自动映射到Java对象（POJOs）。这使得处理AI输出变得类型安全且易于使用。

## 核心特性

1. **自动JSON映射** - AI响应自动转换为Java对象
2. **类型安全** - 编译时类型检查
3. **复杂结构支持** - 支持嵌套对象和集合
4. **格式指导** - 自动生成JSON Schema指导AI输出格式

## 实现的模型

### 1. MovieRecommendation
```java
- title: 电影标题
- year: 发行年份
- genre: 类型列表
- director: 导演
- plotSummary: 剧情摘要
- rating: 评分
- reasonsToWatch: 推荐理由
- similarMovies: 相似电影
```

### 2. PersonProfile
```java
- name: 姓名
- age: 年龄
- occupation: 职业
- skills: 技能列表
- bio: 个人简介
```

### 3. TechnicalAnalysis
```java
- topic: 主题
- complexityLevel: 复杂度
- summary: 摘要
- keyConcepts: 关键概念（嵌套对象）
- prosAndCons: 优缺点分析
- useCases: 使用场景
- recommendations: 建议
```

## API端点

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/structured/movie/{genre}` | GET | 获取单个电影推荐 |
| `/api/structured/movies/{genre}` | GET | 获取多个电影推荐 |
| `/api/structured/person` | POST | 生成人物档案 |
| `/api/structured/analyze` | POST | 技术主题分析 |
| `/api/structured/custom` | POST | 自定义结构化输出 |

## 使用示例

### 获取电影推荐
```bash
curl http://localhost:8080/api/structured/movie/sci-fi
```

响应示例：
```json
{
  "title": "Interstellar",
  "year": 2014,
  "genre": ["Science Fiction", "Drama", "Adventure"],
  "director": "Christopher Nolan",
  "plotSummary": "A team of explorers travel through a wormhole...",
  "rating": 8.6,
  "reasonsToWatch": [
    "Stunning visual effects",
    "Thought-provoking story",
    "Excellent performances"
  ],
  "similarMovies": ["Inception", "The Martian", "Gravity"]
}
```

### 生成人物档案
```bash
curl -X POST http://localhost:8080/api/structured/person \
  -H "Content-Type: application/json" \
  -d '{"description": "senior Java developer"}'
```

### 技术分析
```bash
curl -X POST http://localhost:8080/api/structured/analyze \
  -H "Content-Type: application/json" \
  -d '{"topic": "Spring AI"}'
```

## 实现原理

1. **BeanOutputConverter** - 将Java类转换为JSON Schema
2. **自动格式指导** - Schema被添加到提示中指导AI输出
3. **响应解析** - AI的JSON响应自动映射到Java对象

## 最佳实践

1. **清晰的字段命名** - 使用描述性的字段名
2. **合适的数据类型** - 选择正确的Java类型
3. **使用JsonProperty** - 明确指定JSON字段名
4. **验证响应** - 添加必要的验证逻辑
5. **错误处理** - 处理解析失败的情况

## 扩展建议

- 添加更多复杂的模型示例
- 实现响应验证
- 支持流式结构化输出
- 添加缓存机制
- 集成数据库持久化
