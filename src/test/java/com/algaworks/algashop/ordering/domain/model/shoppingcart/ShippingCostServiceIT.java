package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class ShippingCostServiceIT {

    @Autowired
    private ShippingCostService shippingCostService;

    @Autowired
    private OriginAddressService originAddressService;

    @Test
    void shouldCalculateShippingCost() {
        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode("12345-678");

        ShippingCostService.CalculationResult calculationResult = shippingCostService.calculate(
                ShippingCostService.CalculationRequest.builder()
                        .origin(origin)
                        .destination(destination)
                        .build()
        );

        Assertions.assertThat(calculationResult).isNotNull();
        Assertions.assertThat(calculationResult.cost()).isEqualTo(new Money("35.00"));
        Assertions.assertThat(calculationResult.expectedDate()).isEqualTo(LocalDate.now().plusDays(7));

    }

}