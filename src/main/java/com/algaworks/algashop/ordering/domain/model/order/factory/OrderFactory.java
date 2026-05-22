package com.algaworks.algashop.ordering.domain.model.order.factory;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;

import java.util.Objects;

public class OrderFactory {

    private OrderFactory() {}

    public static Order filled(
            CustomerId customerId,
            Shipping shipping,
            Billing billing,
            PaymentMethod paymentMethod,
            Product product,
            Quantity productQuantity
    ){
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(shipping);
        Objects.requireNonNull(billing);
        Objects.requireNonNull(paymentMethod);
        Objects.requireNonNull(product);
        Objects.requireNonNull(productQuantity);

        Order order = Order.draft(customerId);

        order.changeBillingInfo(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);

        order.addItem(product, productQuantity);

        return order;
    }
}
