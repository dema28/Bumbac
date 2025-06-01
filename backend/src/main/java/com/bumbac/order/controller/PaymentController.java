package com.bumbac.order.controller;

import com.bumbac.order.dto.CreatePaymentRequest;
import com.bumbac.order.dto.PaymentDTO;
import com.bumbac.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDTO> getPayments(@RequestParam(required = false) Long orderId) {
        if (orderId != null) {
            return paymentService.getByOrderId(orderId);
        }
        return paymentService.getAll();
    }

    @PostMapping
    public PaymentDTO createPayment(@RequestBody CreatePaymentRequest request) {
        return paymentService.create(request);
    }
}
