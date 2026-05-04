package com.algaworks.algashop.ordering.domain.model.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class BirthDateTest {

    @Test
    void givenBirthDate_shouldReturnAge(){

        BirthDate birthDate = new BirthDate(LocalDate.of(1990, 10, 30));

        Integer age = birthDate.age();

        Assertions.assertThat(age).isEqualTo(35);

    }

    @Test
    void givenBirthDate_whenDateIsAfterCurrentDate_shouldThrowException(){

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new BirthDate(LocalDate.now().plusDays(1));
                });

    }

}