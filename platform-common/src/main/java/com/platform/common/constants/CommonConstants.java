package com.platform.common.constants;

/**
 * 通用常量定义
 */
public final class CommonConstants {

    /**
     * 私有构造函数，防止实例化
     */
    private CommonConstants() {
        throw new AssertionError("No CommonConstants instances for you!");
    }
    
    /**
     * 系统相关常量
     */
    public static final class System {
        /**
         * 系统默认编码
         */
        public static final String DEFAULT_CHARSET = "UTF-8";
        
        /**
         * 系统默认区域
         */
        public static final String DEFAULT_LOCALE = "zh_CN";
        
        /**
         * 系统默认时区
         */
        public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";
        
        /**
         * 默认分页大小
         */
        public static final int DEFAULT_PAGE_SIZE = 20;
        
        /**
         * 最大分页大小
         */
        public static final int MAX_PAGE_SIZE = 500;
        
        /**
         * 日期时间格式
         */
        public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        
        /**
         * 日期格式
         */
        public static final String DATE_FORMAT = "yyyy-MM-dd";
        
        /**
         * 时间格式
         */
        public static final String TIME_FORMAT = "HH:mm:ss";
    }
    
    /**
     * 符号常量
     */
    public static final class Symbol {
        /**
         * 逗号
         */
        public static final String COMMA = ",";
        
        /**
         * 点
         */
        public static final String DOT = ".";
        
        /**
         * 冒号
         */
        public static final String COLON = ":";
        
        /**
         * 分号
         */
        public static final String SEMICOLON = ";";
        
        /**
         * 下划线
         */
        public static final String UNDERSCORE = "_";
        
        /**
         * 连字符
         */
        public static final String HYPHEN = "-";
        
        /**
         * 星号
         */
        public static final String ASTERISK = "*";
        
        /**
         * 斜杠
         */
        public static final String SLASH = "/";
        
        /**
         * 反斜杠
         */
        public static final String BACKSLASH = "\\";
        
        /**
         * 等号
         */
        public static final String EQUAL = "=";
        
        /**
         * 空格
         */
        public static final String SPACE = " ";
    }
    
    /**
     * 状态常量
     */
    public static final class Status {
        /**
         * 启用
         */
        public static final int ENABLED = 1;
        
        /**
         * 禁用
         */
        public static final int DISABLED = 0;
        
        /**
         * 删除
         */
        public static final int DELETED = -1;
        
        /**
         * 是
         */
        public static final int YES = 1;
        
        /**
         * 否
         */
        public static final int NO = 0;
    }
    
    /**
     * 业务常量
     */
    public static final class Business {
        /**
         * 超级管理员角色编码
         */
        public static final String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";
        
        /**
         * 默认密码
         */
        public static final String DEFAULT_PASSWORD = "123456";
        
        /**
         * 系统默认租户ID
         */
        public static final String DEFAULT_TENANT_ID = "000000";
        
        /**
         * 匿名用户ID
         */
        public static final String ANONYMOUS_USER_ID = "-1";
    }
    
    /**
     * 安全常量
     */
    public static final class Security {
        /**
         * 认证请求头名称
         */
        public static final String AUTHORIZATION_HEADER = "Authorization";
        
        /**
         * Bearer认证方案前缀
         */
        public static final String BEARER_PREFIX = "Bearer ";
        
        /**
         * Basic认证方案前缀
         */
        public static final String BASIC_PREFIX = "Basic ";
        
        /**
         * Token过期时间（毫秒）- 24小时
         */
        public static final long TOKEN_EXPIRATION_MS = 86400000;
        
        /**
         * 刷新Token过期时间（毫秒）- 7天
         */
        public static final long REFRESH_TOKEN_EXPIRATION_MS = 604800000;
    }
    
    /**
     * 缓存常量
     */
    public static final class Cache {
        /**
         * 默认缓存过期时间（秒）- 1小时
         */
        public static final int DEFAULT_EXPIRE_SECONDS = 3600;
        
        /**
         * 最大缓存过期时间（秒）- 24小时
         */
        public static final int MAX_EXPIRE_SECONDS = 86400;
        
        /**
         * 不过期
         */
        public static final int NEVER_EXPIRE = -1;
        
        /**
         * 缓存前缀
         */
        public static final String CACHE_PREFIX = "platform:";
        
        /**
         * 用户缓存前缀
         */
        public static final String USER_CACHE_PREFIX = CACHE_PREFIX + "user:";
        
        /**
         * 权限缓存前缀
         */
        public static final String PERM_CACHE_PREFIX = CACHE_PREFIX + "perm:";
        
        /**
         * 字典缓存前缀
         */
        public static final String DICT_CACHE_PREFIX = CACHE_PREFIX + "dict:";
        
        /**
         * 配置缓存前缀
         */
        public static final String CONFIG_CACHE_PREFIX = CACHE_PREFIX + "config:";
    }
}
