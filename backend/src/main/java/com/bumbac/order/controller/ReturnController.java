package com.bumbac.order.controller;

import com.bumbac.common.dto.ErrorResponse;
import com.bumbac.order.dto.ReturnDTO;
import com.bumbac.order.dto.ReturnRequestDTO;
import com.bumbac.order.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReturnController {

    private final ReturnService returnService;

    @GetMapping
    @Operation(summary = "Получить возвраты", description = "Возвращает список всех возвратов пользователя")
    @ApiResponse(responseCode = "200", description = "Список возвратов получен")
    public List<ReturnDTO> getAllReturns() {
        return returnService.getAllReturnDTOs();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Создать возврат", description = "Создаёт запрос на возврат заказа")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Возврат создан успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные возврата", required = true,
            content = @Content(schema = @Schema(implementation = ReturnRequestDTO.class)))
    public ReturnDTO createReturn(@RequestBody ReturnRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        return returnService.createReturnDTO(dto, userEmail);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Получить возврат по ID", description = "Возвращает данные одного возврата по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Возврат найден"),
            @ApiResponse(responseCode = "404", description = "Возврат не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ReturnDTO getReturnById(@PathVariable Long id) {
        return returnService.getReturnById(id);
    }
}
