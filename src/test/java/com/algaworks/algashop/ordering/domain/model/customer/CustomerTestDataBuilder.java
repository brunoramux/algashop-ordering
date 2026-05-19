package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.commons.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomerTestDataBuilder {

    public static final CustomerId DEFAULT_CUSTOMER_ID = new CustomerId();

    private CustomerTestDataBuilder() {
    }

    // AQUI EU RETORNO O BUILDER POSSIBILITANDO QUE O TESTE POSSA SOBRESCREVER ALGUM DADO PARA TESTAR CENÁRIOS DE VALIDAÇÃO, POR EXEMPLO
    public static Customer.BrandNewCustomerBuild brandNewCustomer(){
        return Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 10, 30)))
                .promotionNotificationsAllowed(true)
                .document(new Document("12345678900"))
                .phone(new Phone("555-1234"))
                .email(new Email("john-doe@email.com"))
                .address(Address.builder()
                        .street("SQN 403")
                        .number("301")
                        .complement("Bloco G Apto 301")
                        .neighborhood("Asa Norte")
                        .city("Brasilia")
                        .state("DF")
                        .zipCode(new ZipCode("70763-540"))
                        .build());
    }

    public static Customer.ExistingCustomerBuild existingCustomer(){
        return Customer.existing()
                .id(DEFAULT_CUSTOMER_ID)
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 10, 30)))
                .promotionNotificationsAllowed(true)
                .document(new Document("12345678900"))
                .phone(new Phone("555-1234"))
                .email(new Email("john-doe@email.com"))
                .archived(false)
                .registeredAt(OffsetDateTime.now())
                .loyaltyPoints(new LoyaltyPoints(0))
                .address(Address.builder()
                        .street("SQN 403")
                        .number("301")
                        .complement("Bloco G Apto 301")
                        .neighborhood("Asa Norte")
                        .city("Brasilia")
                        .state("DF")
                        .zipCode(new ZipCode("70763-540"))
                        .build());
    }
}
