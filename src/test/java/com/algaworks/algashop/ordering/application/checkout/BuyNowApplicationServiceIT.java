package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.customer.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.order.BuyNowInputTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.event.OrderPlacedEvent;
import com.algaworks.algashop.ordering.domain.model.order.repository.Orders;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderId;
import com.algaworks.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BuyNowApplicationServiceIT {

    @Autowired
    private BuyNowApplicationService service;

    @Autowired
    private Customers customers;

    @Autowired
    private Orders orders;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @Test
    void shouldBePossibleToBuyNow() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();

        String orderId = service.buyNow(input);

        Mockito.verify(orderEventListener, Mockito.times(1))
                .listen(Mockito.any(OrderPlacedEvent.class));

        Assertions.assertThat(orderId).isNotNull();
    }

    @Test
    void shouldHaveFreeShipping(){
        CustomerId customerId = new CustomerId(UUIDGenerator.generateTimeBasedUUID());
        Customer customer = CustomerTestDataBuilder.existingCustomer()
                .id(customerId)
                .loyaltyPoints(new LoyaltyPoints(2000))
                .build();

        customers.add(customer);

        BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput()
                .customerId(customerId.value())
                .build();

        String orderId = service.buyNow(input);

        Order order = orders.ofId(new OrderId(orderId)).orElseThrow();

        Assertions.assertThat(order.shipping().cost()).isEqualTo(Money.ZERO);
    }
}