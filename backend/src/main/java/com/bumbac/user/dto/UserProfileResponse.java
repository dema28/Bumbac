package com.bumbac.user.dto;

import com.bumbac.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserProfileResponse {
  private Long id;
  private String email;
  private List<String> roles;
  private String firstName;
  private String lastName;
  private String phone;
}
