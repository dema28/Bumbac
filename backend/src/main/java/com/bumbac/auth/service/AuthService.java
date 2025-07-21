package com.bumbac.auth.service;

import com.bumbac.auth.dto.*;
import com.bumbac.auth.entity.Role;
import com.bumbac.auth.entity.User;
import com.bumbac.auth.repository.RoleRepository;
import com.bumbac.auth.repository.UserRepository;
import com.bumbac.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("Email already in use");
    }

    String roleCode = request.getRole() != null ? request.getRole().toUpperCase() : "USER";
    Role userRole = roleRepository.findByCode(roleCode)
            .orElseThrow(() -> new RuntimeException("Role " + roleCode + " not found"));

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

    String token = jwtService.generateToken(user); // ✅ фикс
    return new AuthResponse(token);
  }

  public AuthResponse login(LoginRequest request) {
    authManager.authenticate(new UsernamePasswordAuthenticationToken(
            request.getEmail(), request.getPassword()));

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    String token = jwtService.generateToken(user); // ✅ фикс
    return new AuthResponse(token);
  }


}
