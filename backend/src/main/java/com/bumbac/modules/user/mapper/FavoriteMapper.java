package com.bumbac.modules.user.mapper;

import com.bumbac.modules.user.dto.FavoriteDTO;
import com.bumbac.modules.user.entity.UserFavorite;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FavoriteMapper {

    public FavoriteDTO toDto(UserFavorite fav) {
        return FavoriteDTO.builder()
                .yarnId(fav.getYarn().getId())
                .yarnName(fav.getYarn().getName())
                .addedAt(fav.getAddedAt())
                .build();
    }

    // Добавляем метод для преобразования списка
    public List<FavoriteDTO> toDtoList(List<UserFavorite> favorites) {
        return favorites.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}