package com.algaworks.algashop.ordering.domain.model.order.valueobject;

import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import lombok.Builder;

import java.util.Objects;

@Builder
public record Recipient(
        FullName fullName,
        Document document,
        Phone phone
) {

    public Recipient {
        Objects.requireNonNull(fullName, "Full name is required");
        Objects.requireNonNull(document, "Document is required");
        Objects.requireNonNull(phone, "Phone is required");
    }
}
