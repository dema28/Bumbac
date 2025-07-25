package com.bumbac.user.controller;

import com.bumbac.common.dto.ErrorResponse;
import com.bumbac.user.dto.FavoriteDTO;
import com.bumbac.user.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/{userId}")
    @Operation(summary = "Получить избранное", description = "Возвращает список избранной пряжи пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список избранного получен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<FavoriteDTO> getFavorites(@PathVariable Long userId) {
        return favoriteService.getFavoritesForUser(userId);
    }

    @PostMapping
    @Operation(summary = "Добавить в избранное", description = "Добавляет пряжу в список избранного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пряжа добавлена в избранное"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void addToFavorites(@RequestParam Long userId, @RequestParam Long yarnId) {
        favoriteService.addToFavorites(userId, yarnId);
    }

    @DeleteMapping
    @Operation(summary = "Удалить из избранного", description = "Удаляет пряжу из избранного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пряжа удалена из избранного"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void removeFromFavorites(@RequestParam Long userId, @RequestParam Long yarnId) {
        favoriteService.removeFromFavorites(userId, yarnId);
    }
}
