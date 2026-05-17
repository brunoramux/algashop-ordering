package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.UUIDGenerator;

import java.util.Objects;
import java.util.UUID;

public record ShoppingCartId(UUID value) {

    public ShoppingCartId {
        Objects.requireNonNull(value);
    }

    public ShoppingCartId(String value) {
        this(UUID.fromString(value));
    }

    public ShoppingCartId(){
        this(UUIDGenerator.generateTimeBasedUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
