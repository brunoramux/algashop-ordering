package com.algaworks.algashop.ordering.domain.model.order.service;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;

@DomainService
public class BuyNowService {

    public Order buyNow(Product product,
                        CustomerId customerId,
                        Billing billing,
                        Shipping shipping,
                        Quantity quantity,
                        PaymentMethod paymentMethod) {

        product.checkOutOfStock();

        Order order = Order.draft(customerId);
        order.changeBillingInfo(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, quantity);
        order.place();

        return order;
    }

}