package com.algaworks.algashop.ordering.domain.valueobject;

import java.util.Objects;

public record Quantity(Integer value) implements Comparable<Quantity> {

    public static final Quantity ZERO = new Quantity(0);

    public Quantity {
        Objects.requireNonNull(value, "value cannot be null");
        if(value < 0){
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
    }

    public Quantity add(Quantity other) {
        if(other.compareTo(ZERO) < 0){
            throw new IllegalArgumentException("Quantity must be a positive number");
        }

        return new Quantity(this.value + other.value());
    }

    @Override
    public int compareTo(Quantity o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
