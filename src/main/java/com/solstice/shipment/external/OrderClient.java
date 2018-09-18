package com.solstice.shipment.external;

import com.solstice.shipment.model.Order;
import com.solstice.shipment.model.OrderLineItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "order-order-line-service", fallback = OrderClientFallback.class)
public interface OrderClient {
  @RequestMapping("/orders")
  List<Order> getOrdersByAccount(@RequestParam("accountId") long accountId);
}

@Component
class OrderClientFallback implements OrderClient {

  @Override
  public List<Order> getOrdersByAccount(long accountId) {
    return Arrays.asList(new Order(0, new ArrayList<>()));
  }
}
