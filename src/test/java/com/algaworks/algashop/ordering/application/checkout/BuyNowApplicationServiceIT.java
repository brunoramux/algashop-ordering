package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.customer.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.BuyNowInputTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.OrderPlacedEvent;
import com.algaworks.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class BuyNowApplicationServiceIT {

    @Autowired
    private BuyNowApplicationService service;

    @Autowired
    private Customers customers;

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
}