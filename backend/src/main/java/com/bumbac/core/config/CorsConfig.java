package com.bumbac.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Разрешаем наш домен по HTTPS
        config.setAllowedOrigins(List.of(
                "https://qscfgrt657.duckdns.org",                    // production
                "http://qscfgrt657.duckdns.org",                     // production
                "https://staging-qscfgrt657.duckdns.org",           // ✅ ДОБАВИТЬ staging
                "http://staging-qscfgrt657.duckdns.org",            // ✅ ДОБАВИТЬ staging http
                "http://localhost:3000",                             // local
                "http://localhost:5173"                              // local dev
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
