package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.util.Objects;

@Builder
public record ShippingInfo(
        FullName fullName,
        Document document,
        Phone phone,
        Address address
) {
    public ShippingInfo {
        Objects.requireNonNull(fullName, "Full name is required");
        Objects.requireNonNull(document, "Document is required");
        Objects.requireNonNull(phone, "Phone is required");
        Objects.requireNonNull(address, "Address is required");
    }
}
