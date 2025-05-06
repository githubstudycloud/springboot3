package com.example.platform.config.infrastructure.mapper;

import com.example.platform.config.domain.model.ConfigVersionHistory;
import com.example.platform.config.infrastructure.persistence.entity.ConfigVersionHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 配置版本历史对象映射器
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigVersionHistoryMapper {

    /**
     * 领域模型转实体
     */
    ConfigVersionHistoryEntity toEntity(ConfigVersionHistory model);

    /**
     * 实体转领域模型
     */
    ConfigVersionHistory toDomain(ConfigVersionHistoryEntity entity);

    /**
     * 实体列表转领域模型列表
     */
    List<ConfigVersionHistory> toDomainList(List<ConfigVersionHistoryEntity> entities);
}
