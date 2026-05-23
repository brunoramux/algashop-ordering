package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.repository.Orders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderQueryServiceIT {

    @Autowired
    private Customers customers;

    @Autowired
    private Orders orders;

    @Autowired
    private OrderQueryService orderQueryService;

    @Test
    public void shouldFindByIdAndReturnOrderDetails() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId(UUIDGenerator.generateTimeBasedUUID()))
                .build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().customerId(customer.id()).withItems(true).build();
        orders.add(order);

        OrderDetailOutput output = orderQueryService.findById(order.id().value().toString());
        System.out.println(output);

        Assertions.assertThat(output).isNotNull();

    }

}