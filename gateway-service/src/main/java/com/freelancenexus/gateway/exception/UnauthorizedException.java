package com.freelancenexus.gateway.exception;
/**
 * Custom Unauthorized Exception
 * 
 * Thrown when authentication fails or user doesn't have
 * required permissions to access a resource.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
