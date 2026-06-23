package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.CustomerOutput;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPersistenceEntityQueries {

    Optional<CustomerOutput> findByIdAsOutput(UUID customerId);

    // Versão equivalente utilizando SQL nativa
    Optional<CustomerOutput> findByIdAsOutputNative(UUID customerId);

}
