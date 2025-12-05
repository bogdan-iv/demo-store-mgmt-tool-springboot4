package com.demo.store.mgmt.tool.exception;

public class ConcurrencyConflictException extends RuntimeException {
    public ConcurrencyConflictException(String message) {
        super(message);
    }
}