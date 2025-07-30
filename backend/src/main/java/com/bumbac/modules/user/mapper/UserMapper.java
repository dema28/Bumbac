package com.bumbac.modules.user.mapper;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.user.dto.UserProfileRequest;
import com.bumbac.modules.user.dto.UserProfileResponse;
import org.springframework.stereotype.Component;

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
        .build();
  }

  public void update(User user, UserProfileRequest request) {
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPhone(request.getPhone());
  }
}
