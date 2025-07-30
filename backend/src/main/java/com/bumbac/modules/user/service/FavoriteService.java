package com.bumbac.modules.user.service;

import com.bumbac.modules.catalog.entity.Yarn;
import com.bumbac.modules.catalog.repository.YarnRepository;
import com.bumbac.modules.user.dto.FavoriteDTO;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.entity.UserFavorite;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.user.mapper.FavoriteMapper;
import com.bumbac.modules.user.repository.FavoriteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;
    private final YarnRepository yarnRepository;
    private final UserRepository userRepository;

    public List<FavoriteDTO> getFavoritesForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return favoriteRepository.findByUser(user).stream()
                .map(favoriteMapper::toDto)
                .toList();
    }

    public void addToFavorites(Long userId, Long yarnId) {
        if (favoriteRepository.existsByUserIdAndYarnId(userId, yarnId)) return;

        User user = userRepository.findById(userId).orElseThrow();
        Yarn yarn = yarnRepository.findById(yarnId).orElseThrow();

        UserFavorite fav = UserFavorite.builder()
                .user(user)
                .yarn(yarn)
                .addedAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(fav);
    }

    public void removeFromFavorites(Long userId, Long yarnId) {
        favoriteRepository.deleteByUserIdAndYarnId(userId, yarnId);
    }
}
