package com.bumbac.admin.service;

import com.bumbac.admin.dto.UserAdminDTO;
import com.bumbac.auth.entity.Role;
import com.bumbac.auth.entity.User;
import com.bumbac.auth.repository.RoleRepository;
import com.bumbac.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public List<UserAdminDTO> getAllUsers() {
    return userRepository.findAll().stream()
        .map(user -> UserAdminDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .roles(user.getRoleCodes())
            .build())
        .toList();
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public void updateRole(Long userId, String role) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    Role roleEntity = roleRepository.findByCode(role.toUpperCase())
        .orElseThrow(() -> new RuntimeException("Role not found: " + role));
    user.setRoles(Set.of(roleEntity));
    userRepository.save(user);
  }
}
