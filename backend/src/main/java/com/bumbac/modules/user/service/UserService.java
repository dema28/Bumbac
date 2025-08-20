package com.bumbac.modules.user.service;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.auth.security.JwtService;
import com.bumbac.modules.user.dto.UpdateUserDto;
import com.bumbac.modules.user.dto.UserProfileRequest;
import com.bumbac.modules.user.dto.UserProfileResponse;
import com.bumbac.modules.user.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bumbac.core.exception.UserNotFoundException;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public void updateUserProfile(String username, UpdateUserDto dto) {
        User user = getCurrentUser(username);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        userRepository.save(user);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = getCurrentUser(username);

        // NOTE: в сущности поле может называться password / passwordHash.
        String currentHash = user.getPasswordHash(); // при необходимости замени на getPassword()

        if (!passwordEncoder.matches(oldPassword, currentHash)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword)); // или setPassword(...)
        userRepository.save(user);
    }


//    public void changePassword(String username, String newPassword) {
//        User user = getCurrentUser(username);
//        user.setPasswordHash(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//    }

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
