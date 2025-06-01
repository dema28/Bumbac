package com.bumbac.newsletter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter_subscribers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsletterSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Builder.Default
    private Boolean confirmed = false;

    private String confirmationToken;

    @Builder.Default
    private Boolean unsubscribed = false;

    @Builder.Default
    private LocalDateTime subscribedAt = LocalDateTime.now();
}
