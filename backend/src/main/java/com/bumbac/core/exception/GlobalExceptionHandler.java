package com.bumbac.core.exception;

import com.bumbac.core.dto.ErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // ====== ЛЕГАСИ-ФОРМАТ (оставляем для совместимости там, где это ожидается)
  // ======
  private ErrorResponse buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
    return new ErrorResponse(
        java.time.LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI());
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
    log.warn("BaseException handled: {} for URI: {}", ex.getMessage(), request.getRequestURI());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, HttpServletRequest request) {
    log.error("RuntimeException handled: {} for URI: {}", ex.getMessage(), request.getRequestURI(), ex);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
    log.warn("AccessDeniedException handled: {} for URI: {}", ex.getMessage(), request.getRequestURI());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
    log.warn("UserNotFoundException handled: {} for URI: {}", ex.getMessage(), request.getRequestURI());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
      HttpServletRequest request) {
    log.warn("ResourceNotFoundException handled: {} for URI: {}", ex.getMessage(), request.getRequestURI());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request));
  }

  // ====== НОВЫЙ ЕДИНЫЙ ФОРМАТ ДЛЯ ВАЛИДАЦИИ/DTO/БИЗНЕС-ОШИБОК ======
  // Формат:
  // {
  // "timestamp": "2025-08-18T11:16:45.785111775Z",
  // "messages": [ { "field": "<field|general>", "message": "<text>" }, ... ],
  // "status": 400,
  // "error": "Validation failed",
  // "path": "/api/..."
  // }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
      HttpServletRequest req) {
    List<Map<String, String>> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fe -> Map.of(
            "field", fe.getField(),
            "message", Optional.ofNullable(fe.getDefaultMessage()).orElse("Invalid value")))
        .toList();

    log.warn("Validation failed for URI: {} with {} errors", req.getRequestURI(), errors.size());

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("messages", errors);
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Validation failed");
    body.put("path", req.getRequestURI());

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex,
      HttpServletRequest req) {
    List<Map<String, String>> errors = ex.getConstraintViolations().stream()
        .map(cv -> Map.of(
            "field", cv.getPropertyPath() == null ? "general" : cv.getPropertyPath().toString(),
            "message", Optional.ofNullable(cv.getMessage()).orElse("Invalid value")))
        .toList();

    log.warn("Constraint violation for URI: {} with {} errors", req.getRequestURI(), errors.size());

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("messages", errors);
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Validation failed");
    body.put("path", req.getRequestURI());

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex,
      HttpServletRequest req) {
    var statusCode = ex.getStatusCode(); // HttpStatusCode
    var resolved = HttpStatus.resolve(statusCode.value()); // может вернуть null, если код нестандартный
    String error = (resolved != null) ? resolved.getReasonPhrase() : statusCode.toString();

    log.warn("ResponseStatusException for URI: {} with status: {}", req.getRequestURI(), statusCode);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("messages", List.of(Map.of(
        "field", "general",
        "message", Optional.ofNullable(ex.getReason()).orElse(error))));
    body.put("status", statusCode.value());
    body.put("error", error);
    body.put("path", req.getRequestURI());

    return ResponseEntity.status(statusCode).body(body);
  }

  // JWT/креды — 401, чтобы не конфликтовать с AccessDenied (403)
  @ExceptionHandler({ JwtException.class, IllegalArgumentException.class, BadCredentialsException.class })
  public ResponseEntity<Map<String, Object>> handleJwtErrors(Exception ex, HttpServletRequest req) {
    log.warn("Authentication error for URI: {}: {}", req.getRequestURI(), ex.getMessage());

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("messages", List.of(Map.of(
        "field", "general",
        "message", "Authentication required")));
    body.put("status", HttpStatus.UNAUTHORIZED.value());
    body.put("error", "Unauthorized");
    body.put("path", req.getRequestURI());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {
    log.error("Unexpected error for URI: {}: {}", req.getRequestURI(), ex.getMessage(), ex);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("messages", List.of(Map.of(
        "field", "general",
        "message", "Unexpected error: " + ex.getMessage())));
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Internal Server Error");
    body.put("path", req.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
