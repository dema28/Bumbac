package com.bumbac.modules.auth.service;

import com.bumbac.modules.auth.dto.AuthResponse;
import com.bumbac.modules.auth.dto.LoginRequest;
import com.bumbac.modules.auth.dto.RegisterRequest;
import com.bumbac.modules.auth.entity.Role;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.entity.EmailVerificationToken;
import com.bumbac.modules.auth.repository.EmailVerificationTokenRepository;
import com.bumbac.modules.auth.repository.RoleRepository;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.auth.security.JwtService;
import com.bumbac.modules.email.EmailService;
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
import java.util.UUID;

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
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Начало процесса регистрации для email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Phone already in use. Would you like to restore your account?");
        }

        Role userRole = roleRepository.findByCode("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));

        try {
            User user = User.builder()
                    .email(request.getEmail().toLowerCase().trim())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName().trim())
                    .lastName(request.getLastName().trim())
                    .phone(request.getPhone().trim())
                    .roles(Set.of(userRole))
                    .createdAt(LocalDateTime.now())
                    .emailVerified(false) // 🚨 новый пользователь ещё не подтвержден
                    .build();

            User savedUser = userRepository.save(user);

            // Генерация и сохранение токена подтверждения
            String token = UUID.randomUUID().toString();
            EmailVerificationToken verificationToken = new EmailVerificationToken(
                    token,
                    LocalDateTime.now().plusHours(24),
                    savedUser
            );
            tokenRepository.save(verificationToken);


            // Отправка письма
            String verificationUrl = "https://qscfgrt657.duckdns.org/api/auth/verify?token=" + token;
            emailService.sendEmail(
                    savedUser.getEmail(),
                    "Подтверждение email для Bumbac.md",
                    "Здравствуйте, " + savedUser.getFirstName() + "!\n\n" +
                            "Пожалуйста, подтвердите ваш email, перейдя по ссылке:\n" +
                            verificationUrl + "\n\n" +
                            "Срок действия ссылки: 24 часа."
            );

            log.info("Пользователь {} зарегистрирован. Email подтверждение отправлено.", savedUser.getEmail());

            // 🚨 Токены не выдаем пока email не подтвержден
            return new AuthResponse(null, null);

        } catch (Exception e) {
            log.error("Ошибка при создании пользователя {}: {}", request.getEmail(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user");
        }
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (DisabledException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
        } catch (LockedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is locked");
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email not verified");
        }

        try {
            String accessToken = jwtService.generateToken(user);
            String refreshToken = refreshTokenService.create(user).getToken();
            return new AuthResponse(accessToken, refreshToken);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate tokens");
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
        log.info("Email {} успешно подтвержден", user.getEmail());
    }
}
