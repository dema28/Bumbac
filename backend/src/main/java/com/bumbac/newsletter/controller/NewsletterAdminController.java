package com.bumbac.newsletter.controller;

import com.bumbac.newsletter.dto.NewsletterSendRequest;
import com.bumbac.newsletter.dto.NewsletterResponse;
import com.bumbac.newsletter.service.NewsletterService;
import com.bumbac.newsletter.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/admin/newsletter")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NewsletterAdminController {

    private final NewsletterService newsletterService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<NewsletterResponse>> getSubscribers() {
        return ResponseEntity.ok(newsletterService.getAll());
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportToCsv() {
        StringBuilder csv = new StringBuilder("Email,Confirmed,Unsubscribed,SubscribedAt\n");
        newsletterService.getAll().forEach(s -> csv.append(String.format("%s,%s,%s,%s\n",
                s.getEmail(),
                s.getConfirmed(),
                s.getUnsubscribed(),
                s.getSubscribedAt()
        )));

        ByteArrayInputStream stream = new ByteArrayInputStream(csv.toString().getBytes(StandardCharsets.UTF_8));
        InputStreamResource file = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subscribers.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNewsletter(@RequestBody NewsletterSendRequest request) {
        var subscribers = newsletterService.getAll();
        int sent = 0;

        for (var sub : subscribers) {
            if (Boolean.TRUE.equals(sub.getConfirmed()) && !Boolean.TRUE.equals(sub.getUnsubscribed())) {
                emailService.sendEmail(sub.getEmail(), request.getSubject(), request.getContent());
                sent++;
            }
        }

        return ResponseEntity.ok("Newsletter sent to " + sent + " recipients.");
    }
}
