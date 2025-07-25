package com.bumbac.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {

    private final RateLimitFilter rateLimitFilter;

    @Bean
    public FilterRegistrationBean<Filter> rateLimiterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimitFilter);
        registration.addUrlPatterns("/api/auth/register"); // Только на регистрацию
        registration.setOrder(0); // До Spring Security (важно!)
        return registration;
    }

    @Bean
    public FilterRegistrationBean<Filter> loginRateLimiterRegistration(LoginRateLimitFilter loginFilter) {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(loginFilter);
        registration.addUrlPatterns("/api/auth/login");
        registration.setOrder(2); // чуть позже, чем регистрация
        return registration;
    }

}
