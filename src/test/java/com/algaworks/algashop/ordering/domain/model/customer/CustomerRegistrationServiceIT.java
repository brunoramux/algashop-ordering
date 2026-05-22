package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.service.CustomerRegistrationService;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.BirthDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class CustomerRegistrationServiceIT {

    @Autowired
    private CustomerRegistrationService service;

    @Test
    public void shouldRegisterCustomer() {

        Customer customer = service.register(
                new FullName("Bruno", "Ramos Lemos"),
                new BirthDate(LocalDate.of(1990, 10, 30)),
                new Email("bruno.lemos2@live.com"),
                new Phone("16994610753"),
                new Document("333-22-33333"),
                true,
                Address.builder()
                        .street("SQN 403")
                        .number("SN")
                        .complement("Bloco G Apto 301")
                        .neighborhood("Asa Norte")
                        .city("Brasilia")
                        .state("Distrito Federal")
                        .zipCode(new ZipCode("47145-000"))
                        .build()
        );

        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat(customer.email()).isEqualTo(new Email("bruno.lemos2@live.com"));
    }

}