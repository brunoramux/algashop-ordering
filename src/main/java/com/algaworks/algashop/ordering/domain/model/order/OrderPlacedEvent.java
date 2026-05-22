package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;

public record OrderPlacedEvent(CustomerId customerId, OrderId orderId, Email email) {
}
