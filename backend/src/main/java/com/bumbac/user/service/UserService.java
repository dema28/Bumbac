package com.bumbac.user.service;

import com.bumbac.auth.entity.User;
import com.bumbac.auth.repository.UserRepository;
import com.bumbac.auth.security.JwtService;
import com.bumbac.user.dto.UpdateUserDto;
import com.bumbac.user.dto.UserProfileRequest;
import com.bumbac.user.dto.UserProfileResponse;
import com.bumbac.user.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bumbac.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public User getCurrentUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateUserProfile(String username, UpdateUserDto dto) {
        User user = getCurrentUser(username);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        userRepository.save(user);
    }

    public void changePassword(String username, String newPassword) {
        User user = getCurrentUser(username);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserProfileResponse getProfile(HttpServletRequest request) {
        String username = jwtService.extractUsernameFromHeader(request);
        User user = getCurrentUser(username);
        return userMapper.toDto(user);
    }

    public UserProfileResponse updateProfile(HttpServletRequest request, UserProfileRequest dto) {
        String username = jwtService.extractUsernameFromHeader(request);
        User user = getCurrentUser(username);
        userMapper.update(user, dto);
        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserProfileResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
