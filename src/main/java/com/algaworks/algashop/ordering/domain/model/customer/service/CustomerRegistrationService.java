package com.algaworks.algashop.ordering.domain.model.customer.service;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.exception.CustomerEmailIsInUseException;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private final Customers customers;

    public Customer register(
            FullName fullName, BirthDate birthDate, Email email, Phone phone, Document document,
            Boolean promotionNotificationAllowed, Address address
    ){

        Customer customer = Customer.brandNew()
                .fullName(fullName)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .document(document)
                .promotionNotificationsAllowed(promotionNotificationAllowed)
                .address(address)
                .build();

        verifyEmailUniqueness(customer.email(), customer.id());

        return customer;
    }

    public void changeEmail(Customer customer, Email newEmail){
        verifyEmailUniqueness(customer.email(), customer.id());

        customer.changeEmail(newEmail);
    }

    private void verifyEmailUniqueness(Email email, CustomerId id) {
        if(!customers.isEmailUnique(email, id)){
            throw new CustomerEmailIsInUseException();
        }
    }

}
