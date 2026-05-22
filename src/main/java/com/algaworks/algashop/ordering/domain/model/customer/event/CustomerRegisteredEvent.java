package com.algaworks.algashop.ordering.domain.model.customer.event;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;

import java.time.OffsetDateTime;

public record CustomerRegisteredEvent(CustomerId customerId, OffsetDateTime registerdAt,
                                      FullName fullName, Email email) {
}
