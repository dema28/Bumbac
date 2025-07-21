package com.bumbac.auth.security;

import com.bumbac.auth.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        String email = jwtService.extractUsernameFromHeader(http);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                var user = userOpt.get();

                // Извлекаем роли в виде строк (например: ADMIN, USER)
                var roleCodes = user.getRoles().stream()
                        .map(role -> role.getCode())
                        .collect(Collectors.toList());

                // Создаём UserDetails с ролями
                var userDetails = new UserDetailsImpl(
                        user.getEmail(),
                        user.getPasswordHash(),
                        roleCodes
                );

                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // ✅ теперь включает ROLE_ADMIN и другие
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(http));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}
