package com.bumbac.modules.pattern.service;

import com.bumbac.modules.pattern.dto.PatternDTO;
import com.bumbac.modules.pattern.dto.CreatePatternRequest;
import com.bumbac.modules.pattern.dto.UpdatePatternRequest;

import java.util.List;
import java.util.Optional;

public interface PatternService {

  /**
   * Получить все схемы вязания на указанном языке
   */
  List<PatternDTO> getAll(String lang);

  /**
   * Получить схему вязания по коду и языку
   */
  Optional<PatternDTO> getByCode(String code, String lang);

  /**
   * Создать новую схему вязания
   */
  PatternDTO createPattern(CreatePatternRequest request);

  /**
   * Обновить существующую схему вязания
   */
  PatternDTO updatePattern(String code, UpdatePatternRequest request);

  /**
   * Удалить схему вязания по коду
   */
  void deletePattern(String code);
}
