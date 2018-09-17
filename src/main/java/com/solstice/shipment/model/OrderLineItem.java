package com.solstice.shipment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@JsonIgnoreProperties({"id","price","totalPrice"})
public class OrderLineItem {
  @JsonProperty(access = Access.WRITE_ONLY)
  private long productId;
  private String productName;
  private int quantity;
  @JsonProperty(access = Access.WRITE_ONLY)
  private long shipmentId;

  public OrderLineItem() {

  }

  public OrderLineItem(long productId, int quantity, long shipmentId) {
    this.productId = productId;
    this.quantity = quantity;
    this.shipmentId = shipmentId;
  }

  public OrderLineItem(long productId, String productName, int quantity, long shipmentId) {
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.shipmentId = shipmentId;
  }

  public long getProductId() {
    return productId;
  }

  public void setProductId(long productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public long getShipmentId() {
    return shipmentId;
  }

  public void setShipmentId(long shipmentId) {
    this.shipmentId = shipmentId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
