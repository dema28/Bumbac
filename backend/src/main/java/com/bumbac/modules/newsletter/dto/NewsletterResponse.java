package com.bumbac.modules.newsletter.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewsletterResponse {
    private Long id;
    private String email;
    private Boolean confirmed;
    private Boolean unsubscribed;
    private LocalDateTime subscribedAt;
}
