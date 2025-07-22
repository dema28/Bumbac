package com.bumbac.admin.dto;

import com.bumbac.auth.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAdminDTO {

  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private List<String> roles;
}
