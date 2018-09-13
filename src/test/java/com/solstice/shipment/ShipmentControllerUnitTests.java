package com.solstice.shipment;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.shipment.controller.ShipmentController;
import com.solstice.shipment.exception.ShipmentExceptionHandler;
import com.solstice.shipment.model.Shipment;
import com.solstice.shipment.service.ShipmentService;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(ShipmentController.class)
public class ShipmentControllerUnitTests {

  private Logger logger = LoggerFactory.getLogger(ShipmentController.class);
  private final String GET = "GET";
  private final String POST = "POST";
  private final String PUT = "PUT";
  private final String DELETE = "DELETE";

  @MockBean
  private ShipmentService shipmentService;

  private ShipmentController shipmentController;
  private MockMvc mockMvc;

  @Before
  public void setup() {
    shipmentController = new ShipmentController(shipmentService);
    mockMvc = MockMvcBuilders.standaloneSetup(shipmentController)
        .setControllerAdvice(new ShipmentExceptionHandler()).build();
  }

  @Test
  public void getShipments_Found_StatusCode200() {
    when(shipmentService.getShipments()).thenReturn(Arrays.asList(new Shipment()));
    mockMvcPerform(GET, "/shipments", "", 200, toJson(Arrays.asList(new Shipment())));
  }

  @Test
  public void getShipments_NotFound_StatusCode404() {
    mockMvcPerform(GET, "/shipments", 404, "");
  }

  @Test
  public void getShipmentById_Found_StatusCode200() {
    when(shipmentService.getShipmentById(1)).thenReturn(new Shipment());
    mockMvcPerform(GET, "/shipments/1", 200, toJson(new Shipment()));
  }

  @Test
  public void getShipmentsById_NotFound_StatusCode404() {
    mockMvcPerform(GET, "/shipments/1", 404, "");
  }

  @Test
  public void createShipment_ValidJson_StatusCode201() throws IOException {
    when(shipmentService.createShipment(toJson(new Shipment()))).thenReturn(new Shipment());
    mockMvcPerform(POST, "/shipments", toJson(new Shipment()), 201, toJson(new Shipment()));
  }

  @Test
  public void createShipment_InternalFailure_StatusCode500() {
    mockMvcPerform(POST, "/shipments", "{wrong}", 500, "");
  }

  @Test
  public void createShipment_InvalidJson_StatusCode400() throws IOException {
    when(shipmentService.createShipment(anyString())).thenThrow(new IOException());
    mockMvcPerform(POST, "/shipments", "{wrong}", 400, "");
  }

  @Test
  public void createShipment_EmptyBody_StatusCode400() {
    mockMvcPerform(POST, "/shipments", 400, "");
  }

  @Test
  public void updateShipment_ValidIdAndJson_StatusCode200() throws IOException {
    when(shipmentService.updateShipment(1, toJson(new Shipment()))).thenReturn(new Shipment());
    mockMvcPerform(PUT, "/shipments/1", toJson(new Shipment()), 200, toJson(new Shipment()));
  }

  @Test
  public void updateShipment_InvalidJson_StatusCode400() throws IOException {
    when(shipmentService.updateShipment(anyLong(), anyString())).thenThrow(new IOException());
    mockMvcPerform(PUT, "/shipments/1", "{wrong}", 400, "");
  }

  @Test
  public void updateShipment_InvalidId_StatusCode404() {
    mockMvcPerform(PUT, "/shipments/1", toJson(new Shipment()), 404, "");
  }

  @Test
  public void updateShipment_EmptyBody_StatusCode400() {
    mockMvcPerform(PUT, "/shipments/1", 400);
  }

  @Test
  public void deleteShipment_ValidId_StatusCode200() throws IOException {
    when(shipmentService.deleteShipment(1)).thenReturn(new Shipment());
    mockMvcPerform(DELETE, "/shipments/1", 200, toJson(new Shipment()));
  }

  @Test
  public void deleteShipment_InvalidId_StatusCode404() {
    mockMvcPerform(DELETE, "/shipments/1", 404, "");
  }

  private String toJson(Object value) {
    String result = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      result = objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      logger.error("JsonProcessingException thrown: {}", e.toString());
    }
    return result;
  }

  private void mockMvcPerform(String method, String endpoint, int expectedStatus) {
    mockMvcPerform(method, endpoint, "", expectedStatus, "");
  }

  private void mockMvcPerform(String method, String endpoint, int expectedStatus, String expectedResponseBody) {
    mockMvcPerform(method, endpoint, "", expectedStatus, expectedResponseBody);
  }

  private void mockMvcPerform(String method, String endpoint, String requestBody, int expectedStatus,
      String expectedResponseBody) {
    try {
      switch(method){

        case GET:
          mockMvc.perform(get(endpoint)).andExpect(status().is(expectedStatus))
              .andExpect(content().json(expectedResponseBody));
          break;

        case POST:
          mockMvc.perform(
              post(endpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
          ).andExpect(status().is(expectedStatus));
          break;

        case PUT:
          mockMvc.perform(
              put(endpoint)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
          ).andExpect(status().is(expectedStatus));
          break;

        case DELETE:
          mockMvc.perform(delete(endpoint)).andExpect(status().is(expectedStatus));
          break;

        default:
          logger.error("Unknown method '{}' given to mockMvcPerform", method);
      }
    } catch (Exception e) {
      logger.error("Exception thrown: {}", e.toString());
    }
  }
}
