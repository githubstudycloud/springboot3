-- 初始化任务模板和依赖关系管理相关表

-- 任务模板表
CREATE TABLE job_template (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    handler_name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    last_modified_by VARCHAR(50),
    last_modified_at DATETIME,
    is_built_in BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_template_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 任务参数模板表
CREATE TABLE job_parameter_template (
    id BIGINT AUTO_INCREMENT NOT NULL,
    template_id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    type VARCHAR(20) NOT NULL,
    default_value VARCHAR(500),
    required BOOLEAN NOT NULL DEFAULT FALSE,
    validation_pattern VARCHAR(200),
    validation_message VARCHAR(200),
    min_value VARCHAR(50),
    max_value VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_param_template_name (template_id, name),
    CONSTRAINT fk_param_template_job_template FOREIGN KEY (template_id) REFERENCES job_template (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 参数模板允许值表
CREATE TABLE job_parameter_allowed_value (
    id BIGINT AUTO_INCREMENT NOT NULL,
    parameter_id BIGINT NOT NULL,
    value VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_allowed_value_parameter FOREIGN KEY (parameter_id) REFERENCES job_parameter_template (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 任务模板标签表
CREATE TABLE job_template_label (
    id BIGINT AUTO_INCREMENT NOT NULL,
    template_id VARCHAR(36) NOT NULL,
    label VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_label (template_id, label),
    CONSTRAINT fk_label_job_template FOREIGN KEY (template_id) REFERENCES job_template (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 任务版本表
CREATE TABLE job_version (
    id VARCHAR(36) NOT NULL,
    job_id VARCHAR(36) NOT NULL,
    job_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    handler_name VARCHAR(200) NOT NULL,
    version INT NOT NULL,
    schedule_strategy_json TEXT NOT NULL,
    max_retry_count INT,
    retry_interval INT,
    timeout INT,
    status VARCHAR(20) NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    created_by VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    comment VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_version (job_id, version),
    INDEX idx_job_version_job_id (job_id),
    INDEX idx_job_version_current (job_id, is_current)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 任务版本参数表
CREATE TABLE job_version_parameter (
    id BIGINT AUTO_INCREMENT NOT NULL,
    version_id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    display_name VARCHAR(100),
    description VARCHAR(200),
    type VARCHAR(20) NOT NULL,
    value VARCHAR(500),
    required BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_version_param_name (version_id, name),
    CONSTRAINT fk_param_job_version FOREIGN KEY (version_id) REFERENCES job_version (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 任务版本依赖表
CREATE TABLE job_version_dependency (
    id BIGINT AUTO_INCREMENT NOT NULL,
    version_id VARCHAR(36) NOT NULL,
    dependency_job_id VARCHAR(36) NOT NULL,
    dependency_job_name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    condition_expr VARCHAR(500),
    description VARCHAR(200),
    PRIMARY KEY (id),
    UNIQUE KEY uk_version_dependency (version_id, dependency_job_id),
    CONSTRAINT fk_dependency_job_version FOREIGN KEY (version_id) REFERENCES job_version (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 依赖关系定义表
CREATE TABLE dependency_definition (
    id VARCHAR(36) NOT NULL,
    source_job_id VARCHAR(36) NOT NULL,
    source_job_name VARCHAR(100) NOT NULL,
    target_job_id VARCHAR(36) NOT NULL,
    target_job_name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    condition_expr VARCHAR(500),
    description VARCHAR(200),
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    last_modified_by VARCHAR(50),
    last_modified_at DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_dependency (source_job_id, target_job_id),
    INDEX idx_dependency_source (source_job_id),
    INDEX idx_dependency_target (target_job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 模板使用记录表
CREATE TABLE job_template_usage (
    id BIGINT AUTO_INCREMENT NOT NULL,
    template_id VARCHAR(36) NOT NULL,
    job_id VARCHAR(36) NOT NULL,
    job_name VARCHAR(100) NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_template_usage (template_id),
    CONSTRAINT fk_usage_template FOREIGN KEY (template_id) REFERENCES job_template (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 版本回滚记录表
CREATE TABLE job_version_rollback (
    id BIGINT AUTO_INCREMENT NOT NULL,
    job_id VARCHAR(36) NOT NULL,
    from_version_id VARCHAR(36) NOT NULL,
    to_version_id VARCHAR(36) NOT NULL,
    rollback_reason VARCHAR(500),
    created_by VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_version_rollback_job (job_id),
    CONSTRAINT fk_rollback_from_version FOREIGN KEY (from_version_id) REFERENCES job_version (id),
    CONSTRAINT fk_rollback_to_version FOREIGN KEY (to_version_id) REFERENCES job_version (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建索引
CREATE INDEX idx_job_template_status ON job_template(status);
CREATE INDEX idx_job_template_category ON job_template(category);
CREATE INDEX idx_job_template_type ON job_template(type);
CREATE INDEX idx_job_template_handler ON job_template(handler_name);
CREATE INDEX idx_job_version_status ON job_version(status);
CREATE INDEX idx_dependency_definition_status ON dependency_definition(status);
CREATE INDEX idx_dependency_definition_type ON dependency_definition(type);
