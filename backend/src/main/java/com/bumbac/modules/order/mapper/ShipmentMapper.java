package com.bumbac.modules.order.mapper;

import com.bumbac.modules.order.dto.ShipmentDTO;
import com.bumbac.modules.order.entity.Shipment;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {

    public ShipmentDTO toDto(Shipment shipment) {
        return ShipmentDTO.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrderId())
                .shippingMethodName(shipment.getShippingMethod() != null
                        ? shipment.getShippingMethod().getName()
                        : null)
                .trackingNumber(shipment.getTrackingNumber())
                .shippedAt(shipment.getShippedAt())
                .deliveredAt(shipment.getDeliveredAt())
                .build();
    }
}
