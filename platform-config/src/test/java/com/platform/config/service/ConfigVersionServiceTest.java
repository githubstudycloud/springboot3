package com.platform.config.service;

import com.platform.config.entity.ConfigVersion;
import com.platform.config.exception.ConfigNotFoundException;
import com.platform.config.exception.ConfigValidationException;
import com.platform.config.metrics.ConfigBusinessMetrics;
import com.platform.config.repository.ConfigVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ConfigVersionService单元测试
 */
@ExtendWith(MockitoExtension.class)
class ConfigVersionServiceTest {

    @Mock
    private ConfigVersionRepository versionRepository;

    @Mock
    private ConfigBusinessMetrics businessMetrics;

    @InjectMocks
    private ConfigVersionService configVersionService;

    private ConfigVersion mockVersion;

    @BeforeEach
    void setUp() {
        mockVersion = ConfigVersion.builder()
                .id(1L)
                .application("test-app")
                .profile("dev")
                .version("v20240607120000")
                .content("test.property=value")
                .contentHash("test-hash")
                .operator("test-user")
                .active(false)
                .build();
    }

    @Test
    void testSaveVersion_Success() {
        // Given
        String application = "test-app";
        String profile = "dev";
        String content = "test.property=value";
        String operator = "test-user";

        when(versionRepository.findByContentHashOrderByCreateTimeDesc(any())).thenReturn(List.of());
        when(versionRepository.save(any(ConfigVersion.class))).thenReturn(mockVersion);
        when(businessMetrics.startVersionOperationTimer(any())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        when(configVersionService.getCurrentActiveVersion(application, profile)).thenReturn(Optional.empty());

        // When
        ConfigVersion result = configVersionService.saveVersion(application, profile, content, operator);

        // Then
        assertNotNull(result);
        assertEquals("test-app", result.getApplication());
        assertEquals("dev", result.getProfile());
        assertEquals("test-user", result.getOperator());
        verify(versionRepository).save(any(ConfigVersion.class));
        verify(businessMetrics).recordVersionCreate(application, profile, operator);
    }

    @Test
    void testSaveVersion_DuplicateContent() {
        // Given
        String application = "test-app";
        String profile = "dev";
        String content = "test.property=value";
        String operator = "test-user";

        when(versionRepository.findByContentHashOrderByCreateTimeDesc(any())).thenReturn(List.of(mockVersion));
        when(businessMetrics.startVersionOperationTimer(any())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When
        ConfigVersion result = configVersionService.saveVersion(application, profile, content, operator);

        // Then
        assertNotNull(result);
        assertEquals(mockVersion, result);
        verify(versionRepository, never()).save(any(ConfigVersion.class));
    }

    @Test
    void testSaveVersion_InvalidInput() {
        // Given
        String application = null;
        String profile = "dev";
        String content = "test.property=value";
        String operator = "test-user";

        when(businessMetrics.startVersionOperationTimer(any())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When & Then
        assertThrows(ConfigValidationException.class, () -> {
            configVersionService.saveVersion(application, profile, content, operator);
        });
    }

    @Test
    void testActivateVersion_Success() {
        // Given
        Long versionId = 1L;
        String operator = "test-user";

        when(versionRepository.findById(versionId)).thenReturn(Optional.of(mockVersion));
        when(versionRepository.save(any(ConfigVersion.class))).thenReturn(mockVersion);
        when(businessMetrics.startVersionOperationTimer(any())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When
        ConfigVersion result = configVersionService.activateVersion(versionId, operator);

        // Then
        assertNotNull(result);
        verify(versionRepository).deactivateAllVersions("test-app", "dev");
        verify(versionRepository).save(any(ConfigVersion.class));
        verify(businessMetrics).recordVersionActivate("test-app", "dev", operator, "v20240607120000");
    }

    @Test
    void testActivateVersion_VersionNotFound() {
        // Given
        Long versionId = 999L;
        String operator = "test-user";

        when(versionRepository.findById(versionId)).thenReturn(Optional.empty());
        when(businessMetrics.startVersionOperationTimer(any())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When & Then
        assertThrows(ConfigNotFoundException.class, () -> {
            configVersionService.activateVersion(versionId, operator);
        });
    }

    @Test
    void testRollback_Success() {
        // Given
        String application = "test-app";
        String profile = "dev";
        String targetVersion = "v20240607120000";
        String operator = "test-user";

        when(versionRepository.findByVersion(targetVersion)).thenReturn(Optional.of(mockVersion));
        when(versionRepository.save(any(ConfigVersion.class))).thenReturn(mockVersion);
        when(businessMetrics.startVersionOperationTimer(any())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        when(configVersionService.getCurrentActiveVersion(application, profile)).thenReturn(Optional.of(mockVersion));

        // When
        ConfigVersion result = configVersionService.rollback(application, profile, targetVersion, operator);

        // Then
        assertNotNull(result);
        verify(versionRepository).findByVersion(targetVersion);
        verify(businessMetrics).recordVersionRollback(application, profile, operator, anyString(), targetVersion);
    }

    @Test
    void testRollback_TargetVersionNotFound() {
        // Given
        String application = "test-app";
        String profile = "dev";
        String targetVersion = "v20240607999999";
        String operator = "test-user";

        when(versionRepository.findByVersion(targetVersion)).thenReturn(Optional.empty());
        when(businessMetrics.startVersionOperationTimer(any())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When & Then
        assertThrows(ConfigNotFoundException.class, () -> {
            configVersionService.rollback(application, profile, targetVersion, operator);
        });
    }

    @Test
    void testGetCurrentActiveVersion() {
        // Given
        String application = "test-app";
        String profile = "dev";
        mockVersion.setActive(true);

        when(versionRepository.findByApplicationAndProfileAndActiveTrue(application, profile))
                .thenReturn(Optional.of(mockVersion));

        // When
        Optional<ConfigVersion> result = configVersionService.getCurrentActiveVersion(application, profile);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockVersion, result.get());
        assertTrue(result.get().isActive());
    }

    @Test
    void testGetVersionHistory() {
        // Given
        String application = "test-app";
        String profile = "dev";
        Pageable pageable = PageRequest.of(0, 10);
        List<ConfigVersion> versions = Arrays.asList(mockVersion);
        Page<ConfigVersion> page = new PageImpl<>(versions, pageable, 1);

        when(versionRepository.findByApplicationAndProfileOrderByCreateTimeDesc(application, profile, pageable))
                .thenReturn(page);

        // When
        Page<ConfigVersion> result = configVersionService.getVersionHistory(application, profile, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockVersion, result.getContent().get(0));
    }

    @Test
    void testGetVersionByNumber() {
        // Given
        String version = "v20240607120000";

        when(versionRepository.findByVersion(version)).thenReturn(Optional.of(mockVersion));

        // When
        Optional<ConfigVersion> result = configVersionService.getVersionByNumber(version);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockVersion, result.get());
    }

    @Test
    void testCompareVersions() {
        // Given
        String fromVersion = "v20240607120000";
        String toVersion = "v20240607130000";
        
        ConfigVersion fromConfigVersion = ConfigVersion.builder()
                .version(fromVersion)
                .content("property1=value1")
                .build();
        
        ConfigVersion toConfigVersion = ConfigVersion.builder()
                .version(toVersion)
                .content("property1=value2\nproperty2=value2")
                .build();

        when(versionRepository.findByVersion(fromVersion)).thenReturn(Optional.of(fromConfigVersion));
        when(versionRepository.findByVersion(toVersion)).thenReturn(Optional.of(toConfigVersion));

        // When
        String diff = configVersionService.compareVersions(fromVersion, toVersion);

        // Then
        assertNotNull(diff);
        assertTrue(diff.contains("版本对比报告"));
        assertTrue(diff.contains(fromVersion));
        assertTrue(diff.contains(toVersion));
    }

    @Test
    void testCompareVersions_VersionNotFound() {
        // Given
        String fromVersion = "v20240607120000";
        String toVersion = "v20240607999999";

        when(versionRepository.findByVersion(fromVersion)).thenReturn(Optional.of(mockVersion));
        when(versionRepository.findByVersion(toVersion)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ConfigNotFoundException.class, () -> {
            configVersionService.compareVersions(fromVersion, toVersion);
        });
    }

    @Test
    void testGetVersionsByTag() {
        // Given
        String tag = "release";
        List<ConfigVersion> versions = Arrays.asList(mockVersion);

        when(versionRepository.findByTagOrderByCreateTimeDesc(tag)).thenReturn(versions);

        // When
        List<ConfigVersion> result = configVersionService.getVersionsByTag(tag);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockVersion, result.get(0));
    }

    @Test
    void testCleanupVersionHistory() {
        // Given
        String application = "test-app";
        String profile = "dev";
        int keepCount = 5;

        when(versionRepository.countByApplicationAndProfile(application, profile)).thenReturn(10L);
        when(versionRepository.deleteOldVersions(application, profile, keepCount)).thenReturn(5);

        // When
        int deletedCount = configVersionService.cleanupVersionHistory(application, profile, keepCount);

        // Then
        assertEquals(5, deletedCount);
        verify(versionRepository).deleteOldVersions(application, profile, keepCount);
    }

    @Test
    void testStatisticsMethods() {
        // Given
        when(versionRepository.countDistinctApplications()).thenReturn(5L);
        when(versionRepository.countDistinctProfiles()).thenReturn(3L);
        when(versionRepository.count()).thenReturn(100L);

        // When & Then
        assertEquals(5L, configVersionService.getTotalApplicationsCount());
        assertEquals(3L, configVersionService.getTotalProfilesCount());
        assertEquals(100L, configVersionService.getTotalVersionsCount());
    }
} 