package com.bumbac.modules.admin.service;

import com.bumbac.modules.admin.dto.UserAdminDTO;
import com.bumbac.modules.auth.entity.Role;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.RoleRepository;
import com.bumbac.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public List<UserAdminDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream()
            .map(user -> UserAdminDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .roles(user.getRoleCodes()) // предполагается метод getRoleCodes() → List<String>
                    .build())
            .toList();
  }

  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      log.warn("Попытка удалить несуществующего пользователя с ID={}", id);
      throw new IllegalArgumentException("Пользователь не найден");
    }
    userRepository.deleteById(id);
    log.info("Пользователь с ID={} успешно удалён", id);
  }

  public void updateRole(Long userId, String roleCode) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> {
              log.error("Пользователь с ID={} не найден", userId);
              return new IllegalArgumentException("Пользователь не найден");
            });

    Role role = roleRepository.findByCode(roleCode.toUpperCase())
            .orElseThrow(() -> {
              log.error("Роль '{}' не найдена", roleCode);
              return new IllegalArgumentException("Роль не найдена: " + roleCode);
            });

    user.setRoles(Set.of(role));
    userRepository.save(user);

    log.info("Роль пользователя ID={} успешно обновлена на '{}'", userId, role.getCode());
  }
}
