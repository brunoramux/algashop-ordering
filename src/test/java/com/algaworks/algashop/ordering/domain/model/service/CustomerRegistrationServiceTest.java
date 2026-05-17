package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

// LEMBRAR DE CONFIGURAR O AGENTE DO MOCKITO
@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    @Mock
    private Customers customers;

    @InjectMocks
    private CustomerRegistrationService service;

    @Test
    public void shouldRegisterCustomer() {
        Mockito.when(customers.isEmailUnique(Mockito.any(Email.class), Mockito.any(CustomerId.class))).thenReturn(true);

        Customer customer = service.register(
                new FullName("Bruno", "Ramos Lemos"),
                new BirthDate(LocalDate.of(1990, 10, 30)),
                new Email("bruno.lemos@live.com"),
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
        Assertions.assertThat(customer.email()).isEqualTo(new Email("bruno.lemos@live.com"));
    }

}