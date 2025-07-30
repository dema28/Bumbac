package com.bumbac.modules.order.service;

import com.bumbac.modules.order.entity.Shipment;
import com.bumbac.modules.order.entity.ShipmentStatusHistory;
import com.bumbac.modules.order.entity.ShippingMethod;
import com.bumbac.modules.order.repository.ShipmentRepository;
import com.bumbac.modules.order.dto.ShipmentDTO;
import com.bumbac.modules.order.mapper.ShipmentMapper;
import com.bumbac.modules.order.repository.ShipmentStatusHistoryRepository;
import com.bumbac.modules.order.repository.ShippingMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final ShipmentStatusHistoryRepository shipmentStatusHistoryRepository;
    private final ShipmentMapper shipmentMapper;


    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    public List<ShippingMethod> getAllMethods() {
        return shippingMethodRepository.findAll();
    }

    public List<ShipmentStatusHistory> getHistoryForShipment(Long shipmentId) {
        return shipmentStatusHistoryRepository.findAll()
                .stream().filter(s -> s.getShipmentId().equals(shipmentId)).toList();
    }
    public List<ShipmentDTO> getAllShipmentDTOs() {
        return shipmentRepository.findAll()
                .stream()
                .map(shipmentMapper::toDto)
                .toList();
    }

}
