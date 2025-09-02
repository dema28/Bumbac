
package com.bumbac.modules.auth.service;

import com.bumbac.modules.auth.dto.LoginRequest;
import com.bumbac.modules.auth.dto.AuthResponse;
import com.bumbac.modules.auth.dto.RegisterRequest;
import com.bumbac.modules.auth.entity.RefreshToken;
import com.bumbac.modules.auth.entity.Role;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.RoleRepository;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.auth.security.JwtService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;


    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                authManager,             // ✅ authManager — 4-й
                roleRepository,          // ✅ roleRepository — 5-й
                refreshTokenService      // ✅ refreshTokenService — 6-й
        );
    }


    @Test
    public void testRegisterSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phone("+37360000001")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        Role userRole = Role.builder().id(1L).code("USER").name("User").build();
        when(roleRepository.findByCode("USER")).thenReturn(Optional.of(userRole));

        // Мокаем сохранение пользователя
        User savedUser = User.builder()
                .id(1L)
                .email(request.getEmail())
                .passwordHash("hashedPassword")
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .roles(Set.of(userRole))
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(jwtService.generateToken(any(User.class))).thenReturn("mocked-access-token");
        when(refreshTokenService.create(any(User.class))).thenReturn(
                RefreshToken.builder()
                        .token("mocked-refresh-token")
                        .expiry(LocalDateTime.now().plusDays(7))
                        .build()
        );

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(response.getAccessToken(), "mocked-access-token");
        assertNull(response.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    public void testLoginSuccess() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .roles(Set.of(Role.builder().id(1L).code("USER").name("User").build()))
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(authManager.authenticate(any(Authentication.class))).thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(any(User.class))).thenReturn("mocked-access-token");
        when(refreshTokenService.create(any(User.class))).thenReturn(
                RefreshToken.builder()
                        .token("mocked-refresh-token")
                        .expiry(LocalDateTime.now().plusDays(7))
                        .build()
        );

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(response.getAccessToken(), "mocked-access-token");
        assertEquals(response.getRefreshToken(), "mocked-refresh-token");
    }
}
