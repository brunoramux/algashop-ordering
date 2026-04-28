package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.*;

import java.time.LocalDate;

public class CustomerTestDataBuilder {

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
}
