package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomerPersistenceEntityTestDataBuilder {


    public CustomerPersistenceEntityTestDataBuilder() {
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder aCustomer(){
        return  CustomerPersistenceEntity.builder()
                        .id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value())
                        .firstName("Bruno")
                        .lastName("Ramos Lemos")
                        .birthDate(LocalDate.of(1990, 10, 30))
                        .email("bruno.lemos@live.com")
                        .document("1234567")
                        .phone("1234556678")
                        .loyaltyPoints(0)
                        .promotionNotificationsAllowed(true)
                        .archived(false)
                        .registeredAt(OffsetDateTime.now())
                .address(AddressEmbeddable.builder()
                        .street("SQN 403")
                        .complement("Bloco G Apto 301")
                        .neighborhood("Asa Norte")
                        .city("Brasilia")
                        .number("0")
                        .zipCode("53040-000")
                        .state("Distrito Federal")
                        .build());

    }
}
