package com.bumbac.modules.newsletter.service;

import com.bumbac.modules.newsletter.dto.NewsletterResponse;
import com.bumbac.modules.email.EmailService;
import com.bumbac.modules.newsletter.dto.NewsletterRequest;
import com.bumbac.modules.newsletter.dto.NewsletterUnsubscribeRequest;
import com.bumbac.modules.newsletter.entity.NewsletterSubscriber;
import com.bumbac.modules.newsletter.repository.NewsletterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {

  private final NewsletterRepository newsletterRepository;
  private final EmailService emailService;

  @Transactional
  public void subscribe(NewsletterRequest request) {
    String email = Optional.ofNullable(request.getEmail())
        .map(String::trim)
        .map(String::toLowerCase)
        .orElseThrow(() -> new IllegalArgumentException("Email обязателен"));

    log.info("Попытка подписки на рассылку для email: {}", email);

    NewsletterSubscriber existing = newsletterRepository.findByEmail(email).orElse(null);

    if (existing != null) {
      if (Boolean.TRUE.equals(existing.getConfirmed()) && !Boolean.TRUE.equals(existing.getUnsubscribed())) {
        log.warn("Попытка повторной подписки для уже подтвержденного email: {}", email);
        throw new RuntimeException("Email уже подписан и подтвержден.");
      } else {
        log.info("Обновление существующей подписки для email: {}", email);
        existing.setConfirmationToken(UUID.randomUUID().toString());
        existing.setUnsubscribed(false);
        existing.setSubscribedAt(LocalDateTime.now());
        newsletterRepository.save(existing);
        emailService.sendConfirmationEmail(existing);
        return;
      }
    }

    log.info("Создание новой подписки для email: {}", email);
    NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
        .email(email)
        .confirmationToken(UUID.randomUUID().toString())
        .confirmed(false)
        .unsubscribed(false)
        .subscribedAt(LocalDateTime.now())
        .build();

    newsletterRepository.save(subscriber);
    emailService.sendConfirmationEmail(subscriber);
    log.info("Подписка создана и письмо подтверждения отправлено для email: {}", email);
  }

  public List<NewsletterResponse> getAll() {
    log.debug("Получение списка всех подписчиков");
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
    log.info("Попытка подтверждения подписки по токену: {}", token);
    return newsletterRepository.findByConfirmationToken(token).map(subscriber -> {
      subscriber.setConfirmed(true);
      subscriber.setConfirmationToken(null);
      newsletterRepository.save(subscriber);
      log.info("Подписка подтверждена для email: {}", subscriber.getEmail());
      return true;
    }).orElse(false);
  }

  @Transactional
  public boolean unsubscribe(NewsletterUnsubscribeRequest request) {
    String email = request.getEmail().trim().toLowerCase();
    log.info("Попытка отписки от рассылки для email: {}", email);

    return newsletterRepository.findByEmail(email).map(subscriber -> {
      subscriber.setUnsubscribed(true);
      newsletterRepository.save(subscriber);
      log.info("Отписка от рассылки выполнена для email: {}", email);
      return true;
    }).orElse(false);
  }
}