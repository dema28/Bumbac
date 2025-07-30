package com.bumbac.modules.user.repository;

import com.bumbac.modules.auth.entity.UserFavorite;
import com.bumbac.modules.auth.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<UserFavorite, Long> {
    List<UserFavorite> findByUser(User user);
    boolean existsByUserIdAndYarnId(Long userId, Long yarnId);
    void deleteByUserIdAndYarnId(Long userId, Long yarnId);
}