# platform-common

## 项目概述

`platform-common` 是企业级数据平台的通用工具和组件模块，为整个平台提供共享的基础功能。该模块遵循高内聚低耦合原则，将通用功能封装为可复用的组件，避免代码重复，提高开发效率和代码质量。

## 模块结构

`platform-common` 是一个多模块项目，由以下子模块组成：

1. **platform-common-core**: 核心工具类和通用组件
2. **platform-common-web**: Web应用相关的通用组件
3. **platform-common-security**: 安全相关的通用组件
4. **platform-common-logging**: 日志相关的通用组件

```
platform-common/
├── platform-common-core/                # 核心工具类
│   ├── src/main/java/com/platform/common/core/
│   │   ├── annotation/                  # 通用注解
│   │   ├── constant/                    # 常量定义
│   │   ├── exception/                   # 异常类
│   │   ├── util/                        # 工具类
│   │   │   ├── collection/              # 集合工具
│   │   │   ├── date/                    # 日期工具
│   │   │   ├── string/                  # 字符串工具
│   │   │   ├── reflect/                 # 反射工具
│   │   │   └── id/                      # ID生成工具
│   │   ├── domain/                      # 领域基类
│   │   ├── base/                        # 基础抽象类
│   │   ├── model/                       # 通用模型
│   │   └── validation/                  # 校验工具
│   └── pom.xml
├── platform-common-web/                 # Web相关通用组件
│   ├── src/main/java/com/platform/common/web/
│   │   ├── advice/                      # 通用切面
│   │   ├── annotation/                  # Web注解
│   │   ├── config/                      # Web配置
│   │   ├── exception/                   # Web异常处理
│   │   ├── interceptor/                 # 拦截器
│   │   ├── result/                      # 统一响应封装
│   │   ├── util/                        # Web工具类
│   │   └── validator/                   # 数据校验器
│   └── pom.xml
├── platform-common-security/            # 安全相关组件
│   ├── src/main/java/com/platform/common/security/
│   │   ├── annotation/                  # 安全注解
│   │   ├── config/                      # 安全配置
│   │   ├── context/                     # 安全上下文
│   │   ├── crypto/                      # 加密工具
│   │   ├── filter/                      # 安全过滤器
│   │   ├── handler/                     # 安全处理器
│   │   ├── model/                       # 安全模型
│   │   ├── service/                     # 安全服务
│   │   └── util/                        # 安全工具
│   └── pom.xml
├── platform-common-logging/             # 日志相关组件
│   ├── src/main/java/com/platform/common/logging/
│   │   ├── annotation/                  # 日志注解
│   │   ├── aspect/                      # 日志切面
│   │   ├── config/                      # 日志配置
│   │   ├── context/                     # 日志上下文
│   │   ├── event/                       # 日志事件
│   │   ├── format/                      # 日志格式化
│   │   └── util/                        # 日志工具
│   └── pom.xml
├── pom.xml                              # 父POM
└── README.md                            # 模块说明
```

## 核心功能

### 1. platform-common-core

核心工具类模块提供基础功能支持，主要包括：

- **通用工具类**：字符串、日期、反射、集合等常用工具封装
- **通用领域对象**：基础实体类、值对象、聚合根等领域模型基类
- **异常体系**：统一的异常处理框架和基类
- **ID生成器**：分布式环境下的唯一ID生成工具
- **通用验证工具**：参数校验工具和通用验证注解

#### 核心工具类示例

```java
/**
 * 字符串工具类
 * 提供字符串处理的各种实用方法
 */
public class StringUtils {
    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * 判断字符串是否为空或null
     * 
     * @param str 待检查字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * 判断字符串是否为空、null或仅包含空格
     * 
     * @param str 待检查字符串
     * @return 是否为空
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 查找字符串中的字符位置，支持从指定位置开始查找
     * 
     * @param str 源字符串
     * @param searchChar 要查找的字符
     * @param startPos 开始位置
     * @return 位置索引，未找到返回-1
     */
    public static int indexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar, startPos);
    }
    
    /**
     * 安全截取字符串
     * 
     * @param str 源字符串
     * @param start 开始位置
     * @param end 结束位置
     * @return 截取后的字符串
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        
        // 边界检查
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            return "";
        }
        
        return str.substring(start, end);
    }
    
    /**
     * 格式化字符串，替换{}占位符
     * 
     * @param pattern 字符串模板
     * @param args 参数列表
     * @return 格式化后的字符串
     */
    public static String format(String pattern, Object... args) {
        if (pattern == null) {
            return null;
        }
        if (args == null || args.length == 0) {
            return pattern;
        }
        
        StringBuilder builder = new StringBuilder(pattern.length() + 50);
        int i = 0;
        int j = 0;
        int start = 0;
        int paramIndex = 0;
        
        while (i < pattern.length()) {
            if (pattern.charAt(i) == '{' && i + 1 < pattern.length() && pattern.charAt(i + 1) == '}') {
                builder.append(pattern, start, i);
                if (paramIndex < args.length) {
                    builder.append(args[paramIndex]);
                } else {
                    builder.append("{}");
                }
                start = i + 2;
                i += 2;
                paramIndex++;
            } else {
                i++;
            }
        }
        
        builder.append(pattern, start, pattern.length());
        return builder.toString();
    }
    
    // 其他字符串工具方法...
}
```

