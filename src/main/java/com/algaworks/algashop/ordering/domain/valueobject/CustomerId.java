package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.UUIDGenerator;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId() {
       this(UUIDGenerator.generateTimeBasedUUID());
    }

    public CustomerId(UUID value){
        Objects.requireNonNull(value, "CustomerId cannot be null");
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
