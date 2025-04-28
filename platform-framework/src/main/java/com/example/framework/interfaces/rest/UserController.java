package com.example.framework.interfaces.rest;

import com.example.common.model.R;
import com.example.framework.application.dto.UserDTO;
import com.example.framework.application.service.UserApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户REST控制器
 * 接口层示例，负责HTTP请求的处理
 *
 * @author platform
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private final UserApplicationService userApplicationService;

    /**
     * 构造函数注入依赖
     *
     * @param userApplicationService 用户应用服务
     */
    public UserController(final UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable final Long id) {
        LOG.info("REST请求获取用户信息: {}", id);
        return userApplicationService.getUserById(id)
                .map(userDTO -> ResponseEntity.ok(R.ok(userDTO)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建用户
     *
     * @param userDTO 用户数据
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<R<Long>> createUser(@RequestBody final UserDTO userDTO) {
        LOG.info("REST请求创建用户: {}", userDTO);
        Long userId = userApplicationService.createUser(userDTO);
        return ResponseEntity.ok(R.ok(userId));
    }
}