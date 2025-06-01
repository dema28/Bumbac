package com.bumbac.user.controller;

import com.bumbac.user.dto.FavoriteDTO;
import com.bumbac.user.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/{userId}")
    public List<FavoriteDTO> getFavorites(@PathVariable Long userId) {
        return favoriteService.getFavoritesForUser(userId);
    }

    @PostMapping
    public void addToFavorites(@RequestParam Long userId, @RequestParam Long yarnId) {
        favoriteService.addToFavorites(userId, yarnId);
    }

    @DeleteMapping
    public void removeFromFavorites(@RequestParam Long userId, @RequestParam Long yarnId) {
        favoriteService.removeFromFavorites(userId, yarnId);
    }
}