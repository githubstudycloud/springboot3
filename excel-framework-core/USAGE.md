# Excel Framework 使用说明

## 架构概述

Excel Framework 已经重构为基于接口+抽象类的设计模式，不再依赖数据库配置。新架构包含：

1. **`ExcelService` 接口** - 定义Excel服务的标准契约
2. **`AbstractExcelService` 抽象类** - 提供通用的Excel处理流程
3. **业务实现类** - 继承抽象类，实现具体的业务逻辑

## 核心设计理念

- **不依赖数据库配置**：所有配置通过业务代码提供
- **入参转换由业务实现**：每个业务可以自定义参数处理逻辑
- **数据获取由业务实现**：支持从任何数据源获取数据
- **配置由业务实现**：模板配置通过常量或方法提供
- **保存逻辑由业务实现**：支持自定义保存逻辑和默认值生成

## 接口说明

### ExcelService 接口

```java
public interface ExcelService {
    byte[] generateTemplate(String templateKey, BusinessConditions conditions);
    ImportResult importData(String templateKey, MultipartFile file, BusinessConditions conditions);
    byte[] exportData(String templateKey, BusinessConditions conditions);
}
```

### 抽象方法说明

继承 `AbstractExcelService` 需要实现以下抽象方法：

#### 1. 参数转换方法
```java
protected abstract BusinessConditions transformAndValidateParameters(
    String templateKey, 
    BusinessConditions initialConditions
);
```
- **用途**：转换和校验业务入参
- **实现建议**：进行参数校验、设置默认值、权限检查等

#### 2. 数据获取方法
```java
protected abstract List<Map<String, Object>> fetchBusinessData(
    ExcelTemplateConfig config, 
    BusinessConditions conditions
);
```
- **用途**：获取导出的业务数据
- **实现建议**：可以从数据库、API、文件等任何数据源获取

#### 3. 配置获取方法
```java
protected abstract ExcelTemplateConfig getExportConfiguration(String templateKey, BusinessConditions conditions);
protected abstract ExcelTemplateConfig getImportConfiguration(String templateKey, BusinessConditions conditions);
protected abstract ExcelTemplateConfig getTemplateGenerationConfiguration(String templateKey, BusinessConditions conditions);
```
- **用途**：提供不同场景下的模板配置
- **实现建议**：通过常量、配置文件或动态构建配置

#### 4. 数据保存方法
```java
protected abstract ImportResult saveBusinessData(
    List<Map<String, Object>> dataList, 
    ExcelTemplateConfig config, 
    BusinessConditions conditions
);
```
- **用途**：保存导入的业务数据
- **实现建议**：处理事务、生成默认字段值、数据校验等

#### 5. 动态Mapper获取方法
```java
protected abstract DynamicMapper getDynamicMapper();
```
- **用途**：提供下拉框数据查询支持
- **实现建议**：如不需要下拉框功能返回null，否则返回相应的Mapper

## 实现示例

### 1. 创建业务Excel服务

```java
@Service("userExcelService")
public class UserExcelService extends AbstractExcelService {
    
    @Autowired
    private UserMapper userMapper;
    
    // 模板配置常量
    private static final String USER_TEMPLATE_KEY = "USER_TEMPLATE";
    private static final ExcelTemplateConfig USER_CONFIG = createUserConfig();
    
    @Override
    protected BusinessConditions transformAndValidateParameters(String templateKey, BusinessConditions conditions) {
        // 参数校验和转换
        if (conditions == null) {
            conditions = new BusinessConditions();
        }
        // 添加权限校验、默认参数等
        return conditions;
    }
    
    @Override
    protected List<Map<String, Object>> fetchBusinessData(ExcelTemplateConfig config, BusinessConditions conditions) {
        // 从数据库获取用户数据
        return userMapper.selectUserList(conditions.getConditions());
    }
    
    @Override
    protected ExcelTemplateConfig getExportConfiguration(String templateKey, BusinessConditions conditions) {
        return USER_CONFIG;
    }
    
    @Override
    protected ImportResult saveBusinessData(List<Map<String, Object>> dataList, ExcelTemplateConfig config, BusinessConditions conditions) {
        ImportResult result = new ImportResult();
        
        for (Map<String, Object> userData : dataList) {
            // 生成默认字段值
            userData.put("id", generateId());
            userData.put("createTime", LocalDateTime.now());
            userData.put("createBy", getCurrentUserId());
            userData.put("status", 1);
            
            // 保存数据
            userMapper.insertUser(userData);
        }
        
        result.setSuccessCount(dataList.size());
        return result;
    }
    
    // 其他抽象方法实现...
}
```

