package com.solstice.shipment.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.solstice.shipment.model.Shipment;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
@DatabaseSetup("classpath:test-dataset.xml")
public class ShipmentRepositoryTest {

  @Autowired
  ShipmentRepository shipmentRepository;

  @Test
  public void findAllByAccountIdOrderByDeliveryDate() {
    List<Shipment> shipments = shipmentRepository.findAllByAccountIdOrderByDeliveryDate(1);

    assertThat(shipments, is(notNullValue()));
    assertFalse(shipments.isEmpty());
    shipments.forEach(shipment -> {
      assertThat(shipment.getId(), is(notNullValue()));
      assertThat(shipment.getAccountId(), is(notNullValue()));
      assertThat(shipment.getShippingAddressId(), is(notNullValue()));
      assertThat(shipment.getShippedDate(), is(notNullValue()));
      assertThat(shipment.getDeliveryDate(), is(notNullValue()));
    });
  }
}