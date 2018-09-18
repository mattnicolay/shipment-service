package com.solstice.shipment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.solstice.shipment.dao.ShipmentRepository;
import com.solstice.shipment.external.OrderClient;
import com.solstice.shipment.external.ProductClient;
import com.solstice.shipment.model.Order;
import com.solstice.shipment.model.OrderLineItem;
import com.solstice.shipment.model.Product;
import com.solstice.shipment.model.Shipment;
import com.solstice.shipment.model.ShipmentDisplay;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {

  private Logger logger = LoggerFactory.getLogger(ShipmentService.class);

  private ShipmentRepository shipmentRepository;
  private OrderClient orderClient;
  private ProductClient productClient;
  private ObjectMapper objectMapper;

  public ShipmentService(ShipmentRepository shipmentRepository,
      OrderClient orderClient, ProductClient productClient) {
    this.shipmentRepository = shipmentRepository;
    this.orderClient = orderClient;
    this.productClient = productClient;
    objectMapper = new ObjectMapper();
  }

  public List<Shipment> getShipments() {
    return shipmentRepository.findAll();
  }

  public Shipment getShipmentById(long id) {
    return shipmentRepository.findById(id);
  }

  public List<ShipmentDisplay> getShipmentByAccountId(long accountId) {
    List<ShipmentDisplay> shipmentDisplays = new ArrayList<>();
    List<Shipment> shipments = shipmentRepository.findAllByAccountIdOrderByDeliveryDate(accountId);
    List<Order> orders = orderClient.getOrdersByAccount(accountId);

    shipments.forEach(shipment -> {
      Optional<Order> orderOptional =
          orders
              .stream()
              .filter(order -> orderHasShipmentId(order, shipments))
              .findFirst();
      Order order = orderOptional.orElseGet(() -> new Order(-1, new ArrayList<>()));
      long orderNumber = order.getOrderNumber();

      setProductNames(order.getOrderLineItems());

      ShipmentDisplay shipmentDisplay = new ShipmentDisplay(
          orderNumber,
          shipment.getShippedDate(),
          shipment.getDeliveryDate(),
          order.getOrderLineItems()
      );

      shipmentDisplays.add(shipmentDisplay);
    });

    return shipmentDisplays;
  }

  private void setProductNames(List<OrderLineItem> orderLineItems) {
    for(OrderLineItem orderLineItem : orderLineItems) {
      Product product = productClient.getProductById(orderLineItem.getProductId());
      orderLineItem.setProductName(product.getName());
    }
  }

  private boolean orderHasShipmentId(Order order, List<Shipment> shipments) {
    for(OrderLineItem orderLineItem : order.getOrderLineItems()) {
      for(Shipment shipment : shipments) {
        if (shipment.getId() == orderLineItem.getShipmentId()) {
          return true;
        }
      }
    }
    return false;
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
    if (shipment != null) {
      shipmentRepository.delete(shipment);
    }
    return shipment;
  }
}
