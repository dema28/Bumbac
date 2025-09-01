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
public class RateLimitFilter implements Filter {

  // Храним лимиты на IP
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Value("${app.security.rate-limit.requests-per-minute:3}")
  private int requestsPerMinute;

  @Value("${app.security.rate-limit.burst-capacity:5}")
  private int burstCapacity;

  private Bucket createBucket() {
    return Bucket4j.builder()
        .addLimit(Bandwidth.classic(requestsPerMinute, Refill.greedy(requestsPerMinute, Duration.ofMinutes(1))))
        .addLimit(Bandwidth.classic(burstCapacity, Refill.greedy(burstCapacity, Duration.ofMinutes(1))))
        .build();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    String ip = req.getRemoteAddr();
    String uri = req.getRequestURI();

    // Применяем лимит только к /api/auth/register
    if (uri.equals("/api/auth/register") && req.getMethod().equalsIgnoreCase("POST")) {
      Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());

      if (bucket.tryConsume(1)) {
        log.debug("Rate limit check passed for IP: {} on URI: {}", ip, uri);
        chain.doFilter(request, response);
      } else {
        log.warn("Rate limit exceeded for IP: {} on URI: {}. Limit: {} requests per minute",
            ip, uri, requestsPerMinute);

        HttpServletResponse res = (HttpServletResponse) response;
        res.setStatus(429); // Too Many Requests
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String errorBody = """
            {
              "timestamp": "%s",
              "status": 429,
              "error": "Too Many Requests",
              "message": "You have exceeded the limit of %d registrations per minute. Please try again later.",
              "path": "%s"
            }
            """.formatted(
            java.time.LocalDateTime.now(),
            requestsPerMinute,
            uri);

        res.getWriter().write(errorBody);
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    log.info("RateLimitFilter initialized with {} requests per minute, burst capacity: {}",
        requestsPerMinute, burstCapacity);
  }

  @Override
  public void destroy() {
    log.info("RateLimitFilter destroyed");
    buckets.clear();
  }
}
