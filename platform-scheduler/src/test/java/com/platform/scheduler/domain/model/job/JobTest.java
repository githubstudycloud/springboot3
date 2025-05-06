package com.platform.scheduler.domain.model.job;

import com.platform.scheduler.domain.event.common.DomainEventPublisher;
import com.platform.scheduler.domain.event.job.JobDisabledEvent;
import com.platform.scheduler.domain.event.job.JobEnabledEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 作业领域模型单元测试
 */
class JobTest {

    @Mock
    private DomainEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("创建作业 - 应成功创建有效的作业实例")
    void testCreateJob() {
        // 准备测试数据
        ScheduleStrategy scheduleStrategy = ScheduleStrategy.cronSchedule("0 0 12 * * ?");
        
        // 创建作业
        Job job = Job.builder()
                .withName("测试作业")
                .withDescription("这是一个测试作业")
                .withType(JobType.SIMPLE)
                .withPriority(JobPriority.NORMAL)
                .withScheduleStrategy(scheduleStrategy)
                .withHandlerName("testHandler")
                .withMaxRetryCount(3)
                .withRetryInterval(60)
                .withTimeout(3600)
                .withCreatedBy("test-user")
                .build();
        
        // 验证作业属性
        assertEquals("测试作业", job.getName());
        assertEquals("这是一个测试作业", job.getDescription());
        assertEquals(JobType.SIMPLE, job.getType());
        assertEquals(JobPriority.NORMAL, job.getPriority());
        assertEquals(scheduleStrategy, job.getScheduleStrategy());
        assertEquals("testHandler", job.getHandlerName());
        assertEquals(Integer.valueOf(3), job.getMaxRetryCount());
        assertEquals(Integer.valueOf(60), job.getRetryInterval());
        assertEquals(Integer.valueOf(3600), job.getTimeout());
        assertEquals("test-user", job.getCreatedBy());
        assertNotNull(job.getId());
        assertEquals(JobStatus.CREATED, job.getStatus());
        assertNotNull(job.getCreatedAt());
    }
    
