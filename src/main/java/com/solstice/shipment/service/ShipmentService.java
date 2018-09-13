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
  ObjectMapper objectMapper;

  public ShipmentService(ShipmentRepository shipmentRepository) {
    this.shipmentRepository = shipmentRepository;
    objectMapper = new ObjectMapper();
  }

  public List<Shipment> getShipments() {
    return shipmentRepository.findAll();
  }

  public Shipment getShipmentById(long id) {
    return shipmentRepository.findById(id);
  }

  public Shipment createShipment(String data) throws IOException {
    Shipment shipment = objectMapper.readValue(data, Shipment.class);
    if (shipment != null) {
      shipmentRepository.save(shipment);
    }
    return shipment;
  }

  public Shipment updateShipment(long id, String data) throws IOException {
    Shipment updatedShipment = objectMapper.readValue(data, Shipment.class);
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
}
