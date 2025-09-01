package com.bumbac.modules.admin.service;

import com.bumbac.modules.admin.dto.UserAdminDTO;
import com.bumbac.shared.enums.UserRole;
import com.bumbac.modules.auth.entity.Role;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.RoleRepository;
import com.bumbac.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public List<UserAdminDTO> getAllUsers() {
    log.debug("Получение списка всех пользователей");
    List<User> users = userRepository.findAll();
    return users.stream()
        .map(this::toUserAdminDTO)
        .toList();
  }

  @Transactional
  public void deleteUser(Long id) {
    log.info("Попытка удаления пользователя с ID={}", id);

    if (!userRepository.existsById(id)) {
      log.warn("Попытка удалить несуществующего пользователя с ID={}", id);
      throw new IllegalArgumentException("Пользователь не найден");
    }

    userRepository.deleteById(id);
    log.info("Пользователь с ID={} успешно удалён", id);
  }

  @Transactional
  public UserAdminDTO updateRole(Long userId, String roleCode) {
    log.info("Попытка обновления роли пользователя ID={} на роль '{}'", userId, roleCode);

    // Валидация роли через enum
    if (!UserRole.isValidRole(roleCode)) {
      log.error("Некорректный код роли: {}", roleCode);
      throw new IllegalArgumentException("Некорректный код роли: " + roleCode);
    }

    // Проверка существования пользователя
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("Пользователь с ID={} не найден", userId);
          return new IllegalArgumentException("Пользователь не найден");
        });

    // Проверка существования роли в базе данных
    Role role = roleRepository.findByCode(roleCode.toUpperCase())
        .orElseThrow(() -> {
          log.error("Роль '{}' не найдена в базе данных", roleCode);
          return new IllegalArgumentException("Роль не найдена: " + roleCode);
        });

    // Проверка, что пользователь не пытается изменить свою роль на ту же самую
    if (user.getRoles().stream().anyMatch(r -> r.getCode().equals(role.getCode()))) {
      log.warn("Пользователь ID={} уже имеет роль '{}'", userId, roleCode);
      throw new IllegalArgumentException("Пользователь уже имеет эту роль");
    }

    // Обновление роли
    user.setRoles(Set.of(role));
    userRepository.save(user);

    log.info("Роль пользователя ID={} успешно обновлена на '{}'", userId, role.getCode());

    // Возвращаем обновленного пользователя
    return toUserAdminDTO(user);
  }

  private UserAdminDTO toUserAdminDTO(User user) {
    return UserAdminDTO.builder()
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phone(user.getPhone())
        .createdAt(user.getCreatedAt())
        .roles(user.getRoleCodes())
        .build();
  }
}