### 2. 配置模板常量

```java
private static ExcelTemplateConfig createUserConfig() {
    ExcelTemplateConfig config = new ExcelTemplateConfig();
    config.setTemplateKey("USER_TEMPLATE");
    config.setTemplateName("用户信息模板");
    config.setSheetName("用户信息");
    
    // 配置字段
    List<ExcelFieldConfig> fields = new ArrayList<>();
    
    ExcelFieldConfig usernameField = new ExcelFieldConfig();
    usernameField.setFieldName("username");
    usernameField.setFieldLabel("用户名");
    usernameField.setFieldType("STRING");
    usernameField.setColumnIndex(0);
    usernameField.setRequired(true);
    fields.add(usernameField);
    
    config.setFields(fields);
    return config;
}
```

### 3. Controller使用

```java
@RestController
public class UserController {
    
    @Autowired
    @Qualifier("userExcelService")
    private ExcelService excelService;
    
    @PostMapping("/users/export")
    public void exportUsers(@RequestBody BusinessConditions conditions, HttpServletResponse response) {
        byte[] excelData = excelService.exportData("USER_TEMPLATE", conditions);
        // 设置响应头并输出文件
    }
    
    @PostMapping("/users/import")
    public ImportResult importUsers(@RequestParam("file") MultipartFile file) {
        return excelService.importData("USER_TEMPLATE", file, null);
    }
}
```

## 最佳实践

### 1. 模板配置管理
- 使用静态常量定义模板配置
- 考虑使用配置文件或配置类集中管理
- 支持多环境配置差异

### 2. 默认值生成
在 `saveBusinessData` 方法中生成业务默认值：
```java
userData.put("id", idGenerator.nextId());
userData.put("createTime", LocalDateTime.now());
userData.put("createBy", SecurityUtils.getCurrentUserId());
userData.put("tenantId", SecurityUtils.getCurrentTenantId());
userData.put("status", StatusEnum.ACTIVE.getCode());
```

### 3. 错误处理
```java
@Override
protected ImportResult saveBusinessData(List<Map<String, Object>> dataList, ExcelTemplateConfig config, BusinessConditions conditions) {
    ImportResult result = new ImportResult();
    
    for (int i = 0; i < dataList.size(); i++) {
        try {
            // 保存逻辑
        } catch (Exception e) {
            ImportResult.ErrorInfo error = new ImportResult.ErrorInfo(i + 1, e.getMessage());
            result.getErrors().add(error);
        }
    }
    
    return result;
}
```

### 4. 权限控制
在 `transformAndValidateParameters` 中进行权限校验：
```java
@Override
protected BusinessConditions transformAndValidateParameters(String templateKey, BusinessConditions conditions) {
    // 权限校验
    if (!hasPermission(templateKey)) {
        throw new SecurityException("无权限访问该模板");
    }
    
    // 数据权限过滤
    conditions = addDataPermissionFilter(conditions);
    
    return conditions;
}
```

## 迁移指南

从旧版本迁移到新架构：

1. **创建业务Excel服务类**：继承 `AbstractExcelService`
2. **实现抽象方法**：根据业务需求实现各个抽象方法
3. **定义模板配置**：将数据库中的配置转换为代码常量
4. **调整Controller注入**：注入具体的业务Excel服务
5. **移除数据库依赖**：删除对通用Excel配置表的依赖

## 注意事项

1. **Spring Bean注册**：业务实现类需要添加 `@Service` 注解
2. **Bean命名**：如果有多个实现类，建议使用 `@Qualifier` 指定具体实现
3. **配置初始化**：静态配置常量在类加载时初始化，注意性能影响
4. **线程安全**：抽象类中的工具类实例是线程安全的
5. **资源管理**：在处理大数据量时注意内存和性能优化 