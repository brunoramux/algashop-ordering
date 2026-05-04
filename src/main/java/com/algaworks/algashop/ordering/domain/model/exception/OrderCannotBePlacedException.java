package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;

public class OrderCannotBePlacedException extends DomainException{

    private OrderCannotBePlacedException(String message) {
        super(message);
    }

    public static OrderCannotBePlacedException noShippingInfo(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO, orderId)
        );
    }

    public static OrderCannotBePlacedException noShippingCost(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_COST, orderId)
        );
    }

    public static OrderCannotBePlacedException noBillingInfo(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO, orderId)
        );
    }

    public static OrderCannotBePlacedException noExpectedDeliveryDate(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_EXPECTED_DELIVERY_DATE, orderId)
        );
    }

    public static OrderCannotBePlacedException noPaymentMethod(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ErrorMessages.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD, orderId)
        );
    }

}
