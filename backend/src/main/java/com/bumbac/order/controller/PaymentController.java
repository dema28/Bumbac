package com.bumbac.order.controller;

import com.bumbac.common.dto.ErrorResponse;
import com.bumbac.order.dto.CreatePaymentRequest;
import com.bumbac.order.dto.PaymentDTO;
import com.bumbac.order.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Получить платежи", description = "Возвращает все платежи или по конкретному заказу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Платежи получены"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<PaymentDTO> getPayments(@RequestParam(required = false) Long orderId) {
        if (orderId != null) {
            return paymentService.getByOrderId(orderId);
        }
        return paymentService.getAll();
    }

    @PostMapping
    @Operation(summary = "Создать платёж", description = "Создаёт новый платёж по заказу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Платёж успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные платежа", required = true,
            content = @Content(schema = @Schema(implementation = CreatePaymentRequest.class)))
    public PaymentDTO createPayment(@RequestBody CreatePaymentRequest request) {
        return paymentService.create(request);
    }
}
