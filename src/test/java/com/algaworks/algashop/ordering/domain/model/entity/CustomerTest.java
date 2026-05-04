package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class CustomerTest {

    @Test
     void given_invalidEmail_whenTryCreateCustomer_ShouldGenerateException(){

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    CustomerTestDataBuilder.brandNewCustomer()
                            .email(new Email("invalid-email"))
                            .build();
                });
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomerEmail_ShouldGenerateException(){

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail(new Email("invalid-email"));
                });
    }


    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customer.archive();

        Assertions.assertWith(customer,
                c -> Assertions.assertThat(c.fullName().firstName()).isEqualTo("ANONYMOUS"),
                c -> Assertions.assertThat(c.fullName().lastName()).isEqualTo("ANONYMOUS"),
                c -> Assertions.assertThat(c.email()).isNotEqualTo(new Email("john-doe@email.com")),
                c -> Assertions.assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
                c -> Assertions.assertThat(c.document()).isEqualTo(new Document("000-00-0000")),
                c -> Assertions.assertThat(c.birthDate()).isNull(),
                c -> Assertions.assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
                c -> Assertions.assertThat(c.address()).isEqualTo(
                        Address.builder()
                                .street("SQN 403")
                                .number("Anonymous")
                                .complement(null)
                                .neighborhood("Asa Norte")
                                .city("Brasilia")
                                .state("DF")
                                .zipCode(new ZipCode("70763-540"))
                                .build()
                )
                );
    }

    @Test
    void given_archivedCustomer_whenTryArchive_ShouldThrowException(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customer.archive();

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);
    }

    @Test
    void given_archivedCustomer_whenTryUpdate_ShouldThrowException(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customer.archive();

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enablePromotionNotifications);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disablePromotionNotifications);

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changeFullName(new FullName("John", "Doe"));
                });

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changePhone(new Phone("000-000-0000"));
                });

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changeEmail(new Email("johndoe@email.com"));
                });
    }

    @Test
    void given_newCustomer_whenAddLoyaltyPoints_ShouldSumPoints(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customer.addLoyaltyPoint(10);
        customer.addLoyaltyPoint(35);

        Assertions.assertThat(customer.loyaltyPoints().value()).isEqualTo(45);
    }

    @Test
    void given_newCustomer_whenAddNegativeLoyaltyPoints_ShouldSumPoints(){
        Customer customer = Customer.brandNew()
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
                        .build())
                .build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.addLoyaltyPoint(-10);
                });

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.addLoyaltyPoint(0);
                });

    }
}