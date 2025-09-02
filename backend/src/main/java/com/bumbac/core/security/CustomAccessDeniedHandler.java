package com.bumbac.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException ex) throws IOException {

    String uri = request.getRequestURI();
    String method = request.getMethod();
    String userAgent = request.getHeader("User-Agent");
    String remoteAddr = request.getRemoteAddr();

    log.warn("Access denied for {} {} from IP: {}, User-Agent: {}. Reason: {}",
        method, uri, remoteAddr, userAgent, ex.getMessage());

    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String body = """
        {
          "timestamp": "%s",
          "status": 403,
          "error": "Forbidden",
          "message": "Access is denied. You don't have permission to access this resource.",
          "path": "%s",
          "method": "%s"
        }
        """.formatted(
        java.time.LocalDateTime.now(),
        uri,
        method);

    response.getWriter().write(body);
  }
}
