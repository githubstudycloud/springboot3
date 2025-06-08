package com.platform.domain.config.template.entity;

import com.platform.domain.config.template.event.ConfigTemplateEvent;
import com.platform.domain.config.template.valueobject.TemplateId;
import com.platform.domain.config.template.valueobject.TemplateStatus;
import com.platform.domain.config.template.valueobject.TemplateType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 配置模板聚合根
 * DDD领域模型 - 封装配置模板业务逻辑和不变性约束
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "platform_config_template")
public class ConfigTemplate extends AbstractAggregateRoot<ConfigTemplate> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private TemplateId templateId;
    
    @NotBlank(message = "模板名称不能为空")
    @Size(min = 2, max = 100, message = "模板名称长度必须在2-100个字符之间")
    @Column(unique = true, nullable = false)
    private String name;
    
    @Size(max = 500, message = "模板描述长度不能超过500个字符")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateStatus status;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String templateContent;
    
    @ElementCollection
    @CollectionTable(name = "config_template_variables", 
                    joinColumns = @JoinColumn(name = "template_id"))
    @MapKeyColumn(name = "variable_name")
    @Column(name = "variable_description")
    private Map<String, String> variables = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "config_template_default_values", 
                    joinColumns = @JoinColumn(name = "template_id"))
    @MapKeyColumn(name = "variable_name")
    @Column(name = "default_value")
    private Map<String, String> defaultValues = new HashMap<>();
    
    @Size(max = 100, message = "分类长度不能超过100个字符")
    private String category;
    
    @Size(max = 50, message = "版本号长度不能超过50个字符")
    private String version;
    
    @Column(nullable = false)
    private String createdBy;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private String updatedBy;
    
    private LocalDateTime updatedAt;
    
    @Version
    private Long versionNumber;
    
    // JPA required
    protected ConfigTemplate() {}
    
    /**
     * 创建新配置模板
     */
    public ConfigTemplate(String name, String description, TemplateType type, 
                         String templateContent, String createdBy) {
        this.templateId = TemplateId.generate();
        this.name = name;
        this.description = description;
        this.type = type;
        this.templateContent = templateContent;
        this.status = TemplateStatus.DRAFT;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = "1.0.0";
        
        // 发布领域事件
        registerEvent(ConfigTemplateEvent.templateCreated(this.templateId, name, type, createdBy));
    }
    
    /**
     * 更新模板内容
     */
    public void updateContent(String templateContent, String updatedBy) {
        if (!this.status.canModify()) {
            throw new IllegalStateException("模板状态不允许修改: " + this.status);
        }
        
        this.templateContent = templateContent;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(ConfigTemplateEvent.templateContentUpdated(this.templateId, this.name, updatedBy));
    }
    
    /**
     * 添加模板变量
     */
    public void addVariable(String variableName, String description, String defaultValue) {
        if (!this.status.canModify()) {
            throw new IllegalStateException("模板状态不允许修改: " + this.status);
        }
        
        this.variables.put(variableName, description);
        if (defaultValue != null) {
            this.defaultValues.put(variableName, defaultValue);
        }
        
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(ConfigTemplateEvent.templateVariableAdded(this.templateId, this.name, variableName));
    }
    
    /**
     * 移除模板变量
     */
    public void removeVariable(String variableName) {
        if (!this.status.canModify()) {
            throw new IllegalStateException("模板状态不允许修改: " + this.status);
        }
        
        this.variables.remove(variableName);
        this.defaultValues.remove(variableName);
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(ConfigTemplateEvent.templateVariableRemoved(this.templateId, this.name, variableName));
    }
    
    /**
     * 发布模板
     */
    public void publish(String publishedBy) {
        if (this.status != TemplateStatus.DRAFT) {
            throw new IllegalStateException("只有草稿状态的模板才能发布");
        }
        
        validateTemplateContent();
        
        this.status = TemplateStatus.PUBLISHED;
        this.updatedBy = publishedBy;
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(ConfigTemplateEvent.templatePublished(this.templateId, this.name, publishedBy));
    }
    
    /**
     * 停用模板
     */
    public void deactivate(String deactivatedBy) {
        if (this.status != TemplateStatus.PUBLISHED) {
            throw new IllegalStateException("只有已发布的模板才能停用");
        }
        
        this.status = TemplateStatus.INACTIVE;
        this.updatedBy = deactivatedBy;
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(ConfigTemplateEvent.templateDeactivated(this.templateId, this.name, deactivatedBy));
    }
    
    /**
     * 重新激活模板
     */
    public void reactivate(String reactivatedBy) {
        if (this.status != TemplateStatus.INACTIVE) {
            throw new IllegalStateException("只有停用的模板才能重新激活");
        }
        
        this.status = TemplateStatus.PUBLISHED;
        this.updatedBy = reactivatedBy;
        this.updatedAt = LocalDateTime.now();
        
        registerEvent(ConfigTemplateEvent.templateReactivated(this.templateId, this.name, reactivatedBy));
    }
    
    /**
     * 创建新版本
     */
    public ConfigTemplate createNewVersion(String newVersion, String createdBy) {
        if (this.status != TemplateStatus.PUBLISHED) {
            throw new IllegalStateException("只有已发布的模板才能创建新版本");
        }
        
        ConfigTemplate newTemplate = new ConfigTemplate(
            this.name, 
            this.description, 
            this.type, 
            this.templateContent, 
            createdBy
        );
        
        newTemplate.version = newVersion;
        newTemplate.category = this.category;
        newTemplate.variables = new HashMap<>(this.variables);
        newTemplate.defaultValues = new HashMap<>(this.defaultValues);
        
        registerEvent(ConfigTemplateEvent.templateVersionCreated(this.templateId, this.name, newVersion, createdBy));
        
        return newTemplate;
    }
    
    /**
     * 生成配置内容
     */
    public String generateConfig(Map<String, String> variableValues) {
        if (this.status != TemplateStatus.PUBLISHED) {
            throw new IllegalStateException("只有已发布的模板才能生成配置");
        }
        
        String result = this.templateContent;
        
        // 使用提供的变量值或默认值替换模板变量
        for (String variableName : this.variables.keySet()) {
            String value = variableValues.getOrDefault(variableName, 
                          this.defaultValues.get(variableName));
            
            if (value != null) {
                result = result.replace("${" + variableName + "}", value);
            }
        }
        
        return result;
    }
    
    /**
     * 验证模板内容
     */
    private void validateTemplateContent() {
        if (this.templateContent == null || this.templateContent.trim().isEmpty()) {
            throw new IllegalArgumentException("模板内容不能为空");
        }
        
        // 检查模板变量是否都有定义
        // 这里可以添加更复杂的模板语法验证逻辑
    }
    
    /**
     * 检查模板是否可以使用
     */
    public boolean canBeUsed() {
        return this.status == TemplateStatus.PUBLISHED;
    }
    
    // Getters
    public Long getId() { return id; }
    public TemplateId getTemplateId() { return templateId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public TemplateType getType() { return type; }
    public TemplateStatus getStatus() { return status; }
    public String getTemplateContent() { return templateContent; }
    public Map<String, String> getVariables() { return new HashMap<>(variables); }
    public Map<String, String> getDefaultValues() { return new HashMap<>(defaultValues); }
    public String getCategory() { return category; }
    public String getVersion() { return version; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getUpdatedBy() { return updatedBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersionNumber() { return versionNumber; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigTemplate that = (ConfigTemplate) o;
        return Objects.equals(templateId, that.templateId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(templateId);
    }
    
    @Override
    public String toString() {
        return "ConfigTemplate{" +
                "templateId=" + templateId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", version='" + version + '\'' +
                '}';
    }
} 