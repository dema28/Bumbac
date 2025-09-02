package com.bumbac.modules.contact.service;

import com.bumbac.modules.contact.dto.ContactRequest;
import com.bumbac.modules.contact.dto.ContactResponse;
import com.bumbac.modules.contact.entity.ContactMessage;
import com.bumbac.modules.contact.repository.ContactRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final MeterRegistry meterRegistry;

    @Value("${app.contact.storage-path:storage/messages}")
    private String storagePath;

    // метрики (внутренняя инициализация — не бины)
    private Counter contactCreatedCounter;
    private Counter contactReadCounter;
    private Counter contactDeletedCounter;
    private Counter contactExportedCounter;
    private Counter contactErrorCounter;

    @PostConstruct
    void initMetrics() {
        contactCreatedCounter = Counter.builder("contact.operations.created")
                .description("Количество созданных контактных сообщений")
                .register(meterRegistry);
        contactReadCounter = Counter.builder("contact.operations.read")
                .description("Количество прочитанных сообщений")
                .register(meterRegistry);
        contactDeletedCounter = Counter.builder("contact.operations.deleted")
                .description("Количество удаленных сообщений")
                .register(meterRegistry);
        contactExportedCounter = Counter.builder("contact.operations.exported")
                .description("Количество экспортов сообщений")
                .register(meterRegistry);
        contactErrorCounter = Counter.builder("contact.operations.errors")
                .description("Количество ошибок при операциях с сообщениями")
                .register(meterRegistry);
    }

    @Transactional
    public ContactResponse handleContact(ContactRequest request) {
        log.info("Обработка нового контактного сообщения от: {} <{}>", request.getName(), request.getEmail());

        try {
            // создание директории хранения
            Path directory = Paths.get(storagePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                log.debug("Создана директория для хранения сообщений: {}", storagePath);
            }

            // имя файла
            String fileName = "contact_" + UUID.randomUUID() + ".md";
            Path filePath = directory.resolve(fileName);

            // запись в файл
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                writer.write("## Контактное сообщение\n\n");
                writer.write("**Имя:** " + request.getName() + "\n");
                writer.write("**Email:** " + request.getEmail() + "\n");
                writer.write("**Тема:** " + request.getSubject() + "\n");
                writer.write("**Дата:** " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
                writer.write("**Сообщение:**\n\n" + request.getMessage() + "\n");
            }

            // сущность
            ContactMessage message = ContactMessage.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .subject(request.getSubject())
                    .filePath(filePath.toString())
                    .isRead(false)
                    .build();

            ContactMessage saved = contactRepository.save(message);
            contactCreatedCounter.increment();
            log.info("Контактное сообщение создано: id={}, email={}", saved.getId(), saved.getEmail());

            return mapToResponse(saved);

        } catch (IOException e) {
            contactErrorCounter.increment();
            log.error("Ошибка при создании файла сообщения: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при сохранении сообщения");
        } catch (Exception e) {
            contactErrorCounter.increment();
            log.error("Неожиданная ошибка при обработке контактного сообщения: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка при обработке сообщения");
        }
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getAllMessages() {
        log.debug("Получение всех контактных сообщений");
        try {
            List<ContactMessage> messages = contactRepository.findAllOrderByCreatedAtDesc();
            return messages.stream().map(this::mapToResponse).toList();
        } catch (Exception e) {
            contactErrorCounter.increment();
            log.error("Ошибка при получении всех сообщений: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка при получении сообщений");
        }
    }

    @Transactional(readOnly = true)
    public ContactResponse getMessageById(Long id) {
        log.debug("Получение сообщения по ID: {}", id);
        try {
            ContactMessage message = contactRepository.findById(id)
                    .orElseThrow(() -> {
                        contactErrorCounter.increment();
                        log.warn("Сообщение не найдено: id={}", id);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Сообщение не найдено");
                    });
            return mapToResponse(message);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            contactErrorCounter.increment();
            log.error("Ошибка при получении сообщения по ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка при получении сообщения");
        }
    }

    @Transactional(readOnly = true)
    public String readMessageFile(Long id) {
        log.info("Чтение файла сообщения: id={}", id);
        try {
            ContactMessage message = contactRepository.findById(id)
                    .orElseThrow(() -> {
                        contactErrorCounter.increment();
                        log.warn("Сообщение не найдено для чтения: id={}", id);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Сообщение не найдено");
                    });

            Path path = Paths.get(message.getFilePath());
            if (!Files.exists(path)) {
                contactErrorCounter.increment();
                log.error("Файл сообщения не найден: {}", message.getFilePath());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл сообщения не найден");
            }

            String content = Files.readString(path, StandardCharsets.UTF_8);

            // отметить как прочитанное
            if (!Boolean.TRUE.equals(message.getIsRead())) {
                message.setIsRead(true);
                message.setReadAt(LocalDateTime.now());
                contactRepository.save(message);
                log.debug("Сообщение отмечено как прочитанное: id={}", id);
            }

            contactReadCounter.increment();
            return content;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (IOException e) {
            contactErrorCounter.increment();
            log.error("Ошибка при чтении файла сообщения {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при чтении файла сообщения");
        } catch (Exception e) {
            contactErrorCounter.increment();
            log.error("Неожиданная ошибка при чтении сообщения {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка при чтении сообщения");
        }
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> getFilteredMessages(String from, String to, String email, String subject) {
        log.debug("Фильтрация сообщений: from={}, to={}, email={}, subject={}", from, to, email, subject);

        try {
            List<ContactMessage> messages;

            if (from == null && to == null && email == null && subject == null) {
                messages = contactRepository.findAllOrderByCreatedAtDesc();
            } else {
                LocalDateTime fromDate = null;
                LocalDateTime toDate = null;

                if (from != null) {
                    try {
                        fromDate = LocalDate.parse(from).atStartOfDay();
                    } catch (DateTimeParseException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный формат даты 'from'. Используйте формат yyyy-MM-dd");
                    }
                }

                if (to != null) {
                    try {
                        toDate = LocalDate.parse(to).atTime(23, 59, 59);
                    } catch (DateTimeParseException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный формат даты 'to'. Используйте формат yyyy-MM-dd");
                    }
                }

                if (email != null && fromDate != null && toDate != null) {
                    messages = contactRepository.findByEmailAndCreatedAtBetween(email, fromDate, toDate);
                } else if (fromDate != null && toDate != null) {
                    messages = contactRepository.findByCreatedAtBetween(fromDate, toDate);
                } else if (email != null) {
                    messages = contactRepository.findByEmailIgnoreCase(email);
                } else if (subject != null) {
                    messages = contactRepository.findBySubjectContainingIgnoreCase(subject);
                } else {
                    messages = contactRepository.findAllOrderByCreatedAtDesc();
                }
            }

            return messages.stream().map(this::mapToResponse).toList();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            contactErrorCounter.increment();
            log.error("Ошибка при фильтрации сообщений: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка при фильтрации сообщений");
        }
    }

    @Transactional
    public void deleteMessage(Long id) {
        log.info("Удаление сообщения: id={}", id);
        try {
            ContactMessage message = contactRepository.findById(id)
                    .orElseThrow(() -> {
                        contactErrorCounter.increment();
                        log.warn("Сообщение не найдено для удаления: id={}", id);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Сообщение не найдено");
                    });

            try {
                Files.deleteIfExists(Paths.get(message.getFilePath()));
                log.debug("Файл сообщения удален: {}", message.getFilePath());
            } catch (IOException e) {
                log.warn("Ошибка при удалении файла сообщения: {}", e.getMessage());
            }

            contactRepository.delete(message);
            contactDeletedCounter.increment();
            log.info("Сообщение успешно удалено: id={}", id);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            contactErrorCounter.increment();
            log.error("Ошибка при удалении сообщения {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка при удалении сообщения");
        }
    }

    @Transactional(readOnly = true)
    public Resource exportMessagesToZip() {
        log.info("Экспорт сообщений в ZIP архив");
        try {
            Path folderPath = Paths.get(storagePath);
            if (!Files.exists(folderPath)) {
                log.warn("Директория для экспорта не существует: {}", storagePath);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Директория с сообщениями не найдена");
            }

            String zipFileName = "messages_export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".zip";
            Path zipPath = Paths.get("storage", zipFileName);
            Files.createDirectories(zipPath.getParent());

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                Files.walk(folderPath)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try (InputStream fis = Files.newInputStream(file)) {
                                ZipEntry entry = new ZipEntry(file.getFileName().toString());
                                zos.putNextEntry(entry);
                                fis.transferTo(zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                log.error("Ошибка при упаковке файла в архив: {}", file.getFileName(), e);
                                throw new RuntimeException("Ошибка при упаковке файла: " + file.getFileName(), e);
                            }
                        });
            }

            contactExportedCounter.increment();
            return new FileSystemResource(zipPath.toFile());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            contactErrorCounter.increment();
            log.error("Ошибка при экспорте сообщений: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка при экспорте сообщений");
        }
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        try {
            long count = contactRepository.countByIsReadFalse();
            log.debug("Количество непрочитанных сообщений: {}", count);
            return count;
        } catch (Exception e) {
            contactErrorCounter.increment();
            log.error("Ошибка при подсчете непрочитанных сообщений: {}", e.getMessage(), e);
            return 0;
        }
    }

    // mapper
    private ContactResponse mapToResponse(ContactMessage m) {
        return ContactResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .subject(m.getSubject())
                .filePath(m.getFilePath())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
