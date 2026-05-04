package com.algaworks.algashop.ordering.domain.model.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class QuantityTest {

    @Test
    void whenCreatingNegativeQuantity_shouldThrowException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                        () -> new Quantity(-2)
                );
    }


    @Test
    void whenAddingNegativeQuantity_shouldThrowException() {
        Quantity quantity = new Quantity(2);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                        () -> new Quantity(-1)
                );

    }

    @Test
    void whenAddingTwoQuantities_shouldReturnTheSum() {
        Quantity quantity1 = new Quantity(2);
        Quantity quantity2 = new Quantity(3);

        Quantity result = quantity1.add(quantity2);

        Assertions.assertThat(result).isEqualTo(new Quantity(5));
    }
}