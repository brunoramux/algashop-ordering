package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId() {
       this(UUIDGenerator.generateTimeBasedUUID());
    }

    public CustomerId{
        Objects.requireNonNull(value, "CustomerId cannot be null");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
