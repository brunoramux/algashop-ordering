package com.algaworks.algashop.ordering.domain.valueobject;

import java.util.Objects;

public record FullName(String firstName, String lastName) {
    public FullName(String firstName, String lastName) {
        Objects.requireNonNull(firstName, "FirstName cannot be null");
        Objects.requireNonNull(lastName, "LastName cannot be null");

        if(firstName.isBlank()){
            throw new IllegalArgumentException("FirstName cannot be blank");
        }

        if(lastName.isBlank()){
            throw new IllegalArgumentException("LastName cannot be blank");
        }

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
