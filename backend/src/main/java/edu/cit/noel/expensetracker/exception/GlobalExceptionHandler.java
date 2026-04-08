package edu.cit.noel.expensetracker.exception;

import edu.cit.noel.expensetracker.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }
}
