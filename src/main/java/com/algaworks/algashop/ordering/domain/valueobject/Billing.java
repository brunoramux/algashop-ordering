package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.util.Objects;

@Builder
public record Billing(
        FullName fullName,
        Document document,
        Phone phone,
        Email email,
        Address address
) {
    public Billing {
        Objects.requireNonNull(fullName, "Full name is required");
        Objects.requireNonNull(document, "Document is required");
        Objects.requireNonNull(phone, "Phone is required");
        Objects.requireNonNull(email, "Email is required");
        Objects.requireNonNull(address, "Address is required");
    }
}
