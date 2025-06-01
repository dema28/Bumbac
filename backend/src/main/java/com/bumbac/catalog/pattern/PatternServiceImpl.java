package com.bumbac.catalog.pattern;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatternServiceImpl implements PatternService {

    private final PatternRepository patternRepository;
    private final PatternMapper patternMapper;

    @Override
    public List<PatternDTO> getAll(String lang) {
        return patternRepository.findAll()
                .stream()
                .map(p -> patternMapper.toDto(p, lang))
                .toList();
    }

    @Override
    public Optional<PatternDTO> getByCode(String code, String lang) {
        return patternRepository.findByCode(code)
                .map(p -> patternMapper.toDto(p, lang));
    }
}
