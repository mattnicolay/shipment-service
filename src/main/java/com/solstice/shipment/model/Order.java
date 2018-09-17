package com.solstice.shipment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties({"accountId","orderDate","shippingAddressId","totalPrice"})
public class Order {
  private long orderNumber;
  private List<OrderLineItem> orderLineItems;

  public Order() {

  }

  public Order(long orderNumber,
      List<OrderLineItem> orderLineItems) {
    this.orderNumber = orderNumber;
    this.orderLineItems = orderLineItems;
  }

  public long getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(long orderNumber) {
    this.orderNumber = orderNumber;
  }

  public List<OrderLineItem> getOrderLineItems() {
    return orderLineItems;
  }

  public void setOrderLineItems(List<OrderLineItem> orderLineItems) {
    this.orderLineItems = orderLineItems;
  }
}
