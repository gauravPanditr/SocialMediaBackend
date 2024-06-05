package com.example.Common.exception;

public class AuthorizationApiException extends RuntimeException {

    public AuthorizationApiException(String message) {
        super(message);
    }

    public AuthorizationApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
