package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerLoyaltyPointsServiceTest {

    CustomerLoyaltyPointsService service = new CustomerLoyaltyPointsService();

    @Test
    void givenAReadyOrder_ShouldAddLoyaltyPoints() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
        order.place();
        order.markAsPaid();
        order.markAsReady();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.READY);

        service.addPoints(customer, order);

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(80));


    }

}