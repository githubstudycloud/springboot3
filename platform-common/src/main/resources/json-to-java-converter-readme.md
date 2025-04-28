# JSON转Java实体类工具 (JsonToJavaConverter)

## 功能介绍

JsonToJavaConverter是一个强大的Java工具类，可以将任意复杂的JSON字符串转换为对应的Java实体类。主要特点包括：

1. 支持将JSON对象转换为Java类
2. 支持处理嵌套的JSON对象和数组
3. 支持深度解析JSON字符串中的JSON字符串（嵌套JSON字符串）
4. 自动生成完整的Java类，包含属性、Getter/Setter方法和toString方法
5. 自动处理JSON属性名称与Java命名规范的转换
6. 生成的类包含Jackson注解，方便序列化和反序列化

## 使用方法

### 在代码中使用

```java
// 将JSON字符串转换为Java实体类
String jsonStr = "{'name':'John','age':30}";
String className = "User";
String packageName = "com.example.model";
String outputDir = "src/main/java";

// 标准转换（不解析嵌套的JSON字符串）
List<String> files = JsonToJavaConverter.convertJsonToJavaClass(
    jsonStr, className, packageName, outputDir);

// 深度解析转换（解析嵌套的JSON字符串）
List<String> files = JsonToJavaConverter.deepParseAndConvert(
    jsonStr, className, packageName, outputDir);
```

### 使用命令行工具

提供了一个命令行工具`JsonToJavaConverterTool`，可以通过命令行轻松转换JSON文件：

```bash
# 转换JSON文件
java -cp <classpath> com.example.tools.JsonToJavaConverterTool convert input.json User com.example.model src/main/java

# 使用深度解析模式
java -cp <classpath> com.example.tools.JsonToJavaConverterTool convert input.json User com.example.model src/main/java --deep

# 验证JSON文件格式
java -cp <classpath> com.example.tools.JsonToJavaConverterTool validate input.json
```

## 示例

### 简单JSON示例

输入JSON：
```json
{
  "name": "John Doe",
  "age": 30,
  "isActive": true,
  "address": {
    "street": "123 Main St",
    "city": "Anytown",
    "zipCode": "12345"
  },
  "phoneNumbers": [
    {
      "type": "home",
      "number": "555-1234"
    },
    {
      "type": "work",
      "number": "555-5678"
    }
  ]
}
```

将生成以下Java类：
- `User.java`
- `Address.java`
- `PhoneNumber.java`

### 复杂嵌套JSON示例

输入JSON（包含嵌套的JSON字符串）：
```json
{
  "userId": 1,
  "username": "user1",
  "profile": "{\"fullName\":\"User One\",\"preferences\":{\"theme\":\"dark\",\"notifications\":true,\"language\":\"en\"}}",
  "orders": [
    {
      "orderId": 101,
      "items": "[{\"id\":1,\"name\":\"Product A\",\"price\":49.99}]"
    }
  ]
}
```

使用深度解析模式时，将解析嵌套的JSON字符串并生成相应的类。

## 注意事项

1. 生成的Java类会自动处理属性名称，确保符合Java命名规范
2. 会自动处理Java关键字，避免使用保留字作为属性名
3. 对于数组属性，会尝试推断单数形式作为元素类名
4. 生成的类包含`@JsonProperty`注解，确保与原始JSON属性名匹配
5. 生成的类包含`@JsonIgnoreProperties(ignoreUnknown = true)`注解，提高反序列化的灵活性

## 依赖项

该工具依赖于Jackson库进行JSON处理：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.14.0</version>
</dependency>
```

## 后续优化方向

1. 支持自定义实体类的生成模板
2. 添加对Lombok注解的支持
3. 支持根据JSON Schema生成更精确的实体类
4. 提供图形化界面
5. 支持更多的序列化库（如Gson、Moshi等）