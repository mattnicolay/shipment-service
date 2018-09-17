package com.solstice.shipment.external;

import com.solstice.shipment.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("product-service")
public interface ProductClient {
  @RequestMapping("/products/{id}")
  Product getProductById(@PathVariable("id") long id);
}
