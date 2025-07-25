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
public class RateLimitFilter implements Filter {

    // Храним лимиты на IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(1))))
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String ip = req.getRemoteAddr();

        // Применяем лимит только к /api/auth/register
        if (req.getRequestURI().equals("/api/auth/register") && req.getMethod().equalsIgnoreCase("POST")) {
            Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());

            if (bucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                HttpServletResponse res = (HttpServletResponse) response;
                res.setStatus(429); // Too Many Requests
                res.setContentType("application/json");
                res.getWriter().write("""
                    {
                      "status": 429,
                      "error": "Too Many Requests",
                      "message": "You have exceeded the limit of 3 registrations per minute"
                    }
                    """);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
