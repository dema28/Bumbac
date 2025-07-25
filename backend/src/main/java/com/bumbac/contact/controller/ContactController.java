package com.bumbac.contact.controller;

import com.bumbac.common.dto.ErrorResponse;
import com.bumbac.contact.dto.ContactRequest;
import com.bumbac.contact.entity.ContactMessage;
import com.bumbac.contact.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    @Operation(summary = "Отправка сообщения", description = "Пользователь отправляет сообщение через форму обратной связи")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение получено"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные сообщения", required = true,
            content = @Content(schema = @Schema(implementation = ContactRequest.class)))
    public ResponseEntity<String> handle(@RequestBody ContactRequest request) {
        contactService.handleContact(request);
        return ResponseEntity.ok("Message received");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Получить все сообщения", description = "Администратор получает список всех сообщений")
    @ApiResponse(responseCode = "200", description = "Список сообщений получен")
    public ResponseEntity<List<ContactMessage>> getAll() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    @Operation(summary = "Фильтрация сообщений", description = "Фильтрация сообщений по дате")
    @ApiResponse(responseCode = "200", description = "Отфильтрованные сообщения")
    public ResponseEntity<List<ContactMessage>> getFiltered(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        return ResponseEntity.ok(contactService.getFilteredMessages(from, to));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/read")
    @Operation(summary = "Прочитать сообщение", description = "Получить текст сообщения по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение прочитано"),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> readFile(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.readMessageFile(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сообщение", description = "Удаляет сообщение по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Сообщение удалено"),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    @Operation(summary = "Экспорт сообщений", description = "Экспортирует все сообщения в ZIP-файл")
    @ApiResponse(responseCode = "200", description = "Файл успешно экспортирован")
    public void export(HttpServletResponse response) throws IOException {
        Resource zip = contactService.exportMessagesToZip();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zip.getFilename());
        zip.getInputStream().transferTo(response.getOutputStream());
    }
}
