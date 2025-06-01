package com.bumbac.catalog.pattern;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patterns")
@RequiredArgsConstructor
public class PatternController {

    private final PatternService patternService;

    @GetMapping
    public List<PatternDTO> getAll(@RequestParam(defaultValue = "en") String lang) {
        return patternService.getAll(lang);
    }

    @GetMapping("/{code}")
    public ResponseEntity<PatternDTO> getByCode(@PathVariable String code,
                                                @RequestParam(defaultValue = "en") String lang) {
        return patternService.getByCode(code, lang)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
