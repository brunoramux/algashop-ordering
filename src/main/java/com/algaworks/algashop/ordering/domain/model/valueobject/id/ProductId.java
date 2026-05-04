package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.UUIDGenerator;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {

    public ProductId() {
       this(UUIDGenerator.generateTimeBasedUUID());
    }

    public ProductId{
        Objects.requireNonNull(value, "CustomerId cannot be null");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
