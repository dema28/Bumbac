package com.bumbac.modules.auth.security;

import com.bumbac.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> {
                    var authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCode()))
                            .toList();
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            user.getPasswordHash(),
                            authorities
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
