package com.platform.config.controller;

import com.alibaba.fastjson2.JSON;
import com.platform.config.service.ConfigManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ConfigController 单元测试
 *
 * @author Platform Team
 * @since 2.0.0
 */
@WebMvcTest(ConfigController.class)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigManagementService configManagementService;

    @Test
    void testRefreshConfig() throws Exception {
        // Given
        String application = "test-app";
        String operator = "test-user";
        HashSet<String> refreshedKeys = new HashSet<>(Arrays.asList("key1", "key2", "key3"));
        
        when(configManagementService.refreshConfig(application, operator))
            .thenReturn(refreshedKeys);

        // When & Then
        mockMvc.perform(post("/config/management/refresh/{application}", application)
                .param("operator", operator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("配置刷新成功"))
                .andExpect(jsonPath("$.application").value(application))
                .andExpect(jsonPath("$.operator").value(operator))
                .andExpect(jsonPath("$.refreshedCount").value(3));
    }

    @Test
    void testRefreshConfigWithDefaultOperator() throws Exception {
        // Given
        String application = "test-app";
        HashSet<String> refreshedKeys = new HashSet<>(Arrays.asList("key1"));
        
        when(configManagementService.refreshConfig(application, "system"))
            .thenReturn(refreshedKeys);

        // When & Then
        mockMvc.perform(post("/config/management/refresh/{application}", application))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operator").value("system"));
    }

    @Test
    void testSwitchConfigSource() throws Exception {
        // Given
        String source = "gitlab";
        String operator = "admin";
        String oldSource = "native";
        
        when(configManagementService.getCurrentConfigSource()).thenReturn(oldSource);

        // When & Then
        mockMvc.perform(post("/config/management/switch-source")
                .param("source", source)
                .param("operator", operator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("配置源切换成功"))
                .andExpect(jsonPath("$.oldSource").value(oldSource))
                .andExpect(jsonPath("$.newSource").value(source))
                .andExpect(jsonPath("$.operator").value(operator));
    }

    @Test
    void testGetConfigStatus() throws Exception {
        // Given
        Map<String, Object> status = new HashMap<>();
        status.put("currentSource", "gitlab");
        status.put("gitlabAvailable", true);
        status.put("version", "2.0.0");
        status.put("cache", Map.of("size", 10, "hitRate", 0.85));
        
        when(configManagementService.getConfigStatus()).thenReturn(status);

        // When & Then
        mockMvc.perform(get("/config/management/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentSource").value("gitlab"))
                .andExpect(jsonPath("$.gitlabAvailable").value(true))
                .andExpect(jsonPath("$.version").value("2.0.0"));
    }

    @Test
    void testSyncToGitlab() throws Exception {
        // Given
        String operator = "sync-user";
        String result = "同步成功：推送了5个配置文件";
        
        when(configManagementService.syncConfigToGitlab(operator)).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/config/management/sync-to-gitlab")
                .param("operator", operator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("配置同步到GitLab成功"))
                .andExpect(jsonPath("$.result").value(result))
                .andExpect(jsonPath("$.operator").value(operator));
    }

    @Test
    void testPullFromGitlab() throws Exception {
        // Given
        String operator = "pull-user";
        String result = "拉取成功：更新了3个配置文件";
        
        when(configManagementService.pullConfigFromGitlab(operator)).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/config/management/pull-from-gitlab")
                .param("operator", operator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("从GitLab拉取配置成功"))
                .andExpect(jsonPath("$.result").value(result));
    }

    @Test
    void testBackupConfig() throws Exception {
        // Given
        String operator = "backup-user";
        String backupPath = "/backup/config-20250607-231200.tar.gz";
        
        when(configManagementService.backupConfig(operator)).thenReturn(backupPath);

        // When & Then
        mockMvc.perform(post("/config/management/backup")
                .param("operator", operator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("配置备份成功"))
                .andExpect(jsonPath("$.backupPath").value(backupPath));
    }

    @Test
    void testRestoreConfig() throws Exception {
        // Given
        String backupPath = "/backup/config-20250607-231200.tar.gz";
        String operator = "restore-user";

        // When & Then
        mockMvc.perform(post("/config/management/restore")
                .param("backupPath", backupPath)
                .param("operator", operator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("配置恢复成功"));
    }

    @Test
    void testListBackups() throws Exception {
        // Given
        List<String> backups = Arrays.asList(
            "/backup/config-20250607-231200.tar.gz",
            "/backup/config-20250606-181500.tar.gz",
            "/backup/config-20250605-120000.tar.gz"
        );
        
        when(configManagementService.listBackups()).thenReturn(backups);

        // When & Then
        mockMvc.perform(get("/config/management/backups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取备份列表成功"))
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    void testGetCacheInfo() throws Exception {
        // Given
        Map<String, Object> cacheInfo = new HashMap<>();
        cacheInfo.put("size", 100L);
        cacheInfo.put("hitRate", 0.85);
        cacheInfo.put("hitCount", 850L);
        cacheInfo.put("missCount", 150L);
        
        when(configManagementService.getCacheInfo()).thenReturn(cacheInfo);

        // When & Then
        mockMvc.perform(get("/config/management/cache/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取缓存信息成功"))
                .andExpect(jsonPath("$.cache.size").value(100))
                .andExpect(jsonPath("$.cache.hitRate").value(0.85));
    }

    @Test
    void testClearCache() throws Exception {
        // Given
        String operator = "cache-admin";

        // When & Then
        mockMvc.perform(post("/config/management/cache/clear")
                .param("operator", operator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("缓存清理成功"))
                .andExpect(jsonPath("$.operator").value(operator));
    }

    @Test
    void testClearSpecificCache() throws Exception {
        // Given
        String application = "test-app";
        String profile = "dev";
        String operator = "cache-admin";

        // When & Then
        mockMvc.perform(post("/config/management/cache/clear/{application}", application)
                .param("profile", profile)
                .param("operator", operator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("指定配置缓存清理成功"))
                .andExpect(jsonPath("$.application").value(application))
                .andExpect(jsonPath("$.profile").value(profile));
    }

    @Test
    void testWarmUpCache() throws Exception {
        // Given
        String operator = "cache-admin";
        Map<String, Object> configs = new HashMap<>();
        configs.put("app1:dev", "config1");
        configs.put("app2:test", "config2");
        
        String requestBody = JSON.toJSONString(configs);

        // When & Then
        mockMvc.perform(post("/config/management/cache/warmup")
                .param("operator", operator)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("缓存预热成功"))
                .andExpect(jsonPath("$.configCount").value(2))
                .andExpect(jsonPath("$.operator").value(operator));
    }

    @Test
    void testHealthCheck() throws Exception {
        // Given
        Map<String, Object> status = new HashMap<>();
        status.put("currentSource", "gitlab");
        status.put("gitlabAvailable", true);
        status.put("version", "2.0.0");
        
        when(configManagementService.getConfigStatus()).thenReturn(status);

        // When & Then
        mockMvc.perform(get("/config/management/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.version").value("2.0.0"))
                .andExpect(jsonPath("$.details.gitlabAvailable").value(true));
    }

    @Test
    void testHealthCheckWhenGitlabUnavailable() throws Exception {
        // Given
        Map<String, Object> status = new HashMap<>();
        status.put("currentSource", "native");
        status.put("gitlabAvailable", null); // GitLab不可用
        
        when(configManagementService.getConfigStatus()).thenReturn(status);

        // When & Then
        mockMvc.perform(get("/config/management/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DOWN"));
    }
} 