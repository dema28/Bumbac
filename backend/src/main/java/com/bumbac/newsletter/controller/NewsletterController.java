package com.bumbac.newsletter.controller;

import com.bumbac.newsletter.dto.NewsletterRequest;
import com.bumbac.newsletter.dto.NewsletterUnsubscribeRequest;
import com.bumbac.newsletter.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public String subscribe(@RequestBody NewsletterRequest request) {
        newsletterService.subscribe(request);
        return "Confirmation email sent!";
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam String token) {
        return newsletterService.confirm(token)
                ? "Subscription confirmed!"
                : "Invalid or expired token.";
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe(@RequestBody NewsletterUnsubscribeRequest request) {
        return newsletterService.unsubscribe(request)
                ? "You have been unsubscribed."
                : "Email not found.";
    }
}