package com.example.framework.interfaces.rest;

import com.example.common.model.R;
import com.example.framework.application.dto.UserDTO;
import com.example.framework.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户REST控制器
 * 接口层示例，负责HTTP请求的处理
 *
 * @author platform
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserApplicationService userApplicationService;

    /**
     * 构造函数注入依赖
     *
     * @param userApplicationService 用户应用服务
     */
    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<R<UserDTO>> getUser(@PathVariable Long id) {
        log.info("REST request to get User : {}", id);
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
    public ResponseEntity<R<Long>> createUser(@RequestBody UserDTO userDTO) {
        log.info("REST request to create User : {}", userDTO);
        Long userId = userApplicationService.createUser(userDTO);
        return ResponseEntity.ok(R.ok(userId));
    }
}