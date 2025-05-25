package md.bumbac.api.controller;

import lombok.RequiredArgsConstructor;
import md.bumbac.api.dto.NotificationSettingsDTO;
import md.bumbac.api.model.User;
import md.bumbac.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // --- профиль ------------------------------------------------------------

    @GetMapping("/me")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody User updates) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(user -> {
                    user.setFullName(updates.getFullName());
                    if (updates.getPassword() != null && !updates.getPassword().isBlank()) {
                        user.setPassword(updates.getPassword());     // в реальном коде зашифруйте!
                    }
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- настройки уведомлений ---------------------------------------------

    @PutMapping("/me/notifications")
    public ResponseEntity<?> updateNotifications(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody NotificationSettingsDTO dto) {

        return userRepository.findByEmail(userDetails.getUsername())
                .map(user -> {
                    user.setNotifyByEmail(dto.isNotifyByEmail());
                    user.setNotifyBySms(dto.isNotifyBySms());
                    user.setNotifyByPush(dto.isNotifyByPush());
                    userRepository.save(user);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
