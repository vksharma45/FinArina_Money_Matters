package com.portfolio.exception;

/**
 * Exception thrown when the request contains invalid data.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
