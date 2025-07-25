package com.bumbac.auth.controller;

import com.bumbac.auth.dto.*;
import com.bumbac.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      BindingResult result) {
        if (result.hasErrors()) {
            var errors = result.getFieldErrors().stream()
                    .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                    .toList();
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 400,
                    "error", "Validation failed",
                    "messages", errors
            ));
        }

        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", ex.getStatusCode().value(),
                    "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
                    "message", ex.getReason(),
                    "path", "/api/auth/register"
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   BindingResult result) {
        if (result.hasErrors()) {
            var errors = result.getFieldErrors().stream()
                    .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                    .toList();
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 400,
                    "error", "Validation failed",
                    "messages", errors
            ));
        }

        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", ex.getStatusCode().value(),
                    "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
                    "message", ex.getReason(),
                    "path", "/api/auth/login"
            ));
        }
    }

}
