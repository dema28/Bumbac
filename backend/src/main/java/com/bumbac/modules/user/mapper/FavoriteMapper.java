package com.bumbac.modules.user.mapper;

import com.bumbac.modules.user.dto.FavoriteDTO;
import com.bumbac.modules.auth.entity.UserFavorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public FavoriteDTO toDto(UserFavorite fav) {
        return FavoriteDTO.builder()
                .yarnId(fav.getYarn().getId())
                .yarnName(fav.getYarn().getName())
                .addedAt(fav.getAddedAt())
                .build();
    }
}
