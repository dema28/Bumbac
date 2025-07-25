package com.bumbac.auth.controller;

import com.bumbac.auth.dto.*;
import com.bumbac.auth.entity.RefreshToken;
import com.bumbac.auth.entity.User;
import com.bumbac.auth.security.JwtService;
import com.bumbac.auth.service.AuthService;
import com.bumbac.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {
            RefreshToken token = refreshTokenService.validate(request.getRefreshToken());
            String newAccessToken = jwtService.generateToken(token.getUser());

            return ResponseEntity.ok(new RefreshResponse(newAccessToken));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(403).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 403,
                    "error", "Forbidden",
                    "message", ex.getMessage(),
                    "path", "/api/auth/refresh"
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractUsername(token);

        User user = authService.getUserByEmail(email);
        refreshTokenService.deleteByUserId(user.getId());

        return ResponseEntity.ok(Map.of(
                "timestamp", LocalDateTime.now(),
                "message", "Logout successful"
        ));
    }

}
