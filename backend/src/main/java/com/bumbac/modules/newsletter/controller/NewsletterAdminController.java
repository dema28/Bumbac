package com.bumbac.modules.newsletter.controller;

import com.bumbac.core.dto.ErrorResponse;
import com.bumbac.modules.newsletter.dto.NewsletterSendRequest;
import com.bumbac.modules.newsletter.dto.NewsletterResponse;
import com.bumbac.modules.newsletter.service.NewsletterService;
import com.bumbac.modules.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class NewsletterAdminController {

    private final NewsletterService newsletterService;
    private final EmailService emailService;

    @GetMapping
    @Operation(summary = "Получить подписчиков", description = "Возвращает список всех email-подписок")
    @ApiResponse(responseCode = "200", description = "Список подписчиков получен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = NewsletterResponse.class)))
    public ResponseEntity<List<NewsletterResponse>> getSubscribers() {
        return ResponseEntity.ok(newsletterService.getAll());
    }

    @GetMapping("/export")
    @Operation(summary = "Экспорт подписчиков в CSV", description = "Экспортирует данные всех подписчиков в CSV-файл")
    @ApiResponse(responseCode = "200", description = "Файл CSV успешно создан")
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
    @Operation(summary = "Разослать письмо подписчикам", description = "Отправляет email-рассылку всем подтверждённым подписчикам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Письма успешно разосланы"),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные письма для рассылки", required = true,
            content = @Content(schema = @Schema(implementation = NewsletterSendRequest.class)))
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
