package com.bumbac.modules.pattern.service;

import com.bumbac.modules.pattern.dto.PatternDTO;

import java.util.List;
import java.util.Optional;

public interface PatternService {
    List<PatternDTO> getAll(String lang);
    Optional<PatternDTO> getByCode(String code, String lang);
}
