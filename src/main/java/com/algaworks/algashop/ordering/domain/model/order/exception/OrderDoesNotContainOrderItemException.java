package com.algaworks.algashop.ordering.domain.model.order.exception;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.ErrorMessages;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderItemId;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(OrderId id, OrderItemId orderItemId) {
        super(String.format(ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ORDER_ITEM,  id, orderItemId));
    }
}
