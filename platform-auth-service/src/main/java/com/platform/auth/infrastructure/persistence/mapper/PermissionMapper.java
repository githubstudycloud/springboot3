package com.platform.auth.infrastructure.persistence.mapper;

import com.platform.auth.domain.model.Permission;
import com.platform.auth.infrastructure.persistence.entity.PermissionEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

/**
 * 权限实体映射器
 * <p>
 * 负责领域模型与JPA实体之间的转换
 * </p>
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {
    
    /**
     * 将权限JPA实体转换为领域模型
     *
     * @param entity JPA实体
     * @return 领域模型
     */
    Permission toDomain(PermissionEntity entity);
    
    /**
     * 将领域模型转换为权限JPA实体
     *
     * @param domain 领域模型
     * @return JPA实体
     */
    PermissionEntity toEntity(Permission domain);
    
    /**
     * 将JPA实体集合转换为领域模型集合
     *
     * @param entities JPA实体集合
     * @return 领域模型集合
     */
    Set<Permission> toDomainSet(Set<PermissionEntity> entities);
    
    /**
     * 将领域模型集合转换为JPA实体集合
     *
     * @param domains 领域模型集合
     * @return JPA实体集合
     */
    Set<PermissionEntity> toEntitySet(Set<Permission> domains);
    
    /**
     * 将JPA实体列表转换为领域模型列表
     *
     * @param entities JPA实体列表
     * @return 领域模型列表
     */
    List<Permission> toDomainList(List<PermissionEntity> entities);
    
    /**
     * 将领域模型列表转换为JPA实体列表
     *
     * @param domains 领域模型列表
     * @return JPA实体列表
     */
    List<PermissionEntity> toEntityList(List<Permission> domains);
    
    /**
     * 更新JPA实体
     *
     * @param domain 领域模型（源）
     * @param entity JPA实体（目标）
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    void updateEntityFromDomain(Permission domain, @MappingTarget PermissionEntity entity);
}
