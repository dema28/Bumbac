package com.bumbac.modules.pattern.controller;

import com.bumbac.modules.pattern.dto.PatternDTO;
import com.bumbac.modules.pattern.service.PatternService;
import com.bumbac.core.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


import java.util.List;

@RestController
@RequestMapping("/api/patterns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PatternController {

    private final PatternService patternService;

    @GetMapping
    @Operation(summary = "Получить все шаблоны описаний", description = "Возвращает список всех доступных шаблонов описаний товаров на заданном языке")
    @ApiResponse(responseCode = "200", description = "Список шаблонов получен")
    public List<PatternDTO> getAll(@RequestParam(defaultValue = "en") String lang) {
        return patternService.getAll(lang);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Получить шаблон по коду", description = "Возвращает шаблон описания по коду и языку. Если не найден — 404")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Шаблон найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatternDTO.class))),
            @ApiResponse(responseCode = "404", description = "Шаблон не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PatternDTO> getByCode(@PathVariable String code,
                                                @RequestParam(defaultValue = "en") String lang) {
        return patternService.getByCode(code, lang)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
