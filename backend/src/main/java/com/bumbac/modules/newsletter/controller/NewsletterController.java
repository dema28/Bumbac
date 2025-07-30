package com.bumbac.modules.newsletter.controller;

import com.bumbac.core.dto.ErrorResponse;
import com.bumbac.modules.newsletter.dto.NewsletterRequest;
import com.bumbac.modules.newsletter.dto.NewsletterUnsubscribeRequest;
import com.bumbac.modules.newsletter.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    @Operation(summary = "Подписка на рассылку", description = "Добавляет email в список подписчиков и отправляет письмо с подтверждением")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Письмо подтверждения отправлено"),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные для подписки", required = true,
            content = @Content(schema = @Schema(implementation = NewsletterRequest.class)))
    public String subscribe(@RequestBody NewsletterRequest request) {
        newsletterService.subscribe(request);
        return "Confirmation email sent!";
    }

    @GetMapping("/confirm")
    @Operation(summary = "Подтвердить подписку", description = "Подтверждение подписки по токену из письма")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Подписка подтверждена или токен недействителен")
    })
    public String confirm(@RequestParam String token) {
        return newsletterService.confirm(token)
                ? "Subscription confirmed!"
                : "Invalid or expired token.";
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Отписка от рассылки", description = "Удаляет email из списка подписчиков")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная отписка или email не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные для отписки", required = true,
            content = @Content(schema = @Schema(implementation = NewsletterUnsubscribeRequest.class)))
    public String unsubscribe(@RequestBody NewsletterUnsubscribeRequest request) {
        return newsletterService.unsubscribe(request)
                ? "You have been unsubscribed."
                : "Email not found.";
    }
}
