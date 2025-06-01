package com.bumbac.newsletter.repository;

import com.bumbac.newsletter.entity.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<NewsletterSubscriber, Long> {
    Optional<NewsletterSubscriber> findByEmail(String email);
    Optional<NewsletterSubscriber> findByConfirmationToken(String token);
}