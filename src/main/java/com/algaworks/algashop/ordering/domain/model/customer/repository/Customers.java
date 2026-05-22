package com.algaworks.algashop.ordering.domain.model.customer.repository;

import com.algaworks.algashop.ordering.domain.model.Repository;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;

import java.util.Optional;

public interface Customers extends Repository<Customer, CustomerId> {
    Optional<Customer> ofEmail(Email email);
    boolean isEmailUnique(Email email, CustomerId customerId);
}
