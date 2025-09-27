package com.bumbac.modules.auth.repository;

import com.bumbac.modules.auth.entity.EmailVerificationToken;
import com.bumbac.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUser(User user);
}
