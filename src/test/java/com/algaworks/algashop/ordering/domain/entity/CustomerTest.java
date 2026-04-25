package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.utility.UUIDGenerator;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

class CustomerTest {

    @Test
     void given_invalidEmail_whenTryCreateCustomer_ShouldGenerateException(){

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new Customer(
                            new CustomerId(UUIDGenerator.generateTimeBasedUUID()),
                            new BirthDate(LocalDate.of(1990, 10, 30)),
                            new FullName("John", "Doe"),
                            true,
                            new Document("12345678900"),
                            new Phone("555-1234"),
                            new Email("invalid-email"),
                            OffsetDateTime.now()
                    );
                });
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomerEmail_ShouldGenerateException(){

        Customer customer = new Customer(
                new CustomerId(UUIDGenerator.generateTimeBasedUUID()),
                new BirthDate(LocalDate.of(1990, 10, 30)),
                new FullName("John", "Doe"),
                true,
                new Document("12345678900"),
                new Phone("555-1234"),
                new Email("john-doe@email.com"),
                OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail(new Email("invalid-email"));
                });
    }


    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize(){
        Customer customer = new Customer(
                new CustomerId(UUIDGenerator.generateTimeBasedUUID()),
                new BirthDate(LocalDate.of(1990, 10, 30)),
                new FullName("John", "Doe"),
                true,
                new Document("12345678900"),
                new Phone("555-1234"),
                new Email("john-doe@email.com"),
                OffsetDateTime.now()
        );

        customer.archive();

        Assertions.assertWith(customer,
                c -> Assertions.assertThat(c.fullName().firstName()).isEqualTo("ANONYMOUS"),
                c -> Assertions.assertThat(c.fullName().lastName()).isEqualTo("ANONYMOUS"),
                c -> Assertions.assertThat(c.email()).isNotEqualTo(new Email("john-doe@email.com")),
                c -> Assertions.assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
                c -> Assertions.assertThat(c.document()).isEqualTo(new Document("000-00-0000")),
                c -> Assertions.assertThat(c.birthDate()).isNull(),
                c -> Assertions.assertThat(c.isPromotionNotificationsAllowed()).isFalse()
                );
    }

    @Test
    void given_archivedCustomer_whenTryArchive_ShouldThrowException(){
        Customer customer = new Customer(
                new CustomerId(UUIDGenerator.generateTimeBasedUUID()),
                new BirthDate(LocalDate.of(1990, 10, 30)),
                new FullName("John", "Doe"),
                true,
                new Document("12345678900"),
                new Phone("555-1234"),
                new Email("john-doe@email.com"),
                OffsetDateTime.now()
        );

        customer.archive();

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);
    }

    @Test
    void given_archivedCustomer_whenTryUpdate_ShouldThrowException(){
        Customer customer = new Customer(
                new CustomerId(UUIDGenerator.generateTimeBasedUUID()),
                new BirthDate(LocalDate.of(1990, 10, 30)),
                new FullName("John", "Doe"),
                true,
                new Document("12345678900"),
                new Phone("555-1234"),
                new Email("john-doe@email.com"),
                OffsetDateTime.now()
        );

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
        Customer customer = new Customer(
                new CustomerId(UUIDGenerator.generateTimeBasedUUID()),
                new BirthDate(LocalDate.of(1990, 10, 30)),
                new FullName("John", "Doe"),
                true,
                new Document("12345678900"),
                new Phone("555-1234"),
                new Email("john-doe@email.com"),
                OffsetDateTime.now()
        );

        customer.addLoyaltyPoint(10);
        customer.addLoyaltyPoint(35);

        Assertions.assertThat(customer.loyaltyPoints().value()).isEqualTo(45);
    }

    @Test
    void given_newCustomer_whenAddNegativeLoyaltyPoints_ShouldSumPoints(){
        Customer customer = new Customer(
                new CustomerId(UUIDGenerator.generateTimeBasedUUID()),
                new BirthDate(LocalDate.of(1990, 10, 30)),
                new FullName("John", "Doe"),
                true,
                new Document("12345678900"),
                new Phone("555-1234"),
                new Email("john-doe@email.com"),
                OffsetDateTime.now()
        );

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