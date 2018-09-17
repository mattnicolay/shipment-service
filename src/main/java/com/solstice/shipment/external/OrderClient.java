package com.solstice.shipment.external;

import com.solstice.shipment.model.Order;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("order-order-line-service")
public interface OrderClient {
  @RequestMapping("/orders")
  List<Order> getOrdersByAccount(@RequestParam("accountId") long accountId);
}
