package com.bumbac.modules.user.mapper;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.user.dto.UserProfileRequest;
import com.bumbac.modules.user.dto.UserProfileResponse;
import com.bumbac.modules.user.entity.UserFavorite;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

  public UserProfileResponse toDto(User user) {
    return UserProfileResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .roles(user.getRoleCodes())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phone(user.getPhone())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .favoritesCount(user.getFavorites() != null ? user.getFavorites().size() : 0)
        .ordersCount(0) // TODO: добавить подсчет заказов
        .build();
  }

  public UserProfileResponse toDtoWithFavorites(User user, List<UserFavorite> favorites) {
    return UserProfileResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .roles(user.getRoleCodes())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phone(user.getPhone())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .favoritesCount(favorites != null ? favorites.size() : 0)
        .ordersCount(0) // TODO: добавить подсчет заказов
        .build();
  }

  public void update(User user, UserProfileRequest request) {
    if (request.getFirstName() != null) {
      user.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastName(request.getLastName());
    }
    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }
  }

  public List<UserProfileResponse> toDtoList(List<User> users) {
    return users.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }
}
