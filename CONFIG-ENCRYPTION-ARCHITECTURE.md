# 配置加密架构设计

## 总体设计

### 设计原则
- **标签化加密**: 使用ENCODE()标签标记需要加密的配置
- **多算法支持**: 支持不同的加密算法（AES256、BASE64等）
- **扩展性**: 可以轻松添加新的加密方式
- **公共配置**: 通过切面统一处理配置解密
- **环境隔离**: 不同环境使用不同的加密密钥

## 加密标签格式

### 基础格式
```yaml
# 基础加密格式
database:
  password: ${ENCODE(AES256:encrypted_password_here)}
  
# 支持的加密类型
api:
  secret: ${ENCODE(AES256:api_secret)}           # AES256加密
  token: ${ENCODE(BASE64:base64_token)}          # Base64编码
  key: ${ENCODE(RSA:rsa_encrypted_key)}          # RSA加密
```

### 扩展格式
```yaml
# 支持自定义加密算法
custom:
  value: ${ENCODE(CUSTOM_ALG:encrypted_value)}
  
# 支持加密参数
secure:
  data: ${ENCODE(AES256:encrypted_data:key_id=prod)}
  info: ${ENCODE(RSA:encrypted_info:key_size=2048)}
```

## 配置加密实现

### 1. 加密接口定义
```java
// 加密器接口
public interface ConfigEncryptor {
    /**
     * 加密配置值
     * @param plainText 明文
     * @param params 加密参数
     * @return 密文
     */
    String encrypt(String plainText, Map<String, String> params);
    
    /**
     * 解密配置值
     * @param encryptedText 密文
     * @param params 解密参数
     * @return 明文
     */
    String decrypt(String encryptedText, Map<String, String> params);
    
    /**
     * 获取加密算法名称
     * @return 算法名称
     */
    String getAlgorithmName();
}
```

### 2. AES256加密实现
```java
@Component
public class AES256Encryptor implements ConfigEncryptor {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    
    @Value("${config.encryption.aes.key:}")
    private String encryptionKey;
    
    @Override
    public String encrypt(String plainText, Map<String, String> params) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                getEncryptionKey(params).getBytes(), ALGORITHM);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
            
        } catch (Exception e) {
            throw new ConfigEncryptionException("AES256加密失败", e);
        }
    }
    
    @Override
    public String decrypt(String encryptedText, Map<String, String> params) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                getEncryptionKey(params).getBytes(), ALGORITHM);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            
            byte[] decrypted = cipher.doFinal(
                Base64.getDecoder().decode(encryptedText));
            return new String(decrypted);
            
        } catch (Exception e) {
            throw new ConfigEncryptionException("AES256解密失败", e);
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "AES256";
    }
    
    private String getEncryptionKey(Map<String, String> params) {
        String keyId = params.get("key_id");
        if (keyId != null) {
            return getKeyById(keyId);
        }
        return encryptionKey;
    }
}
```

### 3. 配置解析器
```java
@Component
public class EncryptedConfigResolver {
    
    private final Map<String, ConfigEncryptor> encryptors = new HashMap<>();
    private final Pattern ENCODE_PATTERN = Pattern.compile(
        "\\$\\{ENCODE\\(([^:]+):([^:}]+)(?::([^}]+))?\\)\\}");
    
    @Autowired
    public EncryptedConfigResolver(List<ConfigEncryptor> encryptorList) {
        encryptorList.forEach(encryptor -> 
            encryptors.put(encryptor.getAlgorithmName(), encryptor));
    }
    
    /**
     * 解析配置值
     * @param configValue 配置值
     * @return 解析后的值
     */
    public String resolveConfigValue(String configValue) {
        if (configValue == null || !configValue.contains("${ENCODE(")) {
            return configValue;
        }
        
        Matcher matcher = ENCODE_PATTERN.matcher(configValue);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String algorithm = matcher.group(1);
            String encryptedValue = matcher.group(2);
            String paramsStr = matcher.group(3);
            
            Map<String, String> params = parseParams(paramsStr);
            
            ConfigEncryptor encryptor = encryptors.get(algorithm);
            if (encryptor == null) {
                throw new ConfigEncryptionException(
                    "不支持的加密算法: " + algorithm);
            }
            
            String decryptedValue = encryptor.decrypt(encryptedValue, params);
            matcher.appendReplacement(result, decryptedValue);
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
    
    private Map<String, String> parseParams(String paramsStr) {
        Map<String, String> params = new HashMap<>();
        if (paramsStr != null && !paramsStr.isEmpty()) {
            String[] pairs = paramsStr.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    params.put(kv[0].trim(), kv[1].trim());
                }
            }
        }
        return params;
    }
}
```

