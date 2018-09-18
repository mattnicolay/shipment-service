package com.solstice.shipment.exception;

import java.io.IOException;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = RestController.class)
public class ShipmentExceptionHandler extends ResponseEntityExceptionHandler {

  private Logger logger = LoggerFactory.getLogger(ShipmentExceptionHandler.class);

  @ExceptionHandler(value = {IOException.class})
  protected ResponseEntity<Object> handleIOException(Exception ex, WebRequest request) {
    String bodyOfResponse = "<h1>ERROR:</h1>\n "
        + "Invalid Json format";
    logger.error("IOException encountered and handled: {}", ex.toString());
    return handleExceptionInternal(
        ex,
        bodyOfResponse,
        new HttpHeaders(),
        HttpStatus.BAD_REQUEST,
        request);
  }

  @ExceptionHandler(value = {EntityNotFoundException.class})
  protected ResponseEntity<Object> handleEntityNotFoundException(Exception ex, WebRequest request) {
    logger.error("EntityNotFoundException encountered and handled: {}", ex.toString());
    String bodyOfResponse = "<h1>ERROR:</h1>\n " + ex.getMessage();
    return handleExceptionInternal(
        ex,
        bodyOfResponse,
        new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }
}
