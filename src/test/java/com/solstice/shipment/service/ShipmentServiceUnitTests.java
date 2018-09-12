package com.solstice.shipment.service;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.shipment.dao.ShipmentRepository;
import com.solstice.shipment.model.Shipment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ShipmentServiceUnitTests {

  private Logger logger = LoggerFactory.getLogger(ShipmentServiceUnitTests.class);

  @MockBean
  private ShipmentRepository shipmentRepository;

  private ShipmentService shipmentService;

  @Before
  public void setup() {
    shipmentService = new ShipmentService(shipmentRepository);
  }

  @Test
  public void getShipments_ShipmentsFound_ReturnsListOfShipments() {
    when(shipmentRepository.findAll()).thenReturn(getMockShipments());
    List<Shipment> shipments = shipmentService.getShipments();

    assertThat(shipments, is(notNullValue()));
    assertFalse(shipments.isEmpty());
    shipments.forEach(shipment -> {
      assertThat(shipment.getAccountId(), is(notNullValue()));
      assertThat(shipment.getShippingAddressId(), is(notNullValue()));
      assertThat(shipment.getShippedDate(), is(notNullValue()));
      assertThat(shipment.getDeliveryDate(), is(notNullValue()));
    });
  }

  @Test
  public void getShipments_ShipmentsNotFound_ReturnsEmptyListOfShipments() {
    List<Shipment> shipments = shipmentService.getShipments();

    assertThat(shipments, is(notNullValue()));
    assertTrue(shipments.isEmpty());
  }

  @Test
  public void getShipmentById_ValidId_ReturnsShipment() {
    Shipment mockShipment = getMockShipment1();
    when(shipmentRepository.findById(1)).thenReturn(mockShipment);
    Shipment shipment = shipmentService.getShipmentById(1);

    assertThatShipmentsAreEqual(mockShipment, shipment);
  }

  @Test
  public void getShipmentById_InvalidId_ReturnsNull() {
    assertThat(shipmentService.getShipmentById(1), is(nullValue()));
  }

  @Test
  public void createShipment_ValidJson_ReturnsShipment() {
    Shipment mockShipment = getMockShipment1();
    Shipment shipment = shipmentService.createShipment(toJson(mockShipment));

    assertThatShipmentsAreEqual(mockShipment, shipment);
  }

  @Test
  public void createShipment_InvalidJson_ReturnsNull() {
    assertThat(shipmentService.createShipment("{wrong}"), is(nullValue()));
  }

  @Test
  public void updateShipment_ValidIdAndJson_ReturnsShipment() {
    Shipment mockShipment = getMockShipment1();
    when(shipmentRepository.findById(1)).thenReturn(mockShipment);
    Shipment shipment = shipmentService.updateShipment(1, toJson(mockShipment));

    assertThatShipmentsAreEqual(mockShipment, shipment);
  }

  @Test
  public void updateShipment_InvalidId_ReturnsNull() {
    when(shipmentRepository.findById(2)).thenReturn(null);
    assertThat(shipmentService.updateShipment(2, toJson(getMockShipment1())), is(nullValue()));
  }

  @Test
  public void updateShipment_InvalidJson_ReturnsNull() {
    assertThat(shipmentService.updateShipment(1, "{wrong}"), is(nullValue()));
  }

  @Test
  public void deleteShipment_ValidId_ReturnsShipment() {
    Shipment mockShipment = getMockShipment1();
    when(shipmentRepository.findById(1)).thenReturn(mockShipment);
    Shipment shipment = shipmentService.deleteShipment(1);

    assertThatShipmentsAreEqual(mockShipment, shipment);
  }

  @Test
  public void deleteShipment_InvalidId_ReturnsNull() {
    assertThat(shipmentService.deleteShipment(2), is(nullValue()));
  }

  private void assertThatShipmentsAreEqual(Shipment expected, Shipment actual) {
    assertThat(actual, is(notNullValue()));
    assertThat(actual.getAccountId(), is(notNullValue()));
    assertThat(actual.getAccountId(), is(expected.getAccountId()));
    assertThat(actual.getShippingAddressId(), is(notNullValue()));
    assertThat(actual.getShippingAddressId(), is(expected.getShippingAddressId()));
    assertThat(actual.getShippedDate(), is(notNullValue()));
    assertThat(actual.getShippedDate(), is(equalTo(expected.getShippedDate())));
    assertThat(actual.getDeliveryDate(), is(notNullValue()));
    assertThat(actual.getDeliveryDate(), is(equalTo(expected.getDeliveryDate())));
  }

  private List<Shipment> getMockShipments() {
    List<Shipment> shipments = new ArrayList<>();
    shipments.add(getMockShipment1());
    shipments.add(getMockShipment2());
    return shipments;
  }

  private Shipment getMockShipment1() {
    return new Shipment(
        1,
        1,
        LocalDateTime.of(2018, 9, 8, 12, 30),
        LocalDateTime.of(2018, 9, 12, 8, 40));
  }

  private Shipment getMockShipment2() {
    return new Shipment(
        2,
        2,
        LocalDateTime.of(2018, 9, 8, 12, 30),
        LocalDateTime.of(2018, 9, 12, 8, 40));
  }

  private String toJson(Shipment shipment) {
    ObjectMapper objectMapper = new ObjectMapper();
    String result = null;
    try {
      result = objectMapper.writeValueAsString(shipment);
    } catch (JsonProcessingException e) {
      logger.error("JsonProcessingException thrown: {}", e.toString());
    }
    return result;
  }
}