package com.bumbac.modules.newsletter.dto;

import lombok.Data;

@Data
public class NewsletterSendRequest {
    private String subject;
    private String content;
}
