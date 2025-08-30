package com.bumbac.modules.auth.security;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * ВАЖНО: здесь мы решаем, какие запросы вообще НЕ фильтровать.
     * Мы отрезаем context-path (например, "/api"), чтобы матчить единообразно:
     *  - /api/v3/api-docs  ->  /v3/api-docs
     *  - /api/swagger-ui   ->  /swagger-ui
     *  - /api/actuator/... ->  /actuator/...
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();     // полный путь, например: /api/v3/api-docs
        String ctx  = request.getContextPath();    // контекст, например: /api
        if (ctx != null && !ctx.isEmpty() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());   // теперь без контекста: /v3/api-docs
        }

        return
                // CORS preflight
                "OPTIONS".equalsIgnoreCase(request.getMethod())
                        // Swagger/OpenAPI
                        || path.startsWith("/v3/api-docs")
                        || path.startsWith("/swagger-ui")
                        || path.equals("/swagger-ui.html")
                        || path.startsWith("/swagger-resources")
                        || path.startsWith("/webjars")
                        // Health (для LB/мониторинга)
                        || path.startsWith("/actuator/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Пропускаем preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // 1) Нет Bearer — не аутентифицируем, отдаём дальше (на защищённых путях вернётся 401 из entryPoint)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 2) Пытаемся извлечь email из заголовка через твой сервис
            String email = jwtService.extractUsernameFromHeader(request);
            if (email == null || email.isBlank()) {
                unauthorized(response, request.getRequestURI(), "Invalid token");
                return;
            }

            // Уже аутентифицирован — идём дальше
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(request, response);
                return;
            }

            // 3) Подтверждаем пользователя в БД
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                unauthorized(response, request.getRequestURI(), "Invalid token");
                return;
            }

            // (Опционально для логов/метрик) — сохраняем roleCodes в атрибут запроса
            List<String> roleCodes = user.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toList());
            request.setAttribute("roleCodes", roleCodes);

            // 4) Создаём аутентификацию
            var userDetails = new UserDetailsImpl(user);
            var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 5) Всё ок — дальше по цепочке
            chain.doFilter(request, response);

        } catch (Exception ex) {
            // Любая ошибка парсинга/валидности токена — 401
            SecurityContextHolder.clearContext();
            unauthorized(response, request.getRequestURI(), "Invalid or expired token");
        }
    }

    private void unauthorized(HttpServletResponse response, String path, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("WWW-Authenticate", "Bearer realm=\"api\"");
        String body = """
        {
          "status": 401,
          "error": "Unauthorized",
          "message": "%s",
          "path": "%s"
        }
        """.formatted(message, path);
        response.getWriter().write(body);
    }
}
