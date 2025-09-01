package com.bumbac.modules.contact.entity;

import com.bumbac.shared.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "contact_messages",
        indexes = {
                @Index(name = "idx_contact_email", columnList = "email"),
                @Index(name = "idx_contact_subject", columnList = "subject"),
                @Index(name = "idx_contact_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Контактное сообщение от пользователя")
public class ContactMessage extends BaseEntity {

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Имя обязательно для заполнения")
    @Size(min = 2, max = 100, message = "Имя должно содержать от 2 до 100 символов")
    @Schema(description = "Имя отправителя", example = "Иван Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    @Schema(description = "Email отправителя", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Тема сообщения обязательна для заполнения")
    @Size(min = 5, max = 200, message = "Тема должна содержать от 5 до 200 символов")
    @Schema(description = "Тема сообщения", example = "Вопрос о товаре", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subject;

    @Column(name = "file_path", nullable = false, length = 500)
    @NotBlank(message = "Путь к файлу обязателен")
    @Size(max = 500, message = "Путь к файлу не должен превышать 500 символов")
    @Schema(description = "Путь к файлу сообщения", example = "storage/messages/contact_123.md", requiredMode = Schema.RequiredMode.REQUIRED)
    private String filePath;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    @Schema(description = "Статус прочтения сообщения", example = "false")
    private Boolean isRead = false;

    @Column(name = "read_at")
    @Schema(description = "Дата и время прочтения сообщения", example = "2024-01-15T10:30:00")
    private LocalDateTime readAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
