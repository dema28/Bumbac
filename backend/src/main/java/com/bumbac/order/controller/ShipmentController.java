package com.bumbac.order.controller;

import com.bumbac.common.dto.ErrorResponse;
import com.bumbac.order.dto.ShipmentDTO;
import com.bumbac.order.entity.Shipment;
import com.bumbac.order.entity.ShippingMethod;
import com.bumbac.order.entity.ShipmentStatusHistory;
import com.bumbac.order.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    @Operation(summary = "Получить все отгрузки", description = "Возвращает список всех отгрузок")
    @ApiResponse(responseCode = "200", description = "Список отгрузок получен")
    public List<ShipmentDTO> getAllShipments() {
        return shipmentService.getAllShipmentDTOs();
    }

    @PostMapping
    @Operation(summary = "Создать отгрузку", description = "Создаёт новую отгрузку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отгрузка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные отгрузки", required = true,
            content = @Content(schema = @Schema(implementation = Shipment.class)))
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentService.createShipment(shipment);
    }

    @GetMapping("/methods")
    @Operation(summary = "Получить методы доставки", description = "Возвращает все доступные методы доставки")
    @ApiResponse(responseCode = "200", description = "Методы доставки получены")
    public List<ShippingMethod> getAllShippingMethods() {
        return shipmentService.getAllMethods();
    }

    @GetMapping("/{shipmentId}/history")
    @Operation(summary = "История статусов отгрузки", description = "Возвращает историю изменения статусов отгрузки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "История получена"),
            @ApiResponse(responseCode = "404", description = "Отгрузка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<ShipmentStatusHistory> getStatusHistory(@PathVariable Long shipmentId) {
        return shipmentService.getHistoryForShipment(shipmentId);
    }
}