#### 领域基类示例

```java
/**
 * 实体基类，提供通用的实体功能
 *
 * @param <ID> ID类型参数
 */
public abstract class BaseEntity<ID> implements Serializable {
    private static final long serialVersionUID = -7376297708555471521L;
    
    /**
     * 获取实体ID
     *
     * @return 实体ID
     */
    public abstract ID getId();
    
    /**
     * 判断实体是否为全新实体（未持久化）
     *
     * @return 如果实体是新建的，返回true
     */
    public abstract boolean isNew();
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        BaseEntity<?> that = (BaseEntity<?>) obj;
        
        // 如果两个实体都是新实体（ID为null），则比较内存地址
        if (this.getId() == null && that.getId() == null) {
            return this == that;
        }
        
        // 否则比较ID是否相等
        return this.getId() != null && this.getId().equals(that.getId());
    }
    
    @Override
    public int hashCode() {
        return getId() == null ? System.identityHashCode(this) : getId().hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s{id=%s}", getClass().getSimpleName(), getId());
    }
}
```

### 2. platform-common-web

Web相关通用组件模块提供与Web应用相关的功能：

- **统一响应处理**：规范化API响应格式
- **全局异常处理**：统一处理Web应用异常
- **请求日志记录**：自动记录HTTP请求和响应日志
- **数据校验**：请求参数验证框架
- **常用过滤器**：XSS过滤、SQL注入防护等

#### 统一响应封装示例

```java
/**
 * 统一API响应结果封装
 *
 * @param <T> 数据类型
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private int code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 请求追踪ID
     */
    private String requestId;
    
    /**
     * 时间戳
     */
    private long timestamp;
    
    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @return 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMessage());
    }
    
    /**
     * 成功响应（有数据）
     * 
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMessage())
                .setData(data);
    }
    
    /**
     * 失败响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return 失败响应
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<T>()
                .setCode(code)
                .setMessage(message);
    }
    
    /**
     * 预定义错误响应
     * 
     * @param resultCode 结果代码枚举
     * @return 失败响应
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }
    
    /**
     * 参数错误响应
     * 
     * @param message 错误消息
     * @return 参数错误响应
     */
    public static <T> Result<T> invalidParam(String message) {
        return error(ResultCode.INVALID_PARAM.getCode(), message);
    }
    
    /**
     * 未授权响应
     * 
     * @return 未授权响应
     */
    public static <T> Result<T> unauthorized() {
        return error(ResultCode.UNAUTHORIZED);
    }
    
    /**
     * 服务器错误响应
     * 
     * @return 服务器错误响应
     */
    public static <T> Result<T> serverError() {
        return error(ResultCode.SERVER_ERROR);
    }
    
    /**
     * 业务异常响应
     * 
     * @param message 错误消息
     * @return 业务异常响应
     */
    public static <T> Result<T> businessError(String message) {
        return error(ResultCode.BUSINESS_ERROR.getCode(), message);
    }
    
    // getter/setter方法
    
    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }
    
    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }
    
    public Result<T> setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
    
    // 常用的getter方法略
}
```

#### 全局异常处理示例

```java
/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.businessError(e.getMessage());
    }
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append(": ");
            sb.append(fieldError.getDefaultMessage()).append(", ");
        }
        
        String msg = sb.toString();
        if (msg.endsWith(", ")) {
            msg = msg.substring(0, msg.length() - 2);
        }
        
        log.warn("参数验证失败: {}", msg);
        return Result.invalidParam(msg);
    }
    
    /**
     * 处理权限不足异常
     */
    @ExceptionHandler({AccessDeniedException.class})
    public Result<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.error(ResultCode.FORBIDDEN);
    }
    
    /**
     * 处理认证异常
     */
    @ExceptionHandler({AuthenticationException.class})
    public Result<?> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return Result.unauthorized();
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.serverError();
    }
    
    /**
     * 处理未预期的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.SERVER_ERROR.getCode(), "系统繁忙，请稍后再试");
    }
}
```

### 3. platform-common-security

安全相关模块提供安全管理功能：

- **加密工具**：提供各种加密/解密算法
- **安全上下文**：管理当前用户和权限信息
- **身份验证**：通用身份验证组件
- **敏感数据处理**：敏感信息脱敏和加密

#### 安全上下文示例

