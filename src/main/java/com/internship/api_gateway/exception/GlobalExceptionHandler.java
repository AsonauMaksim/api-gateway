package com.internship.api_gateway.exception;

import com.internship.api_gateway.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ApiError build(HttpStatus status, String msg, String path, List<String> errors) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(msg)
                .path(path)
                .errors(errors)
                .build();
    }

    private ApiError build(HttpStatus status, String msg, String path) {
        return build(status, msg, path, null);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAlreadyExists(AlreadyExistsException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT, ex.getMessage(), exchange.getRequest().getPath().toString()));
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiError> handleRegistration(RegistrationException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange.getRequest().getPath().toString()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> handleValidation(WebExchangeBindException ex, ServerWebExchange exchange) {
        List<String> errors = ex.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(build(HttpStatus.BAD_REQUEST, "Validation error", exchange.getRequest().getPath().toString(), errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), exchange.getRequest().getPath().toString()));
    }
}
