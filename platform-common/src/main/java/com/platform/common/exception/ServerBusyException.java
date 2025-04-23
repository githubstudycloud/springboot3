package com.platform.common.exception;

/**
 * Exception thrown when the server is too busy to handle a request.
 * Used for self-protection mechanisms.
 */
public class ServerBusyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServerBusyException() {
        super("Server is currently busy, please try again later");
    }

    public ServerBusyException(String message) {
        super(message);
    }

    public ServerBusyException(String message, Throwable cause) {
        super(message, cause);
    }
}
