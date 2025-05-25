package md.bumbac.api.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import md.bumbac.api.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Адаптер между нашей сущностью User и интерфейсом Spring Security UserDetails.
 */
@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean emailVerified;

    /* ---------- static фабрика ---------- */

    public static UserDetailsImpl fromUser(User user) {
        Set<GrantedAuthority> auths = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                auths,
                user.isEmailVerified()
        );
    }

    /* ---------- интерфейс UserDetails ---------- */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {            // логин — это email
        return email;
    }

    @Override
    public boolean isAccountNonExpired()     { return true; }

    @Override
    public boolean isAccountNonLocked()      { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled()               { return emailVerified; }
}
