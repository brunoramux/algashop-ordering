package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;

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
