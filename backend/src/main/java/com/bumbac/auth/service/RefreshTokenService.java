package com.bumbac.auth.service;

import com.bumbac.auth.entity.RefreshToken;
import com.bumbac.auth.entity.User;
import com.bumbac.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;

    public RefreshToken create(User user) {
        return refreshTokenRepo.save(
                RefreshToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiry(LocalDateTime.now().plusDays(7))
                        .build()
        );
    }

    public RefreshToken validate(String token) {
        return refreshTokenRepo.findByToken(token)
                .filter(t -> t.getExpiry().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("Invalid or expired refresh token"));
    }
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepo.deleteByUserId(userId);
    }
}
