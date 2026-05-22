package com.algaworks.algashop.ordering.domain.model.shoppingcart.valueobject;

import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;

import java.util.Objects;
import java.util.UUID;

public record ShoppingCartItemId(UUID value) {

    public ShoppingCartItemId {
        Objects.requireNonNull(value);
    }

    public ShoppingCartItemId(String value){
        this(UUID.fromString(value));
    }

    public ShoppingCartItemId(){
        this(UUIDGenerator.generateTimeBasedUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
