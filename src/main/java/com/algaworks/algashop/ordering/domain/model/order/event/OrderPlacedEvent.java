package com.algaworks.algashop.ordering.domain.model.order.event;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderId;

public record OrderPlacedEvent(CustomerId customerId, OrderId orderId, Email email) {
}
