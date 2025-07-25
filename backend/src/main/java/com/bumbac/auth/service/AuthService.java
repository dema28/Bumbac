package com.bumbac.auth.service;

import com.bumbac.auth.dto.*;
import com.bumbac.auth.entity.Role;
import com.bumbac.auth.entity.User;
import com.bumbac.auth.repository.RoleRepository;
import com.bumbac.auth.repository.UserRepository;
import com.bumbac.auth.security.JwtService;
import com.bumbac.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.AuthenticationException;


import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authManager;
  private final RoleRepository roleRepository;
  private final RefreshTokenService refreshTokenService;

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ResponseStatusException(
              HttpStatus.CONFLICT, "Email already in use"
      );
    }

    Role userRole = roleRepository.findByCode("USER")
            .orElseThrow(() -> new RuntimeException("Default role USER not found"));

    User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phone(request.getPhone())
            .roles(Set.of(userRole))
            .createdAt(LocalDateTime.now())
            .build();

    userRepository.save(user);

    String token = jwtService.generateToken(user);
    return new AuthResponse(token, null); // refresh не нужен при регистрации
  }

  public AuthResponse login(LoginRequest request) {
    try {
      authManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
    try {
      authManager.authenticate(new UsernamePasswordAuthenticationToken(
              request.getEmail(), request.getPassword()));
    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    String accessToken = jwtService.generateToken(user);
    String refreshToken = refreshTokenService.create(user).getToken();

    return new AuthResponse(accessToken, refreshToken);
  }
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));


  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

}
