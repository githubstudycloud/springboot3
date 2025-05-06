package com.platform.auth.infrastructure.persistence.mapper;

import com.platform.auth.domain.model.User;
import com.platform.auth.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

/**
 * 用户实体映射器
 * <p>
 * 负责领域模型与JPA实体之间的转换
 * </p>
 */
@Mapper(componentModel = "spring", 
        uses = {RoleMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    
    /**
     * 将用户JPA实体转换为领域模型
     *
     * @param entity JPA实体
     * @return 领域模型
     */
    @Mapping(target = "roles", source = "roles")
    User toDomain(UserEntity entity);
    
    /**
     * 将领域模型转换为用户JPA实体
     *
     * @param domain 领域模型
     * @return JPA实体
     */
    @Mapping(target = "roles", source = "roles")
    UserEntity toEntity(User domain);
    
    /**
     * 将JPA实体列表转换为领域模型列表
     *
     * @param entities JPA实体列表
     * @return 领域模型列表
     */
    List<User> toDomainList(List<UserEntity> entities);
    
    /**
     * 将领域模型列表转换为JPA实体列表
     *
     * @param domains 领域模型列表
     * @return JPA实体列表
     */
    List<UserEntity> toEntityList(List<User> domains);
    
    /**
     * 更新JPA实体
     *
     * @param domain 领域模型（源）
     * @param entity JPA实体（目标）
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntityFromDomain(User domain, @MappingTarget UserEntity entity);
}