    @Test
    @DisplayName("创建作业 - 缺少必要属性应抛出异常")
    void testCreateJobWithMissingRequiredProperties() {
        // 验证缺少名称时抛出异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Job.builder()
                    .withType(JobType.SIMPLE)
                    .withScheduleStrategy(ScheduleStrategy.cronSchedule("0 0 12 * * ?"))
                    .withHandlerName("testHandler")
                    .build();
        });
        assertTrue(exception.getMessage().contains("name"));
        
        // 验证缺少类型时抛出异常
        exception = assertThrows(IllegalArgumentException.class, () -> {
            Job.builder()
                    .withName("测试作业")
                    .withScheduleStrategy(ScheduleStrategy.cronSchedule("0 0 12 * * ?"))
                    .withHandlerName("testHandler")
                    .build();
        });
        assertTrue(exception.getMessage().contains("type"));
        
        // 验证缺少调度策略时抛出异常
        exception = assertThrows(IllegalArgumentException.class, () -> {
            Job.builder()
                    .withName("测试作业")
                    .withType(JobType.SIMPLE)
                    .withHandlerName("testHandler")
                    .build();
        });
        assertTrue(exception.getMessage().contains("schedule"));
        
        // 验证缺少处理器名称时抛出异常
        exception = assertThrows(IllegalArgumentException.class, () -> {
            Job.builder()
                    .withName("测试作业")
                    .withType(JobType.SIMPLE)
                    .withScheduleStrategy(ScheduleStrategy.cronSchedule("0 0 12 * * ?"))
                    .build();
        });
        assertTrue(exception.getMessage().contains("handler"));
    }
    
    @Test
    @DisplayName("启用作业 - 应将状态更改为启用并发布事件")
    void testEnableJob() {
        // 创建作业
        Job job = createTestJob();
        
        // 启用作业
        job.enable("test-user", eventPublisher);
        
        // 验证状态被更改
        assertEquals(JobStatus.ENABLED, job.getStatus());
        assertNotNull(job.getLastModifiedAt());
        assertEquals("test-user", job.getLastModifiedBy());
        
        // 验证事件被发布
        verify(eventPublisher, times(1)).publish(any(JobEnabledEvent.class));
    }
    
    @Test
    @DisplayName("禁用作业 - 应将状态更改为禁用并发布事件")
    void testDisableJob() {
        // 创建并启用作业
        Job job = createTestJob();
        job.enable("test-user", eventPublisher);
        
        // 重置模拟对象
        reset(eventPublisher);
        
        // 禁用作业
        job.disable("admin-user", eventPublisher);
        
        // 验证状态被更改
        assertEquals(JobStatus.DISABLED, job.getStatus());
        assertNotNull(job.getLastModifiedAt());
        assertEquals("admin-user", job.getLastModifiedBy());
        
        // 验证事件被发布
        verify(eventPublisher, times(1)).publish(any(JobDisabledEvent.class));
    }
    
    @Test
    @DisplayName("添加作业参数 - 应成功添加参数")
    void testAddJobParameter() {
        // 创建作业
        Job job = createTestJob();
        
        // 创建参数列表
        List<JobParameter> parameters = new ArrayList<>();
        parameters.add(new JobParameter("param1", "value1", JobParameter.ParameterType.STRING, true, "参数1"));
        parameters.add(new JobParameter("param2", "123", JobParameter.ParameterType.NUMBER, false, "参数2"));
        
        // 更新参数
        job.updateParameters(parameters, "test-user");
        
        // 验证参数
        List<JobParameter> updatedParams = job.getParameterList();
        assertEquals(2, updatedParams.size());
        
        // 验证参数1
        JobParameter param1 = updatedParams.stream()
                .filter(p -> "param1".equals(p.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(param1);
        assertEquals("value1", param1.getValue());
        assertEquals(JobParameter.ParameterType.STRING, param1.getType());
        assertTrue(param1.isRequired());
        assertEquals("参数1", param1.getDescription());
        
        // 验证参数2
        JobParameter param2 = updatedParams.stream()
                .filter(p -> "param2".equals(p.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(param2);
        assertEquals("123", param2.getValue());
        assertEquals(JobParameter.ParameterType.NUMBER, param2.getType());
        assertFalse(param2.isRequired());
        assertEquals("参数2", param2.getDescription());
        
        // 验证修改信息
        assertNotNull(job.getLastModifiedAt());
        assertEquals("test-user", job.getLastModifiedBy());
    }
    
    @Test
    @DisplayName("添加作业依赖 - 应成功添加依赖")
    void testAddJobDependency() {
        // 创建作业
        Job job = createTestJob();
        
        // 创建依赖作业ID
        JobId dependencyJobId = JobId.newId();
        
        // 添加依赖
        JobDependency dependency = JobDependency.precedenceOf(dependencyJobId, true);
        job.addDependency(dependency, "test-user");
        
        // 验证依赖
        List<JobDependency> dependencies = job.getDependencyList();
        assertEquals(1, dependencies.size());
        
        JobDependency addedDependency = dependencies.get(0);
        assertEquals(dependencyJobId, addedDependency.getDependencyJobId());
        assertEquals(JobDependency.DependencyType.PRECEDENCE, addedDependency.getType());
        assertTrue(addedDependency.isRequired());
        
        // 验证修改信息
        assertNotNull(job.getLastModifiedAt());
        assertEquals("test-user", job.getLastModifiedBy());
    }
    
    @Test
    @DisplayName("添加重复依赖 - 应抛出异常")
    void testAddDuplicateDependency() {
        // 创建作业
        Job job = createTestJob();
        
        // 创建依赖作业ID
        JobId dependencyJobId = JobId.newId();
        
        // 添加依赖
        JobDependency dependency1 = JobDependency.precedenceOf(dependencyJobId, true);
        job.addDependency(dependency1, "test-user");
        
        // 再次添加相同依赖应抛出异常
        JobDependency dependency2 = JobDependency.triggerOf(dependencyJobId, "condition");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            job.addDependency(dependency2, "test-user");
        });
        
        assertTrue(exception.getMessage().contains("already exists"));
    }
    
    @Test
    @DisplayName("移除作业依赖 - 应成功移除依赖")
    void testRemoveJobDependency() {
        // 创建作业
        Job job = createTestJob();
        
        // 创建依赖作业ID
        JobId dependencyJobId = JobId.newId();
        
        // 添加依赖
        JobDependency dependency = JobDependency.precedenceOf(dependencyJobId, true);
        job.addDependency(dependency, "test-user");
        
        // 验证依赖添加成功
        assertEquals(1, job.getDependencyList().size());
        
        // 移除依赖
        job.removeDependency(dependencyJobId, "admin-user");
        
        // 验证依赖被移除
        assertEquals(0, job.getDependencyList().size());
        
        // 验证修改信息
        assertNotNull(job.getLastModifiedAt());
        assertEquals("admin-user", job.getLastModifiedBy());
    }
    
    @Test
    @DisplayName("更新作业基本信息 - 应成功更新")
    void testUpdateJobBasicInfo() {
        // 创建作业
        Job job = createTestJob();
        
        // 更新基本信息
        job.updateBasicInfo("新作业名称", "新的描述信息", JobPriority.HIGH, "admin-user");
        
        // 验证更新
        assertEquals("新作业名称", job.getName());
        assertEquals("新的描述信息", job.getDescription());
        assertEquals(JobPriority.HIGH, job.getPriority());
        assertNotNull(job.getLastModifiedAt());
        assertEquals("admin-user", job.getLastModifiedBy());
    }
    
    @Test
    @DisplayName("更新调度策略 - 应成功更新并发布事件")
    void testUpdateScheduleStrategy() {
        // 创建作业
        Job job = createTestJob();
        
        // 创建新的调度策略
        ScheduleStrategy newStrategy = ScheduleStrategy.fixedRateSchedule(300);
        
        // 更新调度策略
        job.updateScheduleStrategy(newStrategy, "admin-user", eventPublisher);
        
        // 验证更新
        assertEquals(newStrategy, job.getScheduleStrategy());
        assertNotNull(job.getLastModifiedAt());
        assertEquals("admin-user", job.getLastModifiedBy());
        
        // 验证事件发布
        verify(eventPublisher, times(1)).publish(any());
    }
    
    /**
     * 创建测试用作业
     *
     * @return 测试作业
     */
    private Job createTestJob() {
        return Job.builder()
                .withName("测试作业")
                .withDescription("这是一个测试作业")
                .withType(JobType.SIMPLE)
                .withPriority(JobPriority.NORMAL)
                .withScheduleStrategy(ScheduleStrategy.cronSchedule("0 0 12 * * ?"))
                .withHandlerName("testHandler")
                .withMaxRetryCount(3)
                .withRetryInterval(60)
                .withTimeout(3600)
                .withCreatedBy("test-user")
                .build();
    }
}
