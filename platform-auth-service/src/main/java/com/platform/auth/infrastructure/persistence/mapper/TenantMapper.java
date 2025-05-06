package com.platform.auth.infrastructure.persistence.mapper;

import com.platform.auth.domain.model.Tenant;
import com.platform.auth.infrastructure.persistence.entity.TenantEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * 租户实体映射器
 * <p>
 * 负责领域模型与JPA实体之间的转换
 * </p>
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TenantMapper {
    
    /**
     * 将租户JPA实体转换为领域模型
     *
     * @param entity JPA实体
     * @return 领域模型
     */
    Tenant toDomain(TenantEntity entity);
    
    /**
     * 将领域模型转换为租户JPA实体
     *
     * @param domain 领域模型
     * @return JPA实体
     */
    TenantEntity toEntity(Tenant domain);
    
    /**
     * 将JPA实体列表转换为领域模型列表
     *
     * @param entities JPA实体列表
     * @return 领域模型列表
     */
    List<Tenant> toDomainList(List<TenantEntity> entities);
    
    /**
     * 将领域模型列表转换为JPA实体列表
     *
     * @param domains 领域模型列表
     * @return JPA实体列表
     */
    List<TenantEntity> toEntityList(List<Tenant> domains);
    
    /**
     * 更新JPA实体
     *
     * @param domain 领域模型（源）
     * @param entity JPA实体（目标）
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateEntityFromDomain(Tenant domain, @MappingTarget TenantEntity entity);
}
