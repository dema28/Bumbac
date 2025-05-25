package md.bumbac.api.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Утилитарный сервис для генерации и валидации JWT-токенов.
 */
@Service
public class JwtService {

    /** Секрет хранится во внешнем конфиге (application.yml) */
    private final Key signingKey;

    /** Время жизни токена (мс) */
    private final long jwtExpiration;

    public JwtService(
            @Value("${jwt.secret:MyVerySecretKeyForJwtTokenBumbac12345678901234567890}") String secret,
            @Value("${jwt.expiration:86400000}") long jwtExpiration) {          // 24 ч по умолч.
        this.signingKey   = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = jwtExpiration;
    }

    /* ======================  Создание токена  ====================== */

    public String generateToken(String username, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String username) {
        return generateToken(username, Map.of());
    }

    /* ======================  Парсинг токена  ====================== */

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims,T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // вставьте, например, под extractUsername(...)
    public String extractUserName(String token) {
        return extractUsername(token);   // alias для фильтра
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /* ======================  Валидация  ====================== */

    public boolean isTokenValid(String token, String expectedUsername) {
        try {
            String username = extractUsername(token);
            return username.equals(expectedUsername) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
