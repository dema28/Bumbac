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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
                        // CORS preflight
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger + Actuator (health/info) — открыты
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/prometheus"
                        ).permitAll()

                        // Публичные GET каталога (контроллеры с /api, контекст-путь тоже /api → внутренний путь /api/…)
                        .requestMatchers(org.springframework.http.HttpMethod.GET,
                                "/api/yarns/**",
                                "/api/categories/**",
                                "/api/collections/**",
                                "/api/brands/**",
                                "/api/colors/**",
                                "/api/patterns/**",
                                "/api/media/**",
                                "/api/shipments/methods"
                        ).permitAll()

                        // Аутентификация/рассылка/контакты — открыты
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/newsletter/**",
                                "/api/contact/**"
                        ).permitAll()

                        // Всё остальное — по токену (ДОЛЖНО быть последним и в единственном числе)
                        .anyRequest().authenticated()
                )
                // Регистрируем наш DaoAuthenticationProvider (иначе может взяться дефолтный без BCrypt)
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

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger-resources/**"
        );
    }
}