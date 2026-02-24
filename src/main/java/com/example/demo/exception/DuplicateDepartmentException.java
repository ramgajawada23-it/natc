package com.example.demo.exception;

/**
 * Exception thrown when attempting to create a duplicate department
 */
public class DuplicateDepartmentException extends RuntimeException {
    
    public DuplicateDepartmentException(String message) {
        super(message);
    }
    
    public DuplicateDepartmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
