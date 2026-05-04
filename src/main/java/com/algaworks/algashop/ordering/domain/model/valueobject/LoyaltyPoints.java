package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record LoyaltyPoints(Integer value) implements Comparable<LoyaltyPoints> {

    public LoyaltyPoints(){
        this(0);
    }

    public LoyaltyPoints(Integer value) {
        Objects.requireNonNull(value, "LoyaltyPoints cannot be null");
        if(value < 0){
            throw new IllegalArgumentException("LoyaltyPoints cannot be negative");
        }
        this.value = value;
    }


    public LoyaltyPoints add(Integer value){
        return this.add(new LoyaltyPoints(value));
    }

    public LoyaltyPoints add(LoyaltyPoints loyaltyPoints){
        Objects.requireNonNull(loyaltyPoints, "LoyaltyPoints cannot be null");
        if(loyaltyPoints.value() < 0){
            throw new IllegalArgumentException("LoyaltyPoints cannot be negative");
        }
        return new LoyaltyPoints(this.value() + loyaltyPoints.value());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(LoyaltyPoints o) {
        return this.value.compareTo(o.value());
    }
}