```java
/**
 * 安全上下文，用于获取当前用户信息
 */
public final class SecurityContext {
    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();
    
    private SecurityContext() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * 设置当前用户信息
     * 
     * @param userInfo 用户信息
     */
    public static void setCurrentUser(UserInfo userInfo) {
        USER_HOLDER.set(userInfo);
    }
    
    /**
     * 获取当前用户信息
     * 
     * @return 用户信息
     */
    public static UserInfo getCurrentUser() {
        return USER_HOLDER.get();
    }
    
    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    public static String getCurrentUserId() {
        UserInfo userInfo = getCurrentUser();
        return userInfo != null ? userInfo.getUserId() : null;
    }
    
    /**
     * 检查是否已认证
     * 
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
    
    /**
     * 检查是否具有指定角色
     * 
     * @param role 角色
     * @return 是否具有角色
     */
    public static boolean hasRole(String role) {
        UserInfo userInfo = getCurrentUser();
        if (userInfo == null || userInfo.getRoles() == null) {
            return false;
        }
        return userInfo.getRoles().contains(role);
    }
    
    /**
     * 检查是否具有指定权限
     * 
     * @param permission 权限
     * @return 是否具有权限
     */
    public static boolean hasPermission(String permission) {
        UserInfo userInfo = getCurrentUser();
        if (userInfo == null || userInfo.getPermissions() == null) {
            return false;
        }
        return userInfo.getPermissions().contains(permission);
    }
    
    /**
     * 清除当前用户信息
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}
```

#### 加密工具示例

```java
/**
 * 加密工具类
 */
public class CryptoUtils {
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_SIZE = 16;
    
    private CryptoUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * 生成AES密钥
     * 
     * @return AES密钥（Base64编码）
     */
    public static String generateAesKey() throws GeneralSecurityException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE);
        SecretKey key = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * AES加密
     * 
     * @param plaintext 明文
     * @param keyBase64 Base64编码的密钥
     * @return 加密后的数据（Base64编码）
     */
    public static String aesEncrypt(String plaintext, String keyBase64) throws GeneralSecurityException {
        if (plaintext == null || keyBase64 == null) {
            return null;
        }
        
        // 解码密钥
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        SecretKey key = new SecretKeySpec(keyBytes, AES_ALGORITHM);
        
        // 生成初始化向量
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[IV_SIZE];
        random.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        
        // 加密
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        
        // 将IV和密文拼接后Base64编码
        byte[] combined = new byte[ivBytes.length + encryptedBytes.length];
        System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
        System.arraycopy(encryptedBytes, 0, combined, ivBytes.length, encryptedBytes.length);
        
        return Base64.getEncoder().encodeToString(combined);
    }
    
    /**
     * AES解密
     * 
     * @param ciphertextBase64 Base64编码的密文
     * @param keyBase64 Base64编码的密钥
     * @return 解密后的明文
     */
    public static String aesDecrypt(String ciphertextBase64, String keyBase64) throws GeneralSecurityException {
        if (ciphertextBase64 == null || keyBase64 == null) {
            return null;
        }
        
        // 解码密钥和密文
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        byte[] combined = Base64.getDecoder().decode(ciphertextBase64);
        
        if (combined.length < IV_SIZE) {
            throw new IllegalArgumentException("Invalid encrypted data");
        }
        
        // 提取IV和密文
        byte[] ivBytes = new byte[IV_SIZE];
        byte[] cipherBytes = new byte[combined.length - IV_SIZE];
        System.arraycopy(combined, 0, ivBytes, 0, IV_SIZE);
        System.arraycopy(combined, IV_SIZE, cipherBytes, 0, cipherBytes.length);
        
        // 解密
        SecretKey key = new SecretKeySpec(keyBytes, AES_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedBytes = cipher.doFinal(cipherBytes);
        
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    // 其他加密方法略...
}
```

### 4. platform-common-logging

日志相关模块提供日志处理功能：

- **日志切面**：自动记录方法调用日志
- **操作日志**：记录用户操作
- **审计日志**：支持安全审计
- **日志格式化**：标准化日志格式

#### 操作日志切面示例

