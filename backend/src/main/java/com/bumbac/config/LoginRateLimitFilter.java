package com.bumbac.config;

import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimitFilter implements Filter {

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();

    private Bucket createLoginBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
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
                chain.doFilter(request, response);
            } else {
                HttpServletResponse res = (HttpServletResponse) response;
                res.setStatus(429);
                res.setContentType("application/json");
                res.getWriter().write("""
                    {
                      "status": 429,
                      "error": "Too Many Requests",
                      "message": "You have exceeded the limit of 5 login attempts per minute"
                    }
                    """);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
