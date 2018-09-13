package com.solstice.shipment.controller;

import com.solstice.shipment.model.Shipment;
import com.solstice.shipment.service.ShipmentService;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

  private ShipmentService shipmentService;

  public ShipmentController(ShipmentService shipmentService) {
    this.shipmentService = shipmentService;
  }

  @GetMapping
  public ResponseEntity<List<Shipment>> getShipments() {
    List<Shipment> shipments = shipmentService.getShipments();
    return new ResponseEntity<>(
        shipments,
        new HttpHeaders(),
        shipments.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<Shipment> getShipmentById(@PathVariable("id") long id) {
    Shipment shipment = shipmentService.getShipmentById(id);
    return new ResponseEntity<>(
        shipment,
        new HttpHeaders(),
        shipment == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }

  @PostMapping
  public ResponseEntity<Shipment> createShipment(@RequestBody String body) throws IOException {
    Shipment shipment = shipmentService.createShipment(body);
    return new ResponseEntity<>(
        shipment,
        new HttpHeaders(),
        shipment == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.CREATED
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<Shipment> updateShipment(@PathVariable("id") long id,
      @RequestBody String body) throws IOException {
    Shipment shipment = shipmentService.updateShipment(id, body);
    return new ResponseEntity<>(
        shipment,
        new HttpHeaders(),
        shipment == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Shipment> deleteShipment(@PathVariable("id") long id) {
    Shipment shipment = shipmentService.deleteShipment(id);
    return new ResponseEntity<>(
        shipment,
        new HttpHeaders(),
        shipment == null ? HttpStatus.NOT_FOUND : HttpStatus.OK
    );
  }
}
