package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.order.BuyNowInputTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BuyNowApplicationServiceIT {

    @Autowired
    private BuyNowApplicationService service;

    @Test
    void shouldBePossibleToBuyNow() {

        BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput().build();

        String orderId = service.buyNow(input);

        Assertions.assertThat(orderId).isNotNull();
    }
}