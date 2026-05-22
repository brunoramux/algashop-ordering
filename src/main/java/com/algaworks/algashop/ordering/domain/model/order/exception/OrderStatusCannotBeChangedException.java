package com.algaworks.algashop.ordering.domain.model.order.exception;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.ErrorMessages;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderId;

public class OrderStatusCannotBeChangedException extends DomainException {

    public OrderStatusCannotBeChangedException(OrderId id, OrderStatus status, OrderStatus newStatus) {
        super(String.format(ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED, id, status, newStatus));
    }

}
