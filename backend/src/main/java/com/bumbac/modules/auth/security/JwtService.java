package com.bumbac.modules.auth.security;

import com.bumbac.modules.auth.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

  @Value("${JWT_SECRET:defaultSecretKeyForDevelopmentOnly}")
  private String jwtSecret;

  @Value("${JWT_EXPIRATION:86400000}")
  private long jwtExpiration;

  public String generateToken(User user) {
    try {
      String token = Jwts.builder()
          .setSubject(user.getEmail())
          .claim("userId", user.getId())
          .claim("roles", user.getRoles().stream()
              .map(role -> "ROLE_" + role.getCode())
              .toList())
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
          .signWith(getKey(), SignatureAlgorithm.HS256)
          .compact();

      log.debug("JWT токен сгенерирован для пользователя: {}", user.getEmail());
      return token;
    } catch (Exception e) {
      log.error("Ошибка при генерации JWT токена для пользователя {}: {}", user.getEmail(), e.getMessage());
      throw new RuntimeException("Failed to generate JWT token", e);
    }
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("JWT токен истек: {}", e.getMessage());
      return false;
    } catch (UnsupportedJwtException e) {
      log.warn("Неподдерживаемый JWT токен: {}", e.getMessage());
      return false;
    } catch (MalformedJwtException e) {
      log.warn("Некорректный JWT токен: {}", e.getMessage());
      return false;
    } catch (SecurityException e) {
      log.warn("Ошибка безопасности JWT токена: {}", e.getMessage());
      return false;
    } catch (IllegalArgumentException e) {
      log.warn("Некорректный аргумент JWT токена: {}", e.getMessage());
      return false;
    } catch (Exception e) {
      log.error("Неожиданная ошибка при валидации JWT токена: {}", e.getMessage());
      return false;
    }
  }

  public String extractUsername(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getKey())
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getSubject();
    } catch (ExpiredJwtException e) {
      log.warn("Попытка извлечь username из истекшего токена: {}", e.getMessage());
      throw new RuntimeException("JWT token expired", e);
    } catch (Exception e) {
      log.error("Ошибка при извлечении username из JWT токена: {}", e.getMessage());
      throw new RuntimeException("Failed to extract username from JWT", e);
    }
  }

  public Long extractUserId(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(getKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
      return claims.get("userId", Long.class);
    } catch (Exception e) {
      log.error("Ошибка при извлечении userId из JWT токена: {}", e.getMessage());
      return null;
    }
  }

  public Date extractExpiration(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getKey())
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getExpiration();
    } catch (Exception e) {
      log.error("Ошибка при извлечении expiration из JWT токена: {}", e.getMessage());
      return null;
    }
  }

  public boolean isTokenExpired(String token) {
    Date expiration = extractExpiration(token);
    return expiration != null && expiration.before(new Date());
  }

  public String extractUsernameFromHeader(HttpServletRequest request) {
    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return null;
      }
      String token = authHeader.substring(7);
      if (validateToken(token)) {
        return extractUsername(token);
      }
      return null;
    } catch (Exception e) {
      log.warn("Ошибка при извлечении username из заголовка: {}", e.getMessage());
      return null;
    }
  }

  private Key getKey() {
    if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
      log.error("JWT_SECRET не настроен, используется fallback ключ");
      jwtSecret = "defaultSecretKeyForDevelopmentOnly";
    }
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  public String extractTokenFromHeader(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (validateToken(token)) {
        return token;
      } else {
        log.warn("Невалидный JWT токен в заголовке Authorization");
        throw new RuntimeException("Invalid JWT token");
      }
    }
    throw new RuntimeException("Missing or invalid Authorization header");
  }
}
