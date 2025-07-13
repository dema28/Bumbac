package com.bumbac.user.controller;

import com.bumbac.auth.entity.User;
import com.bumbac.user.dto.UpdateUserDto;
import com.bumbac.user.dto.UserProfileRequest;
import com.bumbac.user.dto.UserProfileResponse;
import com.bumbac.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/profile")
  public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.getCurrentUser(userDetails.getUsername());
    var dto = new UserProfileResponse(
        user.getId(),
        user.getEmail(),
        user.getRoleCodes(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhone());
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/profile")
  public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
      @RequestBody UpdateUserDto dto) {
    userService.updateUserProfile(userDetails.getUsername(), dto);
    return ResponseEntity.ok("Profile updated");
  }

  @PutMapping("/password")
  public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails,
      @RequestParam String newPassword) {
    userService.changePassword(userDetails.getUsername(), newPassword);
    return ResponseEntity.ok("Password updated");
  }

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUserShort(@AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(userDetails.getUsername());
  }

  @GetMapping("/me/full")
  public ResponseEntity<UserProfileResponse> getProfile(HttpServletRequest request) {
    return ResponseEntity.ok(userService.getProfile(request));
  }

  @PutMapping("/me/full")
  public ResponseEntity<UserProfileResponse> updateProfile(
      HttpServletRequest request,
      @RequestBody UserProfileRequest updateRequest) {
    return ResponseEntity.ok(userService.updateProfile(request, updateRequest));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin/all")
  public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAll());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/admin/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
