package com.bumbac.modules.auth.service;

import com.bumbac.modules.auth.dto.AuthResponse;
import com.bumbac.modules.auth.dto.LoginRequest;
import com.bumbac.modules.auth.dto.RegisterRequest;
import com.bumbac.modules.auth.entity.Role;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.RoleRepository;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authManager;
  private final RoleRepository roleRepository;
  private final RefreshTokenService refreshTokenService;

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    log.info("Начало процесса регистрации для email: {}", request.getEmail());

    // Проверка существования email
    if (userRepository.existsByEmail(request.getEmail())) {
      log.warn("Попытка регистрации с уже существующим email: {}", request.getEmail());
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
    }

    // Проверка существования телефона
    if (userRepository.existsByPhone(request.getPhone())) {
      log.warn("Попытка регистрации с уже существующим телефоном: {}", request.getPhone());
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Phone already in use. Would you like to restore your account?");
    }

    // Поиск роли USER
    Role userRole = roleRepository.findByCode("USER")
        .orElseThrow(() -> {
          log.error("Роль USER не найдена в системе");
          return new RuntimeException("Default role USER not found");
        });

    try {
      // Создание пользователя
      User user = User.builder()
          .email(request.getEmail().toLowerCase().trim())
          .passwordHash(passwordEncoder.encode(request.getPassword()))
          .firstName(request.getFirstName().trim())
          .lastName(request.getLastName().trim())
          .phone(request.getPhone().trim())
          .roles(Set.of(userRole))
          .createdAt(LocalDateTime.now())
          .build();

      User savedUser = userRepository.save(user);
      log.info("Пользователь {} успешно создан с ID: {}", savedUser.getEmail(), savedUser.getId());

      // Генерация токенов
      String accessToken = jwtService.generateToken(savedUser);
      log.debug("JWT токен сгенерирован для нового пользователя: {}", savedUser.getEmail());

      return new AuthResponse(accessToken, null); // refresh не нужен при регистрации
    } catch (Exception e) {
      log.error("Ошибка при создании пользователя {}: {}", request.getEmail(), e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user");
    }
  }

  public AuthResponse login(LoginRequest request) {
    String email = request.getEmail().toLowerCase().trim();
    log.info("Начало процесса аутентификации для email: {}", email);

    try {
      // Аутентификация через Spring Security
      authManager.authenticate(
          new UsernamePasswordAuthenticationToken(email, request.getPassword()));
      log.debug("Аутентификация Spring Security прошла успешно для: {}", email);
    } catch (DisabledException ex) {
      log.warn("Попытка входа заблокированного пользователя: {}", email);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
    } catch (LockedException ex) {
      log.warn("Попытка входа заблокированного пользователя: {}", email);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is locked");
    } catch (BadCredentialsException ex) {
      log.warn("Неверные учетные данные для пользователя: {}", email);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    } catch (AuthenticationException ex) {
      log.warn("Ошибка аутентификации для пользователя {}: {}", email, ex.getMessage());
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
    }

    // Получение пользователя из БД
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.error("Пользователь {} не найден после успешной аутентификации", email);
          return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        });

    try {
      // Генерация токенов
      String accessToken = jwtService.generateToken(user);
      String refreshToken = refreshTokenService.create(user).getToken();

      log.info("Токены успешно сгенерированы для пользователя: {}", email);
      log.debug("Пользователь {} успешно вошел в систему", email);

      return new AuthResponse(accessToken, refreshToken);
    } catch (Exception e) {
      log.error("Ошибка при генерации токенов для пользователя {}: {}", email, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate tokens");
    }
  }

  public User getUserByEmail(String email) {
    String normalizedEmail = email.toLowerCase().trim();
    log.debug("Поиск пользователя по email: {}", normalizedEmail);

    return userRepository.findByEmail(normalizedEmail)
        .orElseThrow(() -> {
          log.warn("Пользователь с email {} не найден", normalizedEmail);
          return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        });
  }
}
