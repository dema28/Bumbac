package com.bumbac.core.config;

import com.bumbac.core.security.CustomAccessDeniedHandler;
import com.bumbac.modules.auth.security.CustomUserDetailsService;
import com.bumbac.modules.auth.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // ⬅️
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            CustomUserDetailsService userDetailsService,
            CustomAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // ✅ Явный провайдер аутентификации с твоим UserDetailsService и BCrypt
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // email -> UserDetailsImpl
        provider.setPasswordEncoder(passwordEncoder);       // BCrypt сверка хэша
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setHeader("WWW-Authenticate", "Bearer realm=\"api\"");

                            String body = """
                            {
                              "status": 401,
                              "error": "Unauthorized",
                              "message": "Authentication required",
                              "path": "%s"
                            }
                            """.formatted(request.getRequestURI());

                            response.getWriter().write(body);
                        })
                        .accessDeniedHandler(accessDeniedHandler) // 403 для «недостаточно прав»
                )
                .userDetailsService(userDetailsService)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // ✅ УБРАЛИ /api/ префикс - nginx его удаляет перед отправкой в Spring
                                "/auth/**",           // для /api/auth/**
                                "/swagger-ui/**",     // для /api/swagger-ui/**
                                "/api-docs",          // для /api/api-docs
                                "/v3/api-docs/**",    // для /api/v3/api-docs/**
                                "/swagger-ui.html",   // для /api/swagger-ui.html
                                "/newsletter/**",     // для /api/newsletter/**
                                "/catalog/**",        // для /api/catalog/**
                                "/contact/**",        // для /api/contact/**
                                "/yarns/**",          // для /api/yarns/**
                                "/actuator/health",   // для /api/actuator/health
                                "/actuator/info",     // для /api/actuator/info
                                "/actuator/prometheus" // для /api/actuator/prometheus
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // для /api/admin/**
                        .anyRequest().authenticated()
                )
                // ⬇️ Регистрируем наш DaoAuthenticationProvider (иначе может взяться дефолтный без BCrypt)
                .authenticationProvider(authenticationProvider(passwordEncoder()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}