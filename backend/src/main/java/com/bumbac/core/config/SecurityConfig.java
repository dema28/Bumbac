package com.bumbac.core.config;

import com.bumbac.core.security.CustomAccessDeniedHandler;
import com.bumbac.modules.auth.security.CustomUserDetailsService;
import com.bumbac.modules.auth.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\n" +
                                    "  \"status\": 403,\n" +
                                    "  \"error\": \"Forbidden\",\n" +
                                    "  \"message\": \"Authentication required\",\n" +
                                    "  \"path\": \"" + request.getRequestURI() + "\"\n" +
                                    "}");
                        })
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .userDetailsService(userDetailsService)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/**",        // ✅ ИСПРАВЛЕНО - добавлен префикс /api
                                "/swagger-ui/**",
                                "/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api/newsletter/**",  // ✅ ИСПРАВЛЕНО - добавлен префикс /api
                                "/api/catalog/**",     // ✅ ИСПРАВЛЕНО - добавлен префикс /api
                                "/api/contact/**",     // ✅ ИСПРАВЛЕНО - добавлен префикс /api
                                "/api/yarns/**",       // ✅ ИСПРАВЛЕНО - добавлен префикс /api
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        .requestMatchers("/actuator/metrics", "/actuator/prometheus").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
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