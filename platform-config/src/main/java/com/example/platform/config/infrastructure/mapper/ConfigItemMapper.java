package com.example.platform.config.infrastructure.mapper;

import com.example.platform.config.domain.model.ConfigItem;
import com.example.platform.config.infrastructure.persistence.entity.ConfigItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 配置项对象映射器
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigItemMapper {

    /**
     * 领域模型转实体
     */
    @Mapping(target = "id", source = "id")
    ConfigItemEntity toEntity(ConfigItem model);

    /**
     * 实体转领域模型
     */
    @Mapping(target = "versionHistories", ignore = true)
    ConfigItem toDomain(ConfigItemEntity entity);

    /**
     * 实体列表转领域模型列表
     */
    List<ConfigItem> toDomainList(List<ConfigItemEntity> entities);

    /**
     * 更新实体
     */
    void updateEntity(@MappingTarget ConfigItemEntity entity, ConfigItem model);
}
