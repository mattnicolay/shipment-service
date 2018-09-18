package com.solstice.shipment.external;

import com.solstice.shipment.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {
  @RequestMapping("/products/{id}")
  Product getProductById(@PathVariable("id") long id);
}

@Component
class ProductClientFallback implements ProductClient {

  @Override
  public Product getProductById(long id) {
    return new Product("");
  }
}
