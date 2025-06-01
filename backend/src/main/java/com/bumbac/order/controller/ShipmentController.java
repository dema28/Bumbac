package com.bumbac.order.controller;

import com.bumbac.order.dto.ShipmentDTO;
import com.bumbac.order.entity.Shipment;
import com.bumbac.order.entity.ShippingMethod;
import com.bumbac.order.entity.ShipmentStatusHistory;
import com.bumbac.order.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    public List<ShipmentDTO> getAllShipments() {
        return shipmentService.getAllShipmentDTOs();
    }

    @PostMapping
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentService.createShipment(shipment);
    }

    @GetMapping("/methods")
    public List<ShippingMethod> getAllShippingMethods() {
        return shipmentService.getAllMethods();
    }

    @GetMapping("/{shipmentId}/history")
    public List<ShipmentStatusHistory> getStatusHistory(@PathVariable Long shipmentId) {
        return shipmentService.getHistoryForShipment(shipmentId);
    }
}
