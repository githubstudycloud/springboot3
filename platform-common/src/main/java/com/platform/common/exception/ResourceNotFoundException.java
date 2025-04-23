package com.platform.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(resourceType + " with id " + resourceId + " not found");
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
