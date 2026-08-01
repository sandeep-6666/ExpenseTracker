package com.sandeep.expensetracker.exception;

/** Thrown for authentication/authorization failures. Maps to HTTP 401. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
