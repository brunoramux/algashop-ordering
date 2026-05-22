package com.algaworks.algashop.ordering.domain.model.product.valueobject;

import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;

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
