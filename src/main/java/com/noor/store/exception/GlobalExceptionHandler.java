package com.noor.store.exception;

import com.noor.store.api.ErrorResponse;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.*;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of("RESOURCE_NOT_FOUND", ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> duplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of("DUPLICATE_RESOURCE", ex.getMessage(), Map.of()));
    }

    @ExceptionHandler({IllegalArgumentException.class, BusinessException.class, InsufficientStockException.class})
    public ResponseEntity<ErrorResponse> badRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of("BAD_REQUEST", ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a,b) -> a + "; " + b));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of("VALIDATION_FAILED", "Validation failed", Map.of("Validation failed",details)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> general(Exception ex) {
        ex.printStackTrace(); // helpful during development
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of("INTERNAL_ERROR", ex.getMessage() == null ? "Unexpected error" : ex.getMessage(), Map.of()));
    }
}
