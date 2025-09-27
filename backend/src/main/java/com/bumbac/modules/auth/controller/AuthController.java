package com.bumbac.modules.auth.controller;

import com.bumbac.modules.auth.dto.LoginRequest;
import com.bumbac.modules.auth.dto.RefreshRequest;
import com.bumbac.modules.auth.dto.RefreshResponse;
import com.bumbac.modules.auth.dto.RegisterRequest;
import com.bumbac.modules.auth.entity.RefreshToken;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.security.JwtService;
import com.bumbac.modules.auth.service.AuthService;
import com.bumbac.modules.auth.service.RefreshTokenService;
import com.bumbac.core.dto.ErrorResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API для аутентификации и авторизации пользователей")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    // Метрики
    private final Counter successfulLogins;
    private final Counter failedLogins;
    private final Counter successfulRegistrations;
    private final Counter failedRegistrations;
    private final Counter successfulLogouts;
    private final Counter tokenRefreshes;
    private final Counter failedTokenRefreshes;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService,
                          JwtService jwtService, MeterRegistry meterRegistry) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;

        // Инициализация метрик
        this.successfulLogins = Counter.builder("auth.login.successful")
                .description("Количество успешных входов в систему")
                .register(meterRegistry);

        this.failedLogins = Counter.builder("auth.login.failed")
                .description("Количество неуспешных попыток входа")
                .register(meterRegistry);

        this.successfulRegistrations = Counter.builder("auth.registration.successful")
                .description("Количество успешных регистраций")
                .register(meterRegistry);

        this.failedRegistrations = Counter.builder("auth.registration.failed")
                .description("Количество неуспешных попыток регистрации")
                .register(meterRegistry);

        this.successfulLogouts = Counter.builder("auth.logout.successful")
                .description("Количество успешных выходов из системы")
                .register(meterRegistry);

        this.tokenRefreshes = Counter.builder("auth.token.refresh.successful")
                .description("Количество успешных обновлений токенов")
                .register(meterRegistry);

        this.failedTokenRefreshes = Counter.builder("auth.token.refresh.failed")
                .description("Количество неуспешных попыток обновления токенов")
                .register(meterRegistry);

        log.info("AuthController метрики инициализированы");
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя по email и паролю")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      BindingResult result, HttpServletRequest httpRequest) {
        String clientIP = httpRequest.getRemoteAddr();
        log.info("Попытка регистрации пользователя с email: {} с IP: {}", request.getEmail(), clientIP);

        if (result.hasErrors()) {
            log.warn("Ошибка валидации при регистрации пользователя {}: {} ошибок",
                    request.getEmail(), result.getErrorCount());
            failedRegistrations.increment();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed");
        }

        try {
            var response = authService.register(request);
            log.info("Пользователь {} успешно зарегистрирован с IP: {}", request.getEmail(), clientIP);
            successfulRegistrations.increment();
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            log.error("Ошибка при регистрации пользователя {} с IP {}: {}", request.getEmail(), clientIP, ex.getReason());
            failedRegistrations.increment();
            throw ex;
        }
    }

    @GetMapping("/verify")
    @Operation(summary = "Подтверждение email", description = "Подтверждает email по токену из письма")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmail(token);
            log.info("Email подтвержден для токена {}", token);
            return ResponseEntity.ok(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 200,
                    "message", "Email подтвержден успешно! Теперь вы можете войти в систему."));
        } catch (ResponseStatusException ex) {
            log.warn("Ошибка при подтверждении email токеном {}: {}", token, ex.getReason());
            throw ex;
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация", description = "Авторизация пользователя и получение JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   BindingResult result, HttpServletRequest httpRequest) {
        String clientIP = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        log.info("Попытка входа пользователя: {} с IP: {}, User-Agent: {}", request.getEmail(), clientIP, userAgent);

        if (result.hasErrors()) {
            log.warn("Ошибка валидации при входе пользователя {}: {} ошибок",
                    request.getEmail(), result.getErrorCount());
            failedLogins.increment();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed");
        }

        try {
            var response = authService.login(request);
            log.info("Пользователь {} успешно вошел в систему с IP: {}", request.getEmail(), clientIP);
            successfulLogins.increment();
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            log.warn("Неуспешная попытка входа пользователя {} с IP {}: {}", request.getEmail(), clientIP, ex.getReason());
            failedLogins.increment();
            throw ex;
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление токена", description = "Выдаёт новый JWT по refresh токену")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest request, HttpServletRequest httpRequest) {
        String clientIP = httpRequest.getRemoteAddr();
        log.debug("Попытка обновления токена с IP: {}", clientIP);

        try {
            RefreshToken token = refreshTokenService.validate(request.getRefreshToken());
            String newAccessToken = jwtService.generateToken(token.getUser());
            log.info("Токен успешно обновлен для пользователя {} с IP {}", token.getUser().getEmail(), clientIP);
            tokenRefreshes.increment();
            return ResponseEntity.ok(new RefreshResponse(newAccessToken));
        } catch (RuntimeException ex) {
            log.warn("Ошибка при обновлении токена с IP {}: {}", clientIP, ex.getMessage());
            failedTokenRefreshes.increment();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход пользователя", description = "Удаляет refresh токены пользователя")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();

        try {
            String token = jwtService.extractTokenFromHeader(request);
            String email = jwtService.extractUsername(token);

            User user = authService.getUserByEmail(email);
            refreshTokenService.deleteByUserId(user.getId());

            log.info("Пользователь {} успешно вышел из системы с IP {}", email, clientIP);
            successfulLogouts.increment();

            return ResponseEntity.ok(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "message", "Logout successful"));
        } catch (Exception ex) {
            log.error("Ошибка при выходе пользователя с IP {}: {}", clientIP, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Logout failed: " + ex.getMessage());
        }
    }
}
