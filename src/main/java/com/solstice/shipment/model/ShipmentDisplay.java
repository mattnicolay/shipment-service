package com.solstice.shipment.model;

import java.time.LocalDateTime;
import java.util.List;

public class ShipmentDisplay implements ShipmentObject {
  private long orderNumber;
  private LocalDateTime shippedDate;
  private LocalDateTime deliveryDate;
  private List<OrderLineItem> orderLineItems;

  public ShipmentDisplay() {

  }

  public ShipmentDisplay(long orderNumber, LocalDateTime shippedDate,
      LocalDateTime deliveryDate,
      List<OrderLineItem> orderLineItems) {
    this.orderNumber = orderNumber;
    this.shippedDate = shippedDate;
    this.deliveryDate = deliveryDate;
    this.orderLineItems = orderLineItems;
  }

  public long getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(long orderNumber) {
    this.orderNumber = orderNumber;
  }

  public LocalDateTime getShippedDate() {
    return shippedDate;
  }

  public void setShippedDate(LocalDateTime shippedDate) {
    this.shippedDate = shippedDate;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDateTime deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public List<OrderLineItem> getOrderLineItems() {
    return orderLineItems;
  }

  public void setOrderLineItems(List<OrderLineItem> orderLineItems) {
    this.orderLineItems = orderLineItems;
  }
}
