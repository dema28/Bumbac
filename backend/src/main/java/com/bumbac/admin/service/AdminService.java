package com.bumbac.admin.service;

import com.bumbac.admin.dto.UserAdminDTO;
import com.bumbac.auth.entity.Role;
import com.bumbac.auth.entity.User;
import com.bumbac.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserAdminDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserAdminDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole())
                        .build())
                .toList();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void updateRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.valueOf(role.toUpperCase())); // пример: ADMIN, USER
        userRepository.save(user);
    }
}
