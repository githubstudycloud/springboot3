package com.example.framework.application.service;

import com.example.framework.application.dto.UserDTO;
import com.example.framework.domain.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户应用服务
 * 应用层服务示例，负责用例的编排和协调
 *
 * @author platform
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserApplicationService {

    private final Repository userRepository;

    /**
     * 构造函数注入依赖
     * Spring Boot 3.2+推荐的注入方式
     *
     * @param userRepository 用户仓储接口
     */
    public UserApplicationService(Repository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取用户DTO
     * 应用层方法示例，将领域对象转换为DTO
     *
     * @param id 用户ID
     * @return 用户DTO
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        // 实际项目中，这里会调用领域层获取领域对象，然后转换为DTO
        return Optional.empty();
    }

    /**
     * 创建用户
     * 应用层方法示例，处理用户创建用例
     *
     * @param userDTO 用户数据
     * @return 创建的用户ID
     */
    @Transactional
    public Long createUser(UserDTO userDTO) {
        log.info("Creating new user: {}", userDTO.getUsername());
        // 实际项目中，这里会创建领域对象，并使用仓储保存
        return 0L;
    }
}