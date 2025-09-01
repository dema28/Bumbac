package com.bumbac.core.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
    cacheManager.setCacheNames(java.util.Arrays.asList(
        "users", // Кэш пользователей по email
        "userExists", // Кэш проверки существования email
        "phoneExists", // Кэш проверки существования телефона
        "roles" // Кэш ролей
    ));
    return cacheManager;
  }
}
