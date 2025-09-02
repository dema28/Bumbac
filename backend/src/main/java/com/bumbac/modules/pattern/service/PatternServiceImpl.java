package com.bumbac.modules.pattern.service;

import com.bumbac.modules.catalog.entity.Yarn;
import com.bumbac.modules.catalog.repository.YarnRepository;
import com.bumbac.modules.pattern.dto.PatternDTO;
import com.bumbac.modules.pattern.dto.CreatePatternRequest;
import com.bumbac.modules.pattern.dto.UpdatePatternRequest;
import com.bumbac.modules.pattern.entity.Pattern;
import com.bumbac.modules.pattern.entity.PatternTranslation;
import com.bumbac.modules.pattern.entity.PatternTranslationId;
import com.bumbac.modules.pattern.mapper.PatternMapper;
import com.bumbac.modules.pattern.repository.PatternRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bumbac.modules.pattern.entity.Difficulty;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatternServiceImpl implements PatternService {

  private final PatternRepository patternRepository;
  private final YarnRepository yarnRepository;
  private final PatternMapper patternMapper;

  @Override
  public List<PatternDTO> getAll(String lang) {
    log.debug("Получение всех схем вязания для языка: {}", lang);
    return patternRepository.findAll()
        .stream()
        .map(p -> patternMapper.toDto(p, lang))
        .toList();
  }

  @Override
  public Optional<PatternDTO> getByCode(String code, String lang) {
    log.debug("Получение схемы вязания по коду: {} для языка: {}", code, lang);
    return patternRepository.findByCode(code)
        .map(p -> patternMapper.toDto(p, lang));
  }

  @Override
  @Transactional
  public PatternDTO createPattern(CreatePatternRequest request) {
    log.info("Создание новой схемы вязания с кодом: {}", request.getCode());

    // Проверка существования схемы с таким кодом
    if (patternRepository.existsByCode(request.getCode())) {
      log.warn("Попытка создать схему с уже существующим кодом: {}", request.getCode());
      throw new RuntimeException("Схема с кодом " + request.getCode() + " уже существует");
    }

    // Проверка существования пряжи
    Yarn yarn = yarnRepository.findById(request.getYarnId())
        .orElseThrow(() -> {
          log.error("Пряжа с ID {} не найдена", request.getYarnId());
          return new RuntimeException("Пряжа не найдена");
        });

    // Создание схемы
    Pattern pattern = Pattern.builder()
        .code(request.getCode())
        .yarn(yarn)
        .pdfUrl(request.getPdfUrl())
        .difficulty(request.getDifficulty() != null ? request.getDifficulty() : Difficulty.BEGINNER)
        .build();

    // Создание переводов
    List<PatternTranslation> translations = request.getTranslations().stream()
        .map(t -> PatternTranslation.builder()
            .id(new PatternTranslationId(pattern.getId(), t.getLocale()))
            .pattern(pattern)
            .name(t.getName())
            .description(t.getDescription())
            .build())
        .toList();

    pattern.setTranslations(translations);

    Pattern savedPattern = patternRepository.save(pattern);
    log.info("Схема вязания с кодом {} успешно создана", request.getCode());

    return patternMapper.toDto(savedPattern, request.getTranslations().get(0).getLocale());
  }

  @Override
  @Transactional
  public PatternDTO updatePattern(String code, UpdatePatternRequest request) {
    log.info("Обновление схемы вязания с кодом: {}", code);

    Pattern pattern = patternRepository.findByCode(code)
        .orElseThrow(() -> {
          log.warn("Схема вязания с кодом {} не найдена", code);
          return new RuntimeException("Схема вязания не найдена");
        });

    // Обновление полей схемы
    if (request.getYarnId() != null) {
      Yarn yarn = yarnRepository.findById(request.getYarnId())
          .orElseThrow(() -> {
            log.error("Пряжа с ID {} не найдена", request.getYarnId());
            return new RuntimeException("Пряжа не найдена");
          });
      pattern.setYarn(yarn);
    }

    if (request.getPdfUrl() != null) {
      pattern.setPdfUrl(request.getPdfUrl());
    }

    if (request.getDifficulty() != null) {
      pattern.setDifficulty(request.getDifficulty());
    }

    // Обновление переводов
    if (request.getTranslations() != null) {
      request.getTranslations().forEach(t -> {
        PatternTranslationId translationId = new PatternTranslationId(pattern.getId(), t.getLocale());
        Optional<PatternTranslation> existingTranslation = pattern.getTranslations().stream()
            .filter(tr -> tr.getId().equals(translationId))
            .findFirst();

        if (existingTranslation.isPresent()) {
          PatternTranslation translation = existingTranslation.get();
          if (t.getName() != null) {
            translation.setName(t.getName());
          }
          if (t.getDescription() != null) {
            translation.setDescription(t.getDescription());
          }
        } else {
          PatternTranslation newTranslation = PatternTranslation.builder()
              .id(translationId)
              .pattern(pattern)
              .name(t.getName())
              .description(t.getDescription())
              .build();
          pattern.getTranslations().add(newTranslation);
        }
      });
    }

    Pattern updatedPattern = patternRepository.save(pattern);
    log.info("Схема вязания с кодом {} успешно обновлена", code);

    return patternMapper.toDto(updatedPattern, "en"); // Возвращаем на английском по умолчанию
  }

  @Override
  @Transactional
  public void deletePattern(String code) {
    log.info("Удаление схемы вязания с кодом: {}", code);

    Pattern pattern = patternRepository.findByCode(code)
        .orElseThrow(() -> {
          log.warn("Схема вязания с кодом {} не найдена", code);
          return new RuntimeException("Схема вязания не найдена");
        });

    patternRepository.delete(pattern);
    log.info("Схема вязания с кодом {} успешно удалена", code);
  }
}
