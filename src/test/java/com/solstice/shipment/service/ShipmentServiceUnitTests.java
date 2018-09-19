package com.solstice.shipment.service;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.shipment.dao.ShipmentRepository;
import com.solstice.shipment.external.OrderClient;
import com.solstice.shipment.external.ProductClient;
import com.solstice.shipment.model.Order;
import com.solstice.shipment.model.OrderLineItem;
import com.solstice.shipment.model.Product;
import com.solstice.shipment.model.Shipment;
import com.solstice.shipment.model.ShipmentDisplay;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @MockBean
  private ShipmentRepository shipmentRepository;
  @MockBean
  private ProductClient productClient;
  @MockBean
  private OrderClient orderClient;

  private ShipmentService shipmentService;

  @Before
  public void setup() {
    shipmentService = new ShipmentService(shipmentRepository, orderClient, productClient);
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
    logger.info(toJson(shipment));

    assertThatShipmentsAreEqual(mockShipment, shipment);
  }

  @Test
  public void getShipmentById_InvalidId_ReturnsNull() {
    assertThat(shipmentService.getShipmentById(1), is(nullValue()));
  }

  @Test
  public void getShipmentByAccountId_ValidId_ReturnsListOfShipmentDisplays() {
    List<ShipmentDisplay> mockShipmentDisplays = getMockDisplays();

    when(shipmentRepository.findAllByAccountIdOrderByDeliveryDate(1)).thenReturn(getMockShipments());
    when(orderClient.getOrdersByAccount(anyLong())).thenReturn(getMockOrders());
    when(productClient.getProductById(anyLong())).thenReturn(new Product("Test"));
    List<ShipmentDisplay> shipmentDisplays = shipmentService.getShipmentByAccountId(1);

    assertThat(shipmentDisplays, is(notNullValue()));
    assertFalse(shipmentDisplays.isEmpty());

    for (int i = 0; i < shipmentDisplays.size(); i++) {
      ShipmentDisplay shipmentDisplay = shipmentDisplays.get(i);
      ShipmentDisplay mockShipmentDisplay = mockShipmentDisplays.get(i);
      assertThat(shipmentDisplay.getOrderNumber(), is(mockShipmentDisplay.getOrderNumber()));
      assertThat(shipmentDisplay.getShippedDate(), is(equalTo(mockShipmentDisplay.getShippedDate())));
      assertThat(shipmentDisplay.getDeliveryDate(), is(equalTo(mockShipmentDisplay.getDeliveryDate())));
      for(int j = 0; j < shipmentDisplay.getOrderLineItems().size(); j++) {
        OrderLineItem orderLineItem = shipmentDisplay.getOrderLineItems().get(j);
        OrderLineItem mockOrderLineItem = mockShipmentDisplay.getOrderLineItems().get(j);
        assertThat(orderLineItem.getProductName(), is(equalTo(mockOrderLineItem.getProductName())));
        assertThat(orderLineItem.getQuantity(), is(mockOrderLineItem.getQuantity()));
      }
    }
  }

  @Test
  public void getShipmentByAccountId_OrderOrderLineServiceIsDown_OrdersAreEmpty() {
    List<ShipmentDisplay> mockShipmentDisplays = getMockDisplays();

    when(shipmentRepository.findAllByAccountIdOrderByDeliveryDate(1)).thenReturn(getMockShipments());
    when(orderClient.getOrdersByAccount(anyLong()))
        .thenReturn(Arrays.asList(new Order(0, new ArrayList<>())));
    when(productClient.getProductById(anyLong())).thenReturn(new Product("Test"));
    List<ShipmentDisplay> shipmentDisplays = shipmentService.getShipmentByAccountId(1);

    for (int i = 0; i < shipmentDisplays.size(); i++) {
      ShipmentDisplay shipmentDisplay = shipmentDisplays.get(i);
      ShipmentDisplay mockShipmentDisplay = mockShipmentDisplays.get(i);
      assertThat(shipmentDisplay.getOrderNumber(), is(0L));
      assertThat(shipmentDisplay.getShippedDate(), is(equalTo(mockShipmentDisplay.getShippedDate())));
      assertThat(shipmentDisplay.getDeliveryDate(), is(equalTo(mockShipmentDisplay.getDeliveryDate())));
      for(int j = 0; j < shipmentDisplay.getOrderLineItems().size(); j++) {
        OrderLineItem orderLineItem = shipmentDisplay.getOrderLineItems().get(j);
        OrderLineItem mockOrderLineItem = mockShipmentDisplay.getOrderLineItems().get(j);
        assertThat(orderLineItem.getProductName(), is(equalTo("Test")));
        assertThat(orderLineItem.getQuantity(), is(mockOrderLineItem.getQuantity()));
      }
    }
  }

  @Test
  public void getShipmentByAccountId_ProductServiceIsDown_ProductNamesAreEmpty() {
    List<ShipmentDisplay> mockShipmentDisplays = getMockDisplays();

    when(shipmentRepository.findAllByAccountIdOrderByDeliveryDate(1)).thenReturn(getMockShipments());
    when(orderClient.getOrdersByAccount(anyLong())).thenReturn(getMockOrders());
    when(productClient.getProductById(anyLong())).thenReturn(new Product(""));
    List<ShipmentDisplay> shipmentDisplays = shipmentService.getShipmentByAccountId(1);

    for (int i = 0; i < shipmentDisplays.size(); i++) {
      ShipmentDisplay shipmentDisplay = shipmentDisplays.get(i);
      ShipmentDisplay mockShipmentDisplay = mockShipmentDisplays.get(i);
      assertThat(shipmentDisplay.getOrderNumber(), is(mockShipmentDisplay.getOrderNumber()));
      assertThat(shipmentDisplay.getShippedDate(), is(equalTo(mockShipmentDisplay.getShippedDate())));
      assertThat(shipmentDisplay.getDeliveryDate(), is(equalTo(mockShipmentDisplay.getDeliveryDate())));
      for(int j = 0; j < shipmentDisplay.getOrderLineItems().size(); j++) {
        OrderLineItem orderLineItem = shipmentDisplay.getOrderLineItems().get(j);
        OrderLineItem mockOrderLineItem = mockShipmentDisplay.getOrderLineItems().get(j);
        assertThat(orderLineItem.getProductName(), is(equalTo("")));
        assertThat(orderLineItem.getQuantity(), is(mockOrderLineItem.getQuantity()));
      }
    }
  }

  @Test
  public void getShipmentByAccountId_InvalidId_ReturnsEmptyList() {

    List<ShipmentDisplay> shipments = shipmentService.getShipmentByAccountId(1);

    assertThat(shipments, is(notNullValue()));
    assertTrue(shipments.isEmpty());
  }

  @Test
  public void createShipment_ValidJson_ReturnsShipment() throws IOException {
    Shipment mockShipment = getMockShipment1();
    Shipment shipment = shipmentService.createShipment(toJson(mockShipment));

    assertThatShipmentsAreEqual(mockShipment, shipment);
  }

  @Test(expected = IOException.class)
  public void createShipment_InvalidJson_ThrowsIOException() throws IOException {
    shipmentService.createShipment("{wrong}");
  }

  @Test
  public void updateShipment_ValidIdAndJson_ReturnsShipment() throws IOException {
    Shipment mockShipment = getMockShipment1();
    when(shipmentRepository.findById(1)).thenReturn(mockShipment);
    Shipment shipment = shipmentService.updateShipment(1, toJson(mockShipment));

    assertThatShipmentsAreEqual(mockShipment, shipment);
  }

  @Test
  public void updateShipment_InvalidId_ReturnsNull() throws IOException {
    when(shipmentRepository.findById(2)).thenReturn(null);
    assertThat(shipmentService.updateShipment(2, toJson(getMockShipment1())), is(nullValue()));
  }

  @Test(expected = IOException.class)
  public void updateShipment_InvalidJson_ThrowsIOException() throws IOException {
    shipmentService.updateShipment(1, "{wrong}");
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

  private List<ShipmentDisplay> getMockDisplays() {
    List<Shipment> shipments = getMockShipments();
    List<ShipmentDisplay> shipmentDisplays = new ArrayList<>();

    shipments.forEach(shipment -> shipmentDisplays.add(new ShipmentDisplay(
            1,
            shipment.getShippedDate(),
            shipment.getDeliveryDate(),
            getMockOrderLineItems())));

    return shipmentDisplays;
  }

  private List<OrderLineItem> getMockOrderLineItems() {
    List<OrderLineItem> orderLineItems = new ArrayList<>();
    orderLineItems.add(new OrderLineItem(1, "Test", 1, 1L));
    orderLineItems.add(new OrderLineItem(1, "Test", 1, 2L));
    return orderLineItems;
  }

  private List<Order> getMockOrders() {
    List<Order> orders = new ArrayList<>();
    orders.add(new Order(1, getMockOrderLineItems()));
    orders.add(new Order(2, getMockOrderLineItems()));
    return orders;
  }

  private List<Shipment> getMockShipments() {
    List<Shipment> shipments = new ArrayList<>();
    shipments.add(getMockShipment1());
    shipments.add(getMockShipment2());
    return shipments;
  }

  private Shipment getMockShipment1() {
    Shipment shipment =  new Shipment(
        1,
        1,
        LocalDateTime.of(2018, 9, 8, 12, 30),
        LocalDateTime.of(2018, 9, 12, 8, 40));
    shipment.setId(1L);
    return shipment;
  }

  private Shipment getMockShipment2() {
    Shipment shipment = new Shipment(
        2,
        2,
        LocalDateTime.of(2018, 9, 8, 12, 30),
        LocalDateTime.of(2018, 9, 12, 8, 40));
    shipment.setId(2L);
    return shipment;
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