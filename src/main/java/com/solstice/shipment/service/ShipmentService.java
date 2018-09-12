package com.solstice.shipment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.shipment.dao.ShipmentRepository;
import com.solstice.shipment.model.Shipment;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {

  private Logger logger = LoggerFactory.getLogger(ShipmentService.class);

  private ShipmentRepository shipmentRepository;

  public ShipmentService(ShipmentRepository shipmentRepository) {
    this.shipmentRepository = shipmentRepository;
  }

  public List<Shipment> getShipments() {
    return shipmentRepository.findAll();
  }

  public Shipment getShipmentById(long id) {
    return shipmentRepository.findById(id);
  }

  public Shipment createShipment(String data) {
    Shipment shipment = shipmentFromJson(data);
    if (shipment != null) {
      shipmentRepository.save(shipment);
    }
    return shipment;
  }

  public Shipment updateShipment(long id, String data) {
    Shipment updatedShipment = shipmentFromJson(data);
    Shipment dbShipment = shipmentRepository.findById(id);
    if (updatedShipment == null || dbShipment == null) {
      return null;
    }
    updatedShipment.setId(id);
    shipmentRepository.save(updatedShipment);


    return updatedShipment;
  }

  public Shipment deleteShipment(long id) {
    Shipment shipment = shipmentRepository.findById(id);
    shipmentRepository.delete(shipment);
    return shipment;
  }

  public Shipment shipmentFromJson(String json) {
    ObjectMapper objectMapper = new ObjectMapper();
    Shipment shipment = null;
    try {
      shipment = objectMapper.readValue(json, Shipment.class);
    } catch (IOException e) {
      logger.error("IOException thrown in shipmentFromJson: {}", e.toString());
    }
    return shipment;
  }
}
