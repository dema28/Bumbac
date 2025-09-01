package com.bumbac.modules.media.service;

import com.bumbac.modules.media.dto.MediaAssetDTO;
import com.bumbac.modules.media.dto.UploadMediaRequest;
import com.bumbac.modules.media.entity.MediaAsset;
import com.bumbac.modules.media.entity.MediaEntityType;
import com.bumbac.modules.media.entity.MediaFormat;
import com.bumbac.modules.media.entity.MediaVariant;
import com.bumbac.modules.media.mapper.MediaAssetMapper;
import com.bumbac.modules.media.repository.MediaAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaAssetServiceImpl implements MediaAssetService {

  private final MediaAssetRepository repository;
  private final MediaAssetMapper mapper;

  @Value("${app.media.upload-dir:uploads/}")
  private String uploadDir;

  @Value("${app.media.base-url:http://localhost:8080/media/}")
  private String baseUrl;

  @Override
  public List<MediaAssetDTO> getMediaForEntity(MediaEntityType type, Long entityId) {
    log.debug("Получение медиафайлов для сущности типа {} с ID {}", type, entityId);
    return repository.findByEntityTypeAndEntityIdOrderBySortOrder(type, entityId)
        .stream()
        .map(mapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public MediaAssetDTO uploadFile(MultipartFile file, UploadMediaRequest request) {
    log.info("Загрузка файла {} для сущности типа {} с ID {}",
        file.getOriginalFilename(), request.getEntityType(), request.getEntityId());

    // Валидация файла
    validateFile(file);

    try {
      // Создание директории если не существует
      Path uploadPath = Paths.get(uploadDir);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      // Генерация уникального имени файла
      String originalFilename = file.getOriginalFilename();
      String fileExtension = getFileExtension(originalFilename);
      String filename = UUID.randomUUID().toString() + fileExtension;
      Path filePath = uploadPath.resolve(filename);

      // Сохранение файла
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      // Определение формата файла
      MediaFormat format = determineMediaFormat(fileExtension);

      // Создание записи в базе данных
      MediaAsset mediaAsset = MediaAsset.builder()
          .entityType(request.getEntityType())
          .entityId(request.getEntityId())
          .variant(request.getVariant() != null ? request.getVariant() : MediaVariant.MAIN)
          .format(format)
          .url(baseUrl + filename)
          .sizeBytes((int) file.getSize())
          .altText(request.getAltText())
          .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
          .createdAt(LocalDateTime.now())
          .build();

      MediaAsset savedAsset = repository.save(mediaAsset);
      log.info("Файл {} успешно загружен с ID {}", originalFilename, savedAsset.getId());

      return mapper.toDto(savedAsset);

    } catch (IOException e) {
      log.error("Ошибка при сохранении файла {}: {}", file.getOriginalFilename(), e.getMessage(), e);
      throw new RuntimeException("Ошибка при сохранении файла: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteMedia(Long id) {
    log.info("Удаление медиафайла с ID {}", id);

    MediaAsset mediaAsset = repository.findById(id)
        .orElseThrow(() -> {
          log.warn("Медиафайл с ID {} не найден", id);
          return new RuntimeException("Медиафайл не найден");
        });

    try {
      // Удаление физического файла
      String filename = extractFilenameFromUrl(mediaAsset.getUrl());
      Path filePath = Paths.get(uploadDir, filename);

      if (Files.exists(filePath)) {
        Files.delete(filePath);
        log.debug("Физический файл {} удален", filename);
      }

      // Удаление записи из базы данных
      repository.delete(mediaAsset);
      log.info("Медиафайл с ID {} успешно удален", id);

    } catch (IOException e) {
      log.error("Ошибка при удалении физического файла для медиафайла ID {}: {}", id, e.getMessage(), e);
      throw new RuntimeException("Ошибка при удалении файла: " + e.getMessage());
    }
  }

  private void validateFile(MultipartFile file) {
    if (file.isEmpty()) {
      throw new RuntimeException("Файл не может быть пустым");
    }

    if (file.getSize() > 10 * 1024 * 1024) { // 10MB
      throw new RuntimeException("Размер файла не может превышать 10MB");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new RuntimeException("Поддерживаются только изображения");
    }
  }

  private String getFileExtension(String filename) {
    if (filename == null || filename.lastIndexOf(".") == -1) {
      return "";
    }
    return filename.substring(filename.lastIndexOf("."));
  }

  private MediaFormat determineMediaFormat(String extension) {
    return switch (extension.toLowerCase()) {
      case ".jpg", ".jpeg" -> MediaFormat.JPG;
      case ".png" -> MediaFormat.PNG;
      case ".gif" -> MediaFormat.GIF;
      case ".webp" -> MediaFormat.WEBP;
      default -> MediaFormat.OTHER;
    };
  }

  private String extractFilenameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }
}
