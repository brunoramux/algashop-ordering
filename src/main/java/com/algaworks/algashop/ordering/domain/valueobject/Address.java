package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.validator.FieldValidations;
import lombok.Builder;

import java.util.Objects;

public record Address(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        ZipCode zipCode
) {

    @Builder(toBuilder = true)
    public Address {
        FieldValidations.requiresNonBlank(street, "Street is required");
        FieldValidations.requiresNonBlank(number, "Number is required");
        FieldValidations.requiresNonBlank(neighborhood, "Neighborhood is required");
        FieldValidations.requiresNonBlank(city, "City is required");
        FieldValidations.requiresNonBlank(state, "State is required");
        Objects.requireNonNull(zipCode, "Zip code is required");
    }

}
