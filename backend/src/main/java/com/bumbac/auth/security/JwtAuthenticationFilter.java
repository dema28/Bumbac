package com.bumbac.auth.security;

import com.bumbac.auth.entity.Role;
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
        HttpServletResponse httpResp = (HttpServletResponse) response;

        try {
            String email = jwtService.extractUsernameFromHeader(http);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userOpt = userRepository.findByEmail(email);

                if (userOpt.isPresent()) {
                    var user = userOpt.get();

                    var roleCodes = user.getRoles().stream()
                            .map(Role::getCode)
                            .collect(Collectors.toList());

                    var userDetails = new UserDetailsImpl(user);

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(http));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            chain.doFilter(request, response);

        } catch (Exception ex) {
            httpResp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResp.setContentType("application/json");
            httpResp.getWriter().write("""
        {
          "status": 403,
          "error": "Forbidden",
          "message": "Authentication required",
          "path": "%s"
        }
        """.formatted(http.getRequestURI()));
        }
    }
}
