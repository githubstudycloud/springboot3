package com.example.platform.config.infrastructure.mapper;

import com.example.platform.config.domain.model.ConfigChangeEvent;
import com.example.platform.config.infrastructure.persistence.entity.ConfigChangeEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 配置变更事件对象映射器
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigChangeEventMapper {

    /**
     * 领域模型转实体
     */
    @Mapping(target = "eventType", source = "eventType")
    ConfigChangeEventEntity toEntity(ConfigChangeEvent model);

    /**
     * 实体转领域模型
     */
    @Mapping(target = "eventType", source = "eventType")
    ConfigChangeEvent toDomain(ConfigChangeEventEntity entity);

    /**
     * 实体列表转领域模型列表
     */
    List<ConfigChangeEvent> toDomainList(List<ConfigChangeEventEntity> entities);

    /**
     * 领域事件类型映射到实体事件类型
     */
    @ValueMapping(source = "CREATE", target = "CREATE")
    @ValueMapping(source = "UPDATE", target = "UPDATE")
    @ValueMapping(source = "DELETE", target = "DELETE")
    ConfigChangeEventEntity.EventTypeEnum toEntityEventType(ConfigChangeEvent.EventType eventType);

    /**
     * 实体事件类型映射到领域事件类型
     */
    @ValueMapping(source = "CREATE", target = "CREATE")
    @ValueMapping(source = "UPDATE", target = "UPDATE")
    @ValueMapping(source = "DELETE", target = "DELETE")
    ConfigChangeEvent.EventType toDomainEventType(ConfigChangeEventEntity.EventTypeEnum eventType);
}
