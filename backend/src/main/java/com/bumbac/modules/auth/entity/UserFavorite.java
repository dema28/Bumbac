package com.bumbac.modules.auth.entity;

import com.bumbac.modules.catalog.entity.Yarn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_favorites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavorite {

    @EmbeddedId
    private UserFavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("yarn")
    @JoinColumn(name = "yarn_id")
    @JsonIgnore
    private Yarn yarn;

    private LocalDateTime addedAt;
}

