package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
// import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Centralized exception handling for all controllers
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle duplicate department exceptions
     */
    @ExceptionHandler(DuplicateDepartmentException.class)
    public String handleDuplicateDepartment(
            DuplicateDepartmentException ex,
            RedirectAttributes redirectAttributes) {

        log.error("Duplicate department error: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/departments";
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(
            ResourceNotFoundException ex,
            RedirectAttributes redirectAttributes) {

        log.error("Resource not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/";
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(
            MethodArgumentNotValidException ex,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation errors: {}", errors);
        redirectAttributes.addFlashAttribute("validationErrors", errors);
        return "redirect:/";
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(
            Exception ex,
            RedirectAttributes redirectAttributes) {

        log.error("Unexpected error occurred", ex);
        redirectAttributes.addFlashAttribute("errorMessage",
                "An unexpected error occurred. Please try again.");
        return "redirect:/";
    }
}
