package md.bumbac.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.bumbac.api.dto.AuthRequestDTO;
import md.bumbac.api.dto.UserProfileDTO;
import md.bumbac.api.model.User;
import md.bumbac.api.service.JwtService;
import md.bumbac.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Аутентификация и регистрация.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    /* ----------- регистрация ------------------------------------------- */

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequestDTO dto) {
        User user = userService.registerUser(dto.getEmail(), dto.getPassword(), ""); // fullName пустой
        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "profile", new UserProfileDTO(user.getEmail(), user.getFullName(), user.isEmailVerified())
        ));
    }

    /* ----------- логин -------------------------------------------------- */

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO dto) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        String token = jwtService.generateToken(dto.getEmail());
        User user = userService.findByEmail(dto.getEmail()).orElseThrow();

        return ResponseEntity.ok(Map.of(
                "token", token,
                "profile", new UserProfileDTO(user.getEmail(), user.getFullName(), user.isEmailVerified())
        ));
    }
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        // TODO: реализовать позже
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        // TODO: реализовать позже
        return ResponseEntity.ok().build();
    }
}