```java
/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 操作类型
     */
    OperationType type();
    
    /**
     * 是否保存请求参数
     */
    boolean saveParams() default true;
    
    /**
     * 是否保存响应结果
     */
    boolean saveResult() default false;
}

/**
 * 操作类型枚举
 */
public enum OperationType {
    /**
     * 查询
     */
    QUERY("查询"),
    
    /**
     * 新增
     */
    CREATE("新增"),
    
    /**
     * 修改
     */
    UPDATE("修改"),
    
    /**
     * 删除
     */
    DELETE("删除"),
    
    /**
     * 导出
     */
    EXPORT("导出"),
    
    /**
     * 导入
     */
    IMPORT("导入"),
    
    /**
     * 其他
     */
    OTHER("其他");
    
    private final String text;
    
    OperationType(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
}

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {
    
    @Resource
    private OperationLogService operationLogService;
    
    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.platform.common.logging.annotation.OperationLog)")
    public void operationLogPointcut() {
    }
    
    /**
     * 环绕通知，记录操作日志
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取当前用户
        String userId = SecurityContext.getCurrentUserId();
        String username = SecurityContext.getCurrentUser() != null ? 
                          SecurityContext.getCurrentUser().getUsername() : null;
        
        // 获取请求信息
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();
        
        // 记录请求参数
        String params = operationLog.saveParams() ? parseParams(args) : null;
        
        // 执行目标方法
        Object result = null;
        Exception exception = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 记录响应结果
            String responseData = (operationLog.saveResult() && result != null) ? 
                                 JsonUtils.toJson(result) : null;
            
            // 构建操作日志
            OperationLogDTO logDTO = new OperationLogDTO();
            logDTO.setUserId(userId);
            logDTO.setUsername(username);
            logDTO.setType(operationLog.type().getText());
            logDTO.setDescription(operationLog.description());
            logDTO.setClassName(className);
            logDTO.setMethodName(methodName);
            logDTO.setParams(params);
            logDTO.setResult(responseData);
            logDTO.setDuration(duration);
            logDTO.setStatus(exception == null ? "成功" : "失败");
            logDTO.setErrorMessage(exception != null ? exception.getMessage() : null);
            logDTO.setOperationTime(new Date(startTime));
            logDTO.setIp(WebUtils.getClientIp());
            
            // 异步保存日志
            operationLogService.save(logDTO);
        }
    }
    
    /**
     * 解析请求参数
     */
    private String parseParams(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        
        try {
            // 过滤敏感参数
            Object[] filteredArgs = Arrays.stream(args)
                .map(arg -> {
                    if (arg instanceof MultipartFile || arg instanceof HttpServletRequest || 
                        arg instanceof HttpServletResponse) {
                        return "[不支持的类型]";
                    }
                    return arg;
                })
                .toArray();
            
            return JsonUtils.toJson(filteredArgs);
        } catch (Exception e) {
            log.warn("解析请求参数失败", e);
            return "[解析失败]";
        }
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
    
    <artifactId>platform-common</artifactId>
    <packaging>pom</packaging>
    <n>Platform Common</n>
    <description>Common tools and components for Enterprise Data Platform</description>
    
    <modules>
        <module>platform-common-core</module>
        <module>platform-common-web</module>
        <module>platform-common-security</module>
        <module>platform-common-logging</module>
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

### platform-common-core 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.platform</groupId>
        <artifactId>platform-common</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>platform-common-core</artifactId>
    <packaging>jar</packaging>
    <n>Platform Common Core</n>
    <description>Core utilities and components for Enterprise Data Platform</description>
    
    <dependencies>
        <!-- 基础工具包 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-collections4</artifactId>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>
        
        <!-- JSON处理 -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
        
        <!-- 日志 -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
        
        <!-- 校验 -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 设计原则与最佳实践

1. **单一职责原则**：每个子模块和类都应当只有一个职责
2. **封装与抽象**：隐藏实现细节，只暴露必要的接口
3. **组合优于继承**：优先使用组合而非继承来复用代码
4. **依赖注入**：通过DI降低组件间耦合
5. **不可变性**：尽可能使用不可变对象，特别是领域对象
6. **异常处理**：统一的异常体系和处理策略

## 代码规范

1. **命名规范**：
   - 包名：使用小写字母，如`com.platform.common.util`
   - 类名：使用大驼峰命名法，如`StringUtils`
   - 方法名：使用小驼峰命名法，如`parseParams`
   - 常量：全大写，下划线分隔，如`MAX_SIZE`

2. **代码风格**：
   - 缩进：使用4个空格
   - 行宽：不超过120个字符
   - 括号：左括号不换行，右括号单独一行
   - 注释：方法上方使用JavaDoc注释，关键代码使用行注释

3. **异常处理**：
   - 不捕获通用异常（Exception、RuntimeException）
   - 异常必须有明确的处理策略，不允许空catch块
   - 记录异常日志时包含上下文信息

4. **资源管理**：
   - 使用try-with-resources自动关闭资源
   - 避免在循环中频繁创建对象
   - 使用StringBuilder而非+拼接字符串

## 测试策略

1. **单元测试**：
   - 核心工具类方法的单元测试
   - 使用模拟对象测试组件交互
   - 边界条件和异常情况测试

2. **集成测试**：
   - 验证模块间交互
   - 测试与外部系统的集成

3. **测试覆盖率目标**：
   - 行覆盖率：>80%
   - 分支覆盖率：>70%
   - 关键核心组件：>90%
