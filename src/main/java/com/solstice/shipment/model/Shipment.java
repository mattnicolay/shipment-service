package com.solstice.shipment.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Shipment implements ShipmentObject {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private long accountId;
  private long shippingAddressId;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime shippedDate;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime deliveryDate;

  public Shipment(){}

  public Shipment(long accountId, long shippingAddressId,
      LocalDateTime shippedDate, LocalDateTime deliveryDate) {
    this.accountId = accountId;
    this.shippingAddressId = shippingAddressId;
    this.shippedDate = shippedDate;
    this.deliveryDate = deliveryDate;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getAccountId() {
    return accountId;
  }

  public void setAccountId(long accountId) {
    this.accountId = accountId;
  }

  public long getShippingAddressId() {
    return shippingAddressId;
  }

  public void setShippingAddressId(long shippingAddressId) {
    this.shippingAddressId = shippingAddressId;
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

  @Override
  public String toString() {
    return "Shipment{" +
        "\nid=" + id +
        "\n, accountId=" + accountId +
        "\n, shippingAddressId=" + shippingAddressId +
        "\n, shippedDate=" + shippedDate +
        "\n, deliveryDate=" + deliveryDate +
        '}';
  }
}