### 4. 配置切面处理
```java
@Aspect
@Component
@Slf4j
public class ConfigDecryptionAspect {
    
    @Autowired
    private EncryptedConfigResolver configResolver;
    
    /**
     * 拦截配置注入
     */
    @Around("@annotation(org.springframework.beans.factory.annotation.Value)")
    public Object decryptConfigValue(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        
        if (result instanceof String) {
            String configValue = (String) result;
            if (configValue.contains("${ENCODE(")) {
                String decryptedValue = configResolver.resolveConfigValue(configValue);
                log.debug("配置解密成功: {} -> [已解密]", configValue);
                return decryptedValue;
            }
        }
        
        return result;
    }
    
    /**
     * 拦截ConfigurationProperties
     */
    @Around("@within(org.springframework.boot.context.properties.ConfigurationProperties)")
    public Object decryptConfigurationProperties(ProceedingJoinPoint joinPoint) throws Throwable {
        Object configObject = joinPoint.proceed();
        
        // 递归处理配置对象的字段
        processConfigObject(configObject);
        
        return configObject;
    }
    
    private void processConfigObject(Object configObject) {
        if (configObject == null) {
            return;
        }
        
        Class<?> clazz = configObject.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(configObject);
                
                if (value instanceof String) {
                    String stringValue = (String) value;
                    if (stringValue.contains("${ENCODE(")) {
                        String decryptedValue = configResolver.resolveConfigValue(stringValue);
                        field.set(configObject, decryptedValue);
                    }
                } else if (value != null && !isPrimitiveOrWrapper(value.getClass())) {
                    // 递归处理嵌套对象
                    processConfigObject(value);
                }
                
            } catch (IllegalAccessException e) {
                log.error("配置对象字段访问失败: {}", field.getName(), e);
            }
        }
    }
    
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == String.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Double.class ||
               clazz == Boolean.class;
    }
}
```

### 5. 配置加密工具
```java
@Component
public class ConfigEncryptionTool {
    
    @Autowired
    private List<ConfigEncryptor> encryptors;
    
    /**
     * 加密配置值
     * @param algorithm 加密算法
     * @param plainText 明文
     * @param params 参数
     * @return 加密后的配置格式
     */
    public String encryptConfig(String algorithm, String plainText, Map<String, String> params) {
        ConfigEncryptor encryptor = findEncryptor(algorithm);
        if (encryptor == null) {
            throw new ConfigEncryptionException("不支持的加密算法: " + algorithm);
        }
        
        String encryptedValue = encryptor.encrypt(plainText, params);
        
        StringBuilder result = new StringBuilder();
        result.append("${ENCODE(").append(algorithm).append(":").append(encryptedValue);
        
        if (params != null && !params.isEmpty()) {
            result.append(":");
            params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .forEach(param -> result.append(param).append(","));
            // 移除最后的逗号
            result.setLength(result.length() - 1);
        }
        
        result.append(")}");
        return result.toString();
    }
    
    private ConfigEncryptor findEncryptor(String algorithm) {
        return encryptors.stream()
            .filter(enc -> enc.getAlgorithmName().equals(algorithm))
            .findFirst()
            .orElse(null);
    }
}
```

## 使用示例

### 1. 配置文件示例
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/platform
    username: ${ENCODE(AES256:encrypted_username)}
    password: ${ENCODE(AES256:encrypted_password)}
    
  redis:
    host: localhost
    port: 6379
    password: ${ENCODE(AES256:encrypted_redis_password)}
    
# 外部API配置
external:
  api:
    secret-key: ${ENCODE(RSA:encrypted_secret_key:key_size=2048)}
    access-token: ${ENCODE(BASE64:base64_encoded_token)}
    
# 微信配置
wechat:
  app-id: wx1234567890
  app-secret: ${ENCODE(AES256:encrypted_app_secret:key_id=wechat)}
  
# 邮件配置
mail:
  host: smtp.qq.com
  username: ${ENCODE(BASE64:encoded_email)}
  password: ${ENCODE(AES256:encrypted_email_password)}
```

### 2. 配置类示例
```java
@ConfigurationProperties(prefix = "external.api")
@Data
public class ExternalApiConfig {
    private String secretKey;
    private String accessToken;
    private Integer timeout = 30000;
}
```

### 3. 加密工具使用
```java
@RestController
@RequestMapping("/admin/config")
public class ConfigEncryptionController {
    
    @Autowired
    private ConfigEncryptionTool encryptionTool;
    
    @PostMapping("/encrypt")
    public ResponseEntity<String> encryptConfig(@RequestBody EncryptRequest request) {
        String encryptedConfig = encryptionTool.encryptConfig(
            request.getAlgorithm(),
            request.getPlainText(),
            request.getParams()
        );
        
        return ResponseEntity.ok(encryptedConfig);
    }
}
```

## 密钥管理

### 1. 环境变量密钥
```bash
# 开发环境
export CONFIG_ENCRYPTION_KEY="dev_encryption_key_32_chars"

# 测试环境
export CONFIG_ENCRYPTION_KEY="test_encryption_key_32_chars"

# 生产环境
export CONFIG_ENCRYPTION_KEY="prod_encryption_key_32_chars"
```

### 2. 密钥轮转支持
```java
@Component
public class KeyRotationManager {
    
    private final Map<String, String> keyRegistry = new HashMap<>();
    
    @PostConstruct
    public void initKeys() {
        // 从安全存储加载密钥
        loadKeysFromSecureStorage();
    }
    
    public String getKeyById(String keyId) {
        return keyRegistry.get(keyId);
    }
    
    public void rotateKey(String keyId, String newKey) {
        keyRegistry.put(keyId, newKey);
        // 保存到安全存储
        saveKeyToSecureStorage(keyId, newKey);
    }
}
```

这个配置加密架构提供了：

1. **灵活的标签系统**: 支持多种加密算法和参数
2. **自动解密**: 通过切面自动处理配置解密
3. **扩展性**: 易于添加新的加密算法
4. **安全性**: 支持密钥轮转和环境隔离
5. **易用性**: 简单的配置格式和工具支持 