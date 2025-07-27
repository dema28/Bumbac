package com.bumbac.auth.security;

import com.bumbac.auth.entity.User;
import com.bumbac.auth.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    @Getter
    private final User user;
    private final List<GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.user = user;
        this.authorities = user.getRoles().stream()
                .map(Role::getCode)
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                .collect(Collectors.toList());
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override public String getPassword() {
        return user.getPasswordHash(); // или user.getPassword(), если поле так называется
    }

    @Override public String getUsername() {
        return user.getEmail();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
