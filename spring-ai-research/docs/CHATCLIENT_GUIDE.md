# ChatClient 功能指南

## 概述

ChatClient是Spring AI 1.0的核心API，提供了与AI模型交互的流畅接口。本指南介绍如何使用已实现的ChatClient功能。

## 功能特性

1. **简单对话** - 基础的问答交互
2. **系统提示** - 自定义AI的行为和角色
3. **模板化提示** - 使用参数化模板生成动态提示
4. **元数据响应** - 获取完整的响应信息，包括token使用情况

## 快速开始

### 1. 启动应用

```bash
cd spring-ai-research
mvn spring-boot:run
```

### 2. API端点

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/chat/simple` | POST | 简单对话 |
| `/api/chat/system` | POST | 带系统提示的对话 |
| `/api/chat/template` | POST | 模板化对话 |
| `/api/chat/metadata` | POST | 获取完整响应元数据 |
| `/api/chat/health` | GET | 健康检查 |

### 3. 使用示例

#### 简单对话
```bash
curl -X POST http://localhost:8080/api/chat/simple \
  -H "Content-Type: text/plain" \
  -d "What is Spring AI?"
```

#### 带系统提示的对话
```bash
curl -X POST http://localhost:8080/api/chat/system \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explain dependency injection",
    "systemPrompt": "You are a Spring expert. Explain concepts simply."
  }'
```

#### 模板化对话
```bash
curl -X POST http://localhost:8080/api/chat/template \
  -H "Content-Type: application/json" \
  -d '{
    "template": "Write a {style} poem about {topic}",
    "params": {
      "style": "haiku",
      "topic": "microservices"
    }
  }'
```

## 配置选项

在`application.yml`中可以配置不同的AI提供商：

- **OpenAI**: 设置 `OPENAI_API_KEY` 环境变量
- **Anthropic**: 设置 `ANTHROPIC_API_KEY` 环境变量
- **Ollama**: 本地运行，默认地址 `http://localhost:11434`

## 下一步

- 实现流式响应支持
- 添加对话历史管理
- 集成更多AI提供商
