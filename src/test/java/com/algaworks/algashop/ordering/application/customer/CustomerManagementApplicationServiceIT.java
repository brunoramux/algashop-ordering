package com.algaworks.algashop.ordering.application.customer;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CustomerManagementApplicationServiceIT {

    @Autowired
    private CustomerManagementApplicationService service;

    @Test
    void shouldRegisterCustomer() {
        CustomerInput input = CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("johndoe@email.com")
                .phone("11999999999")
                .document("12345678900")
                .promotionNotificationsAllowed(true)
                .address(AddressData.builder()
                        .street("123 Main St")
                        .number("12345")
                        .city("Main St")
                        .state("Main St")
                        .zipCode("12400-000")
                        .neighborhood("Main St")
                        .complement("Main St")
                        .build())
                .build();

        UUID customerId = service.create(input);

        Assertions.assertThat(customerId).isNotNull();
    }

    @Test
    void shouldGetCustomerById(){
        CustomerInput input = CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("johndoe2@email.com")
                .phone("11999999999")
                .document("12345678900")
                .promotionNotificationsAllowed(true)
                .address(AddressData.builder()
                        .street("123 Main St")
                        .number("12345")
                        .city("Main St")
                        .state("Main St")
                        .zipCode("12400-000")
                        .neighborhood("Main St")
                        .complement("Main St")
                        .build())
                .build();

        UUID customerId = service.create(input);

        CustomerOutput customerOutput = service.findById(customerId);

        Assertions.assertThat(customerOutput).isNotNull();
        Assertions.assertThat(customerOutput.getId()).isEqualTo(customerId);
        Assertions.assertThat(customerOutput.getFirstName()).isEqualTo("John");
        Assertions.assertThat(customerOutput.getLastName()).isEqualTo("Doe");
        Assertions.assertThat(customerOutput.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldBeAbleToUpdateCustomerEmail(){
        CustomerInput input = CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("johndoe2@email.com")
                .phone("11999999999")
                .document("12345678900")
                .promotionNotificationsAllowed(true)
                .address(AddressData.builder()
                        .street("123 Main St")
                        .number("12345")
                        .city("Main St")
                        .state("Main St")
                        .zipCode("12400-000")
                        .neighborhood("Main St")
                        .complement("Main St")
                        .build())
                .build();

        UUID customerId = service.create(input);

        service.changeEmail(customerId, "bruno.lemos@live.com");

        CustomerOutput customer = service.findById(customerId);

        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat(customer.getId()).isEqualTo(customerId);
        Assertions.assertThat(customer.getEmail()).isEqualTo("bruno.lemos@live.com");


    }
}