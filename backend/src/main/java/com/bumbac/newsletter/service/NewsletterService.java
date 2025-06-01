package com.bumbac.newsletter.service;

import com.bumbac.newsletter.dto.NewsletterResponse;
import com.bumbac.newsletter.email.service.EmailService;
import com.bumbac.newsletter.dto.NewsletterRequest;
import com.bumbac.newsletter.dto.NewsletterUnsubscribeRequest;
import com.bumbac.newsletter.entity.NewsletterSubscriber;
import com.bumbac.newsletter.repository.NewsletterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;
    private final EmailService emailService;

    public void subscribe(NewsletterRequest request) {
        NewsletterSubscriber existing = newsletterRepository.findByEmail(request.getEmail()).orElse(null);

        if (existing != null) {
            if (Boolean.TRUE.equals(existing.getConfirmed()) && !Boolean.TRUE.equals(existing.getUnsubscribed())) {
                throw new RuntimeException("Email already subscribed and confirmed.");
            } else {
                existing.setConfirmationToken(UUID.randomUUID().toString());
                existing.setUnsubscribed(false);
                existing.setSubscribedAt(LocalDateTime.now());
                newsletterRepository.save(existing);
                emailService.sendConfirmationEmail(existing);
                return;
            }
        }

        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .email(request.getEmail())
                .confirmationToken(UUID.randomUUID().toString())
                .confirmed(false)
                .unsubscribed(false)
                .subscribedAt(LocalDateTime.now())
                .build();

        newsletterRepository.save(subscriber);
        emailService.sendConfirmationEmail(subscriber);
    }
    public List<NewsletterResponse> getAll() {
        return newsletterRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }
    private NewsletterResponse toDto(NewsletterSubscriber s) {
        return NewsletterResponse.builder()
                .id(s.getId())
                .email(s.getEmail())
                .confirmed(s.getConfirmed())
                .unsubscribed(s.getUnsubscribed())
                .subscribedAt(s.getSubscribedAt())
                .build();
    }



    @Transactional
    public boolean confirm(String token) {
        return newsletterRepository.findByConfirmationToken(token).map(subscriber -> {
            subscriber.setConfirmed(true);
            subscriber.setConfirmationToken(null);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean unsubscribe(NewsletterUnsubscribeRequest request) {
        return newsletterRepository.findByEmail(request.getEmail()).map(subscriber -> {
            subscriber.setUnsubscribed(true);
            return true;
        }).orElse(false);
    }
}