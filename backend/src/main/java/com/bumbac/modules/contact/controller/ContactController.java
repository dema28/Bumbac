package com.bumbac.modules.contact.controller;

import com.bumbac.core.dto.ErrorResponse;
import com.bumbac.modules.contact.dto.ContactFilterRequest;
import com.bumbac.modules.contact.dto.ContactRequest;
import com.bumbac.modules.contact.dto.ContactResponse;
import com.bumbac.modules.contact.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Контактные сообщения", description = "API для управления контактными сообщениями")
@Slf4j
public class ContactController {

  private final ContactService contactService;

  @PostMapping
  @Operation(summary = "Отправка сообщения", description = "Пользователь отправляет сообщение через форму обратной связи")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Сообщение успешно отправлено", content = @Content(schema = @Schema(implementation = ContactResponse.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ContactResponse> handleContact(
      @Valid @RequestBody ContactRequest request,
      BindingResult bindingResult,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.info("Получено контактное сообщение от {} <{}> с IP: {}",
        request.getName(), request.getEmail(), clientIP);

    if (bindingResult.hasErrors()) {
      log.warn("Ошибка валидации контактного сообщения от {}: {}",
          request.getEmail(), bindingResult.getAllErrors());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Ошибка валидации: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    try {
      ContactResponse response = contactService.handleContact(request);
      log.info("Контактное сообщение успешно обработано: id={}", response.getId());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при обработке контактного сообщения от {}: {}",
          request.getEmail(), e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при обработке сообщения");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  @Operation(summary = "Получить все сообщения", description = "Администратор получает список всех контактных сообщений")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список сообщений получен", content = @Content(schema = @Schema(implementation = ContactResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<ContactResponse>> getAllMessages(HttpServletRequest httpRequest) {
    String clientIP = getClientIP(httpRequest);
    log.info("Запрос на получение всех сообщений от IP: {}", clientIP);

    try {
      List<ContactResponse> messages = contactService.getAllMessages();
      log.debug("Возвращено {} сообщений", messages.size());
      return ResponseEntity.ok(messages);

    } catch (Exception e) {
      log.error("Ошибка при получении всех сообщений: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при получении сообщений");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  @Operation(summary = "Получить сообщение по ID", description = "Получение конкретного контактного сообщения по идентификатору")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Сообщение найдено", content = @Content(schema = @Schema(implementation = ContactResponse.class))),
      @ApiResponse(responseCode = "404", description = "Сообщение не найдено", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<ContactResponse> getMessageById(
      @PathVariable @NotNull(message = "ID сообщения обязателен") @Positive(message = "ID сообщения должен быть положительным числом") Long id,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.debug("Запрос на получение сообщения ID: {} от IP: {}", id, clientIP);

    try {
      ContactResponse message = contactService.getMessageById(id);
      log.debug("Сообщение ID: {} найдено", id);
      return ResponseEntity.ok(message);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при получении сообщения ID {}: {}", id, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при получении сообщения");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/filter")
  @Operation(summary = "Фильтрация сообщений", description = "Фильтрация контактных сообщений по различным критериям")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Отфильтрованные сообщения получены", content = @Content(schema = @Schema(implementation = ContactResponse.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации параметров", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<ContactResponse>> getFilteredMessages(
      @RequestParam(required = false) String from,
      @RequestParam(required = false) String to,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String subject,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.debug("Запрос на фильтрацию сообщений от IP: {} с параметрами: from={}, to={}, email={}, subject={}",
        clientIP, from, to, email, subject);

    try {
      List<ContactResponse> messages = contactService.getFilteredMessages(from, to, email, subject);
      log.debug("Возвращено {} отфильтрованных сообщений", messages.size());
      return ResponseEntity.ok(messages);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при фильтрации сообщений: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при фильтрации сообщений");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}/read")
  @Operation(summary = "Прочитать сообщение", description = "Получить текст сообщения по ID и отметить как прочитанное")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Сообщение прочитано", content = @Content(schema = @Schema(example = "## Контактное сообщение\n\n**Имя:** Иван Иванов\n**Email:** ivan@example.com\n..."))),
      @ApiResponse(responseCode = "404", description = "Сообщение не найдено", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<String> readMessageFile(
      @PathVariable @NotNull(message = "ID сообщения обязателен") @Positive(message = "ID сообщения должен быть положительным числом") Long id,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.info("Запрос на чтение сообщения ID: {} от IP: {}", id, clientIP);

    try {
      String content = contactService.readMessageFile(id);
      log.info("Сообщение ID: {} успешно прочитано", id);
      return ResponseEntity.ok(content);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при чтении сообщения ID {}: {}", id, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при чтении сообщения");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить сообщение", description = "Удаляет контактное сообщение по ID")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Сообщение успешно удалено"),
      @ApiResponse(responseCode = "404", description = "Сообщение не найдено", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> deleteMessage(
      @PathVariable @NotNull(message = "ID сообщения обязателен") @Positive(message = "ID сообщения должен быть положительным числом") Long id,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.info("Запрос на удаление сообщения ID: {} от IP: {}", id, clientIP);

    try {
      contactService.deleteMessage(id);
      log.info("Сообщение ID: {} успешно удалено", id);
      return ResponseEntity.noContent().build();

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при удалении сообщения ID {}: {}", id, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при удалении сообщения");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/export")
  @Operation(summary = "Экспорт сообщений", description = "Экспортирует все контактные сообщения в ZIP-файл")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Файл успешно экспортирован"),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Директория с сообщениями не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public void exportMessages(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
    String clientIP = getClientIP(httpRequest);
    log.info("Запрос на экспорт сообщений от IP: {}", clientIP);

    try {
      Resource zip = contactService.exportMessagesToZip();
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zip.getFilename());
      response.setContentType("application/zip");
      zip.getInputStream().transferTo(response.getOutputStream());

      log.info("Экспорт сообщений завершен успешно");

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при экспорте сообщений: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при экспорте сообщений");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/stats/unread")
  @Operation(summary = "Статистика непрочитанных сообщений", description = "Получение количества непрочитанных сообщений")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Статистика получена", content = @Content(schema = @Schema(example = "{\"unreadCount\": 5}"))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Map<String, Object>> getUnreadCount(HttpServletRequest httpRequest) {
    String clientIP = getClientIP(httpRequest);
    log.debug("Запрос на получение статистики непрочитанных сообщений от IP: {}", clientIP);

    try {
      long unreadCount = contactService.getUnreadCount();
      Map<String, Object> stats = Map.of(
          "unreadCount", unreadCount,
          "timestamp", LocalDateTime.now());

      log.debug("Статистика непрочитанных сообщений: {}", unreadCount);
      return ResponseEntity.ok(stats);

    } catch (Exception e) {
      log.error("Ошибка при получении статистики непрочитанных сообщений: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при получении статистики");
    }
  }

  // Вспомогательный метод для получения IP-адреса клиента
  private String getClientIP(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIP = request.getHeader("X-Real-IP");
    if (xRealIP != null && !xRealIP.isEmpty()) {
      return xRealIP;
    }

    return request.getRemoteAddr();
  }
}
