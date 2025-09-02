package com.bumbac.modules.contact.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {
    private Long id;

    // отправитель
    private String name;
    private String email;

    // содержание
    private String subject;
    private String message;   // опционально: если где-то маппится текст

    // файл/вложение
    private String filePath;

    // статус
    private String status;    // опционально: если где-то используется
    private Boolean archived; // опционально

    // таймстемпы
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt; // опционально
}
