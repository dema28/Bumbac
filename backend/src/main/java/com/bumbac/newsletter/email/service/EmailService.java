package com.bumbac.newsletter.email.service;

import com.bumbac.newsletter.entity.NewsletterSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.url.base}")
    private String baseUrl;

    public void sendConfirmationEmail(NewsletterSubscriber subscriber) {
        String confirmUrl = baseUrl + "/api/newsletter/confirm?token=" + subscriber.getConfirmationToken();
        String subject = "Confirm your subscription";
        String text = "Hello,\n\nPlease confirm your subscription by clicking the link below:\n"
                + confirmUrl + "\n\nThank you!";

        sendEmail(subscriber.getEmail(), subject, text);
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        log.info("Email sent to: {}", to);
    }
}
