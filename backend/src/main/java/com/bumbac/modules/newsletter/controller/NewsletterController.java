package com.bumbac.modules.newsletter.controller;

import com.bumbac.core.dto.ErrorResponse;
import com.bumbac.modules.newsletter.dto.NewsletterRequest;
import com.bumbac.modules.newsletter.dto.NewsletterUnsubscribeRequest;
import com.bumbac.modules.newsletter.dto.NewsletterResponse;
import com.bumbac.modules.newsletter.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@Tag(name = "Newsletter", description = "API для управления подпиской на рассылку")
public class NewsletterController {

  private final NewsletterService newsletterService;

  @PostMapping("/subscribe")
  @Operation(summary = "Подписка на рассылку", description = "Добавляет email в список подписчиков и отправляет письмо с подтверждением")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Письмо подтверждения отправлено"),
      @ApiResponse(responseCode = "400", description = "Неверный формат запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<String> subscribe(@Valid @RequestBody NewsletterRequest request) {
    newsletterService.subscribe(request);
    return ResponseEntity.ok("Confirmation email sent.");
  }

  @GetMapping("/confirm")
  @Operation(summary = "Подтвердить подписку", description = "Подтверждение подписки по токену из письма")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Подписка подтверждена или токен недействителен")
  })
  public ResponseEntity<String> confirm(@RequestParam String token) {
    boolean confirmed = newsletterService.confirm(token);
    String message = confirmed ? "Subscription confirmed!" : "Invalid or expired token.";
    return ResponseEntity.ok(message);
  }

  @PostMapping("/unsubscribe")
  @Operation(summary = "Отписка от рассылки", description = "Удаляет email из списка подписчиков")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Успешная отписка или email не найден"),
      @ApiResponse(responseCode = "400", description = "Ошибка запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<String> unsubscribe(@Valid @RequestBody NewsletterUnsubscribeRequest request) {
    boolean unsubscribed = newsletterService.unsubscribe(request);
    String message = unsubscribed ? "You have been unsubscribed." : "Email not found.";
    return ResponseEntity.ok(message);
  }

  @GetMapping("/subscribers")
  @Operation(summary = "Получить всех подписчиков", description = "Возвращает список всех подписчиков (только для админов)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список подписчиков получен", content = @Content(schema = @Schema(implementation = NewsletterResponse.class)))
  })
  public ResponseEntity<List<NewsletterResponse>> getAllSubscribers() {
    List<NewsletterResponse> subscribers = newsletterService.getAll();
    return ResponseEntity.ok(subscribers);
  }
}
