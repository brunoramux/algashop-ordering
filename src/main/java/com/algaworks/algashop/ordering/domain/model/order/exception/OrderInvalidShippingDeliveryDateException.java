package com.algaworks.algashop.ordering.domain.model.order.exception;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.ErrorMessages;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderId;

public class OrderInvalidShippingDeliveryDateException extends DomainException {
    public OrderInvalidShippingDeliveryDateException(OrderId id) {
        super(String.format(ErrorMessages.ERROR_ORDER_INVALID_SHIPPING_DELIVERY_DATE, id));
    }
}
