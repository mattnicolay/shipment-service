package com.solstice.shipment.controller;

import com.solstice.shipment.exception.HTTP400Exception;
import com.solstice.shipment.exception.HTTP404Exception;
import com.solstice.shipment.model.Shipment;
import com.solstice.shipment.model.ShipmentDisplay;
import com.solstice.shipment.model.ShipmentObject;
import com.solstice.shipment.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipments")
public class ShipmentController extends AbstractRestController{

  private ShipmentService shipmentService;

  public ShipmentController(ShipmentService shipmentService) {
    this.shipmentService = shipmentService;
  }

  @GetMapping
  public @ResponseBody ShipmentObject[] getShipments(@RequestParam(value = "accountId", required = false) Long accountId) {
    ShipmentObject[] shipments = accountId != null
        ? shipmentService.getShipmentByAccountId(accountId).toArray(new ShipmentDisplay[0])
        : shipmentService.getShipments().toArray(new Shipment[0]);
    if (shipments.length == 0) {
      throw new HTTP404Exception("No shipments found");
    }
    return shipments;
  }

  @GetMapping("/{id}")
  public @ResponseBody Shipment getShipmentById(@PathVariable("id") long id) {
    Shipment shipment = shipmentService.getShipmentById(id);
    if (shipment == null) {
      throw new HTTP404Exception("No shipment found with id: " + id);
    }
    return shipment;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody Shipment createShipment(@RequestBody Shipment body) {
    Shipment shipment = shipmentService.createShipment(body);
    if (shipment == null) {
      throw new HTTP400Exception("Could not create shipment");
    }
    return shipment;
  }

  @PutMapping("/{id}")
  public @ResponseBody Shipment updateShipment(@PathVariable("id") long id,
      @RequestBody Shipment body) {
    Shipment shipment = shipmentService.updateShipment(id, body);
    if(shipment == null) {
      throw new HTTP404Exception("No shipment found with id: " + id);
    }
    return shipment;
  }

  @DeleteMapping("/{id}")
  public @ResponseBody Shipment deleteShipment(@PathVariable("id") long id) {
    Shipment shipment = shipmentService.deleteShipment(id);
    if(shipment == null) {
      throw new HTTP404Exception("No shipment found with id: " + id);
    }
    return shipment;
  }
}
