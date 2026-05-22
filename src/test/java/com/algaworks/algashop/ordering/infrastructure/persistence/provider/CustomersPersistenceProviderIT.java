package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

@DataJpaTest
@Import({CustomerPersistenceEntityAssembler.class, CustomerPersistenceEntityDisassembler.class, CustomersPersistenceProvider.class})
class CustomersPersistenceProviderIT {

    private final CustomersPersistenceProvider provider;

    @Autowired
    public CustomersPersistenceProviderIT(CustomersPersistenceProvider provider) {
        this.provider = provider;
    }

    @Test
    void shouldPersistCustomer() {

        Customer customer = Customer.brandNew()
                .fullName(new FullName("Bruno", "Ramos Lemos"))
                .birthDate(new BirthDate(LocalDate.of(1990, 10, 30)))
                .email(new Email("bruno.lemos@live.com"))
                .document(new Document("1234567"))
                .phone(new Phone("1234556678"))
                .promotionNotificationsAllowed(true)
                .version(1L)
                .address(Address.builder()
                        .street("SQN 403")
                        .complement("Bloco G Apto 301")
                        .city("Brasilia")
                        .number("0")
                        .zipCode(new ZipCode("90.000-00"))
                        .state("Distrito Federal")
                        .neighborhood("Asa Norte")
                        .build())
                .build();

        provider.add(customer);

        Customer persistenceEntity = provider.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(customer.id()).isEqualTo(persistenceEntity.id());
        Assertions.assertThat(provider.count()).isEqualTo(1);
    }
}