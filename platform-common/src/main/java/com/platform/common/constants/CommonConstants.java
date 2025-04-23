package com.platform.common.constants;

/**
 * Common constants used across the platform.
 */
public class CommonConstants {

    /**
     * Common HTTP headers
     */
    public static class Headers {
        public static final String REQUEST_ID = "X-Request-ID";
        public static final String TRACE_ID = "X-Trace-ID";
        public static final String USER_ID = "X-User-ID";
        public static final String USER_NAME = "X-User-Name";
        public static final String TOKEN = "X-Token";
        public static final String TIMESTAMP = "X-Timestamp";
    }

    /**
     * Common response codes
     */
    public static class ResponseCode {
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_ERROR = 500;
        public static final int SERVICE_UNAVAILABLE = 503;
        
        // Business specific error codes start from 1000
        public static final int VALIDATION_ERROR = 1000;
        public static final int DUPLICATE_ERROR = 1001;
        public static final int DATA_ERROR = 1002;
    }

    /**
     * Date format patterns
     */
    public static class DatePattern {
        public static final String DATE = "yyyy-MM-dd";
        public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
        public static final String TIME = "HH:mm:ss";
        public static final String DATETIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";
        public static final String ZONE_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    }

    /**
     * Character constants
     */
    public static class Chars {
        public static final String COMMA = ",";
        public static final String DOT = ".";
        public static final String COLON = ":";
        public static final String SEMICOLON = ";";
        public static final String SLASH = "/";
        public static final String BACKSLASH = "\\";
        public static final String EMPTY = "";
        public static final String SPACE = " ";
        public static final String UNDERSCORE = "_";
        public static final String DASH = "-";
    }

    /**
     * Cache related constants
     */
    public static class Cache {
        public static final String DEFAULT_PREFIX = "platform:";
        public static final int DEFAULT_EXPIRE_SECONDS = 1800; // 30 minutes
        public static final int ONE_MINUTE = 60;
        public static final int ONE_HOUR = 3600;
        public static final int ONE_DAY = 86400;
        public static final int ONE_WEEK = 604800;
    }
    
    /**
     * File related constants
     */
    public static class File {
        public static final String CSV = "csv";
        public static final String EXCEL = "xlsx";
        public static final String PDF = "pdf";
        public static final String TXT = "txt";
        public static final String JSON = "json";
        public static final String XML = "xml";
    }
    
    /**
     * Security related constants
     */
    public static class Security {
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_USER = "ROLE_USER";
        public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final int TOKEN_EXPIRE_HOURS = 24;
    }
    
    /**
     * Scheduler related constants
     */
    public static class Scheduler {
        public static final String STATUS_PENDING = "PENDING";
        public static final String STATUS_RUNNING = "RUNNING";
        public static final String STATUS_SUCCESS = "SUCCESS";
        public static final String STATUS_FAILED = "FAILED";
        public static final String STATUS_TIMEOUT = "TIMEOUT";
        public static final String STATUS_CANCELLED = "CANCELLED";
    }
    
    /**
     * Queue related constants
     */
    public static class Queue {
        public static final String DEFAULT_EXCHANGE = "platform.direct";
        public static final String DEAD_LETTER_EXCHANGE = "platform.dlx";
        public static final String DEFAULT_QUEUE_PREFIX = "platform.queue.";
        public static final String DEAD_LETTER_QUEUE_PREFIX = "platform.dlq.";
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private CommonConstants() {
        // Private constructor to prevent instantiation
    }
}
