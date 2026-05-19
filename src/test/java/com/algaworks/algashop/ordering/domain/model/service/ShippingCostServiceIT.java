package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
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