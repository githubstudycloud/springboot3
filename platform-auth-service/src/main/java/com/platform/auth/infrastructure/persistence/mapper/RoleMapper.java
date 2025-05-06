package com.platform.auth.infrastructure.persistence.mapper;

import com.platform.auth.domain.model.Role;
import com.platform.auth.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

/**
 * 角色实体映射器
 * <p>
 * 负责领域模型与JPA实体之间的转换
 * </p>
 */
@Mapper(componentModel = "spring", 
        uses = {PermissionMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    
    /**
     * 将角色JPA实体转换为领域模型
     *
     * @param entity JPA实体
     * @return 领域模型
     */
    @Mapping(target = "permissions", source = "permissions")
    Role toDomain(RoleEntity entity);
    
    /**
     * 将领域模型转换为角色JPA实体
     *
     * @param domain 领域模型
     * @return JPA实体
     */
    @Mapping(target = "permissions", source = "permissions")
    RoleEntity toEntity(Role domain);
    
    /**
     * 将JPA实体集合转换为领域模型集合
     *
     * @param entities JPA实体集合
     * @return 领域模型集合
     */
    Set<Role> toDomainSet(Set<RoleEntity> entities);
    
    /**
     * 将领域模型集合转换为JPA实体集合
     *
     * @param domains 领域模型集合
     * @return JPA实体集合
     */
    Set<RoleEntity> toEntitySet(Set<Role> domains);
    
    /**
     * 将JPA实体列表转换为领域模型列表
     *
     * @param entities JPA实体列表
     * @return 领域模型列表
     */
    List<Role> toDomainList(List<RoleEntity> entities);
    
    /**
     * 将领域模型列表转换为JPA实体列表
     *
     * @param domains 领域模型列表
     * @return JPA实体列表
     */
    List<RoleEntity> toEntityList(List<Role> domains);
    
    /**
     * 更新JPA实体
     *
     * @param domain 领域模型（源）
     * @param entity JPA实体（目标）
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateEntityFromDomain(Role domain, @MappingTarget RoleEntity entity);
}
