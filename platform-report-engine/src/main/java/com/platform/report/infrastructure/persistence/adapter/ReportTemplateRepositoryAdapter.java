package com.platform.report.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.report.domain.model.template.*;
import com.platform.report.domain.repository.ReportTemplateRepository;
import com.platform.report.infrastructure.persistence.entity.ReportTemplateEntity;
import com.platform.report.infrastructure.persistence.entity.TemplateComponentEntity;
import com.platform.report.infrastructure.persistence.repository.ReportTemplateJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 报表模板仓储适配器
 * 实现领域仓储接口，转换领域模型和持久化实体
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ReportTemplateRepositoryAdapter implements ReportTemplateRepository {
    
    private final ReportTemplateJpaRepository reportTemplateJpaRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public ReportTemplate save(ReportTemplate template) {
        ReportTemplateEntity entity = toEntity(template);
        entity = reportTemplateJpaRepository.save(entity);
        return toDomain(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ReportTemplate> findById(String id) {
        return reportTemplateJpaRepository.findById(id)
                .map(this::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportTemplate> findAll() {
        return reportTemplateJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportTemplate> findByStatus(TemplateStatus status) {
        ReportTemplateEntity.TemplateStatusEnum statusEnum = 
                ReportTemplateEntity.TemplateStatusEnum.valueOf(status.name());
        return reportTemplateJpaRepository.findByStatus(statusEnum).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportTemplate> findByCreatedBy(String createdBy) {
        return reportTemplateJpaRepository.findByCreatedBy(createdBy).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportTemplate> findByDataSourceId(String dataSourceId) {
        return reportTemplateJpaRepository.findByDataSourceId(dataSourceId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportTemplate> findByDataSetId(String dataSetId) {
        return reportTemplateJpaRepository.findByDataSetId(dataSetId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReportTemplate> findByNameLike(String nameLike) {
        return reportTemplateJpaRepository.findByNameContainingIgnoreCase(nameLike).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void delete(String id) {
        reportTemplateJpaRepository.deleteById(id);
    }
    
    /**
     * 将领域模型转换为实体
     */
    private ReportTemplateEntity toEntity(ReportTemplate template) {
        ReportTemplateEntity entity = new ReportTemplateEntity();
        entity.setId(template.getId());
        entity.setName(template.getName());
        entity.setDescription(template.getDescription());
        entity.setCreatedBy(template.getCreatedBy());
        entity.setCreatedAt(template.getCreatedAt());
        entity.setUpdatedAt(template.getUpdatedAt());
        entity.setTemplateType(ReportTemplateEntity.TemplateTypeEnum.valueOf(template.getTemplateType().name()));
        entity.setStatus(ReportTemplateEntity.TemplateStatusEnum.valueOf(template.getStatus().name()));
        entity.setDataSourceId(template.getDataSourceId());
        entity.setDataSetId(template.getDataSetId());
        
        // 序列化布局
        try {
            if (template.getLayout() != null) {
                entity.setLayoutJson(objectMapper.writeValueAsString(template.getLayout()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize template layout", e);
            throw new RuntimeException("Failed to serialize template layout", e);
        }
        
        // 序列化样式
        try {
            if (template.getStyle() != null) {
                entity.setStyleJson(objectMapper.writeValueAsString(template.getStyle()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize template style", e);
            throw new RuntimeException("Failed to serialize template style", e);
        }
        
        // 转换组件
        List<TemplateComponentEntity> componentEntities = new ArrayList<>();
        if (template.getComponents() != null) {
            for (TemplateComponent component : template.getComponents()) {
                componentEntities.add(toComponentEntity(component, entity));
            }
        }
        entity.setComponents(componentEntities);
        
        return entity;
    }
    
    /**
     * 将组件领域模型转换为实体
     */
    private TemplateComponentEntity toComponentEntity(TemplateComponent component, ReportTemplateEntity templateEntity) {
        TemplateComponentEntity entity = new TemplateComponentEntity();
        entity.setId(component.getId());
        entity.setName(component.getName());
        entity.setType(TemplateComponentEntity.ComponentTypeEnum.valueOf(component.getType().name()));
        entity.setPositionX(component.getPosition().getX());
        entity.setPositionY(component.getPosition().getY());
        entity.setWidth(component.getSize().getWidth());
        entity.setHeight(component.getSize().getHeight());
        entity.setDataBinding(component.getDataBinding());
        entity.setTemplate(templateEntity);
        
        // 序列化属性
        try {
            if (component.getProperties() != null) {
                entity.setPropertiesJson(objectMapper.writeValueAsString(component.getProperties()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize component properties", e);
            throw new RuntimeException("Failed to serialize component properties", e);
        }
        
        return entity;
    }
    
    /**
     * 将实体转换为领域模型
     */
    private ReportTemplate toDomain(ReportTemplateEntity entity) {
        // 解析布局
        TemplateLayout layout = null;
        if (entity.getLayoutJson() != null && !entity.getLayoutJson().isEmpty()) {
            try {
                layout = objectMapper.readValue(entity.getLayoutJson(), TemplateLayout.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize template layout", e);
                throw new RuntimeException("Failed to deserialize template layout", e);
            }
        }
        
        // 解析样式
        TemplateStyle style = null;
        if (entity.getStyleJson() != null && !entity.getStyleJson().isEmpty()) {
            try {
                style = objectMapper.readValue(entity.getStyleJson(), TemplateStyle.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize template style", e);
                throw new RuntimeException("Failed to deserialize template style", e);
            }
        }
        
        // 创建领域模型
        ReportTemplate template = ReportTemplate.create(
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedBy(),
                TemplateType.valueOf(entity.getTemplateType().name()),
                layout,
                style,
                entity.getDataSourceId(),
                entity.getDataSetId()
        );
        
        // 设置ID和时间戳（通过反射或setter，这里假设有setter方法）
        // 在实际实现中，可能需要在领域模型中添加适当的方法来设置这些值
        // 这里仅为示例
        
        // 转换组件
        if (entity.getComponents() != null) {
            for (TemplateComponentEntity componentEntity : entity.getComponents()) {
                template.addComponent(toDomainComponent(componentEntity));
            }
        }
        
        return template;
    }
    
    /**
     * 将组件实体转换为领域模型
     */
    private TemplateComponent toDomainComponent(TemplateComponentEntity entity) {
        // 解析属性
        Map<String, Object> properties = null;
        if (entity.getPropertiesJson() != null && !entity.getPropertiesJson().isEmpty()) {
            try {
                // 将JSON字符串解析为Map
                properties = objectMapper.readValue(
                        entity.getPropertiesJson(),
                        objectMapper.getTypeFactory().constructMapType(
                                Map.class, String.class, Object.class));
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize component properties", e);
                throw new RuntimeException("Failed to deserialize component properties", e);
            }
        }
        
        TemplateComponent.Position position = new TemplateComponent.Position(
                entity.getPositionX(), entity.getPositionY());
        
        TemplateComponent.Size size = new TemplateComponent.Size(
                entity.getWidth(), entity.getHeight());
        
        return TemplateComponent.create(
                entity.getName(),
                TemplateComponent.ComponentType.valueOf(entity.getType().name()),
                position,
                size,
                properties,
                entity.getDataBinding()
        );
    }
}
