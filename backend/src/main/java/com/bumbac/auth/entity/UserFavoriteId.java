// UserFavoriteId.java
package com.bumbac.auth.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFavoriteId implements Serializable {
    private Long user;
    private Long yarn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFavoriteId that)) return false;
        return Objects.equals(user, that.user) && Objects.equals(yarn, that.yarn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, yarn);
    }
}
