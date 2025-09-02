package com.bumbac.core.security;

import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class LoginRateLimitFilter implements Filter {

  private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();

  @Value("${app.security.rate-limit.login-attempts-per-minute:5}")
  private int loginAttemptsPerMinute;

  @Value("${app.security.rate-limit.login-burst-capacity:8}")
  private int loginBurstCapacity;

  private Bucket createLoginBucket() {
    return Bucket4j.builder()
        .addLimit(
            Bandwidth.classic(loginAttemptsPerMinute, Refill.greedy(loginAttemptsPerMinute, Duration.ofMinutes(1))))
        .addLimit(Bandwidth.classic(loginBurstCapacity, Refill.greedy(loginBurstCapacity, Duration.ofMinutes(1))))
        .build();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;

    String ip = req.getRemoteAddr();
    String uri = req.getRequestURI();
    String method = req.getMethod();

    if (uri.equals("/api/auth/login") && method.equalsIgnoreCase("POST")) {
      Bucket bucket = loginBuckets.computeIfAbsent(ip, k -> createLoginBucket());

      if (bucket.tryConsume(1)) {
        log.debug("Login rate limit check passed for IP: {} on URI: {}", ip, uri);
        chain.doFilter(request, response);
      } else {
        log.warn("Login rate limit exceeded for IP: {} on URI: {}. Limit: {} attempts per minute",
            ip, uri, loginAttemptsPerMinute);

        HttpServletResponse res = (HttpServletResponse) response;
        res.setStatus(429);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String errorBody = """
            {
              "timestamp": "%s",
              "status": 429,
              "error": "Too Many Requests",
              "message": "You have exceeded the limit of %d login attempts per minute. Please try again later.",
              "path": "%s"
            }
            """.formatted(
            java.time.LocalDateTime.now(),
            loginAttemptsPerMinute,
            uri);

        res.getWriter().write(errorBody);
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    log.info("LoginRateLimitFilter initialized with {} login attempts per minute, burst capacity: {}",
        loginAttemptsPerMinute, loginBurstCapacity);
  }

  @Override
  public void destroy() {
    log.info("LoginRateLimitFilter destroyed");
    loginBuckets.clear();
  }
}
