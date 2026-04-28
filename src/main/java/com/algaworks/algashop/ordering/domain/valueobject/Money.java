package com.algaworks.algashop.ordering.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal value) implements Comparable<Money> {

    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    public static final int SCALE = 2;

    public Money(BigDecimal value){
        Objects.requireNonNull(value);

        if(value.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Value must be a positive number");
        }

        this.value = value.setScale(SCALE, ROUNDING_MODE);
    }

    public Money(String value) {
        this(new BigDecimal(value));
    }

    public Money add(Money other) {
        Objects.requireNonNull(other);
        return new Money(this.value.add(other.value));
    }

    public Money multiply(Quantity quantity) {
        Objects.requireNonNull(quantity);
        if(quantity.value() < 1){
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        return new Money(this.value.multiply(BigDecimal.valueOf(quantity.value())));
    }

    public Money divide(Money other) {
        Objects.requireNonNull(other);
        return new Money(this.value.divide(other.value, SCALE, ROUNDING_MODE));
    }

    @Override
    public int compareTo(Money o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
