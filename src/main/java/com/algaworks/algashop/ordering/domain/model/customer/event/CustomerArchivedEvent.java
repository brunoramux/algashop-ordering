package com.algaworks.algashop.ordering.domain.model.customer.event;

import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;

import java.time.OffsetDateTime;

public record CustomerArchivedEvent(CustomerId customerId, OffsetDateTime archivedAt) {

}
