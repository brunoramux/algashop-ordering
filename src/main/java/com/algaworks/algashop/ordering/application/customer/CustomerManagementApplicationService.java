package com.algaworks.algashop.ordering.application.customer;

import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

    private final CustomerRegistrationService customerRegistrationService;
    private final Customers customers;
    private final Mapper mapper;

    @Transactional
    public UUID create(CustomerInput input) {
        Objects.requireNonNull(input);
        Customer customer = customerRegistrationService.register(
                new FullName(input.getFirstName(), input.getLastName()),
                new BirthDate(input.getBirthDate()),
                new Email(input.getEmail()),
                new Phone(input.getPhone()),
                new Document(input.getDocument()),
                input.getPromotionNotificationsAllowed(),
                Address.builder()
                        .street(input.getAddress().getStreet())
                        .number(input.getAddress().getNumber())
                        .complement(input.getAddress().getComplement())
                        .neighborhood(input.getAddress().getNeighborhood())
                        .city(input.getAddress().getCity())
                        .state(input.getAddress().getState())
                        .zipCode(new ZipCode(input.getAddress().getZipCode()))
                        .build()
        );
        customers.add(customer);
        return customer.id().value();
    }

    @Transactional(readOnly = true)
    public CustomerOutput findById(UUID customerId) {

        Objects.requireNonNull(customerId);

        Customer customer = customers.ofId(new CustomerId(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        return mapper.convert(customer, CustomerOutput.class);

    }
}
