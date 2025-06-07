package com.platform.config.exception;

/**
 * Git同步异常
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class GitSyncException extends RuntimeException {

    private final String operation;
    private final String gitUri;

    public GitSyncException(String operation, String gitUri, String message) {
        super(String.format("Git同步失败: operation=%s, uri=%s, message=%s", operation, gitUri, message));
        this.operation = operation;
        this.gitUri = gitUri;
    }

    public GitSyncException(String operation, String gitUri, String message, Throwable cause) {
        super(String.format("Git同步失败: operation=%s, uri=%s, message=%s", operation, gitUri, message), cause);
        this.operation = operation;
        this.gitUri = gitUri;
    }

    public String getOperation() {
        return operation;
    }

    public String getGitUri() {
        return gitUri;
    }
} 