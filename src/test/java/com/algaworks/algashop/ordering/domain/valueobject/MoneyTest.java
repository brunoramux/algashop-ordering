package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MoneyTest {

    @Test
    void whenCreatingMoneyWithNegativeValue_shouldThrowException(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                        () -> new Money(new BigDecimal("-10.00"))
                );
    }

    @Test
    void whenAddMoneyToAnExistingMoney_shouldAddMoney()
    {
        Money money = new Money(new BigDecimal("10.00"));
        Money newMoney = money.add(new Money(new BigDecimal("10.00")));

        Assertions.assertThat(newMoney).isEqualTo(new Money(new BigDecimal("20.00")));
    }

    @Test
    void whenMultiplyByQuantity_shouldReturnTheMultiplication()
    {
        Money money = new Money(new BigDecimal("10.00"));
        Quantity quantity = new Quantity(2);

        Money newMoney = money.multiply(quantity);

        Assertions.assertThat(newMoney).isEqualTo(new Money(new BigDecimal("20.00")));
    }

    @Test
    void whenDivideMoneyByMoney_shouldReturnTheDivision(){
        Money money = new Money(new BigDecimal("10.00"));
        Money newMoney = money.divide(new Money(new BigDecimal("10.00")));

        Assertions.assertThat(newMoney).isEqualTo(new Money(new BigDecimal("1.00")));
    }
}