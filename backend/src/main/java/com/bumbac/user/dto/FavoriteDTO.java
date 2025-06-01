package com.bumbac.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteDTO {
    private Long id;
    private Long yarnId;
    private String yarnName;
    private LocalDateTime addedAt;
}