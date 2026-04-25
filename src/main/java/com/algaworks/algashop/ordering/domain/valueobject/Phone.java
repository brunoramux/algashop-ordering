package com.algaworks.algashop.ordering.domain.valueobject;

import java.util.Objects;

public record Phone(String value) {

    public Phone(String value) {
        Objects.requireNonNull(value, "Phone cannot be null");
        if(value.isBlank()){
            throw new IllegalArgumentException("Phone cannot be blank");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
