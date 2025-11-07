package com.freelancenexus.userservice.exception;

public class KeycloakException extends RuntimeException {
    public KeycloakException(String message) {
        super(message);
    }
}