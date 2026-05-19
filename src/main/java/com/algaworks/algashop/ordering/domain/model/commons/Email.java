package com.algaworks.algashop.ordering.domain.model.commons;

import com.algaworks.algashop.ordering.domain.model.ErrorMessages;
import com.algaworks.algashop.ordering.domain.model.FieldValidations;

import java.util.Objects;

public record Email(String value) {

    public Email(String value) {
        Objects.requireNonNull(value);
        FieldValidations.requiresValidEmail(value, ErrorMessages.VALIDATION_ERROR_EMAIL_IS_INVALID);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
