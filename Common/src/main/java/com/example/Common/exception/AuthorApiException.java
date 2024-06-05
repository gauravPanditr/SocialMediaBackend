package com.example.Common.exception;

public class AuthorApiException extends RuntimeException {

    public AuthorApiException(String message) {
        super(message);
    }

    public AuthorApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
