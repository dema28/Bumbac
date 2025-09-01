package com.bumbac.modules.order.controller;

import com.bumbac.core.dto.ErrorResponse;
import com.bumbac.modules.order.dto.ReturnDTO;
import com.bumbac.modules.order.entity.ReturnStatusHistory;
import com.bumbac.modules.order.service.ReturnService;
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

import com.bumbac.shared.enums.ReturnStatus;

import java.util.List;

@RestController
@RequestMapping("/api/admin/returns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminReturnController {

    private final ReturnService returnService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список возвратов", description = "Администратор получает возвраты, опционально фильтруя по статусу")
    @ApiResponse(responseCode = "200", description = "Список возвратов получен")
    public List<ReturnDTO> getAllReturns(@RequestParam(required = false) ReturnStatus status) {
        return returnService.getReturnsByStatus(status);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус возврата", description = "Изменяет статус возврата по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус возврата обновлён"),
            @ApiResponse(responseCode = "404", description = "Возврат не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Новый статус возврата", required = true,
            content = @Content(schema = @Schema(implementation = ReturnStatus.class)))
    public ReturnDTO updateStatus(@PathVariable Long id, @RequestParam ReturnStatus status) {
        return returnService.updateReturnStatus(id, status);
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "История статусов возврата", description = "Возвращает историю изменений статуса возврата")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "История получена"),
            @ApiResponse(responseCode = "404", description = "Возврат не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<ReturnStatusHistory> getReturnHistory(@PathVariable Long id) {
        return returnService.getHistoryByReturnId(id);
    }
}
