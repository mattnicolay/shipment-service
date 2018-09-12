package com.solstice.shipment.dao;

import com.solstice.shipment.model.Shipment;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ShipmentRepository extends CrudRepository<Shipment, Long> {

  List<Shipment> findAll();

  Shipment findById(long id);
}
