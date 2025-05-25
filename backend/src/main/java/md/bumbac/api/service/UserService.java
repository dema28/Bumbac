package md.bumbac.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.bumbac.api.model.User;
import md.bumbac.api.repository.UserRepository;
import md.bumbac.api.security.UserDetailsImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Сервис работы с пользователями + адаптер для Spring Security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final @Lazy PasswordEncoder passwordEncoder;   // BCryptPasswordEncoder Bean

    /* ----------- Spring Security (login) ---------------- */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        return UserDetailsImpl.fromUser(user);
    }

    /* ----------- Регистрация пользователя --------------- */

    public User registerUser(String email, String rawPassword, String fullName) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already registered");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .emailVerified(false)
                .roles(Set.of("USER"))
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {}", saved.getEmail());
        return saved;
    }

    /* ----------- Доп. методы ---------------------------- */

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
