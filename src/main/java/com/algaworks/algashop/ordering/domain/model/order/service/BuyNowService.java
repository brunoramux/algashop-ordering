package com.algaworks.algashop.ordering.domain.model.order.service;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.specification.CustomerHaveFreeShippingSpecification;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class BuyNowService {

    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;

    public Order buyNow(Product product,
                        Customer customer,
                        Billing billing,
                        Shipping shipping,
                        Quantity quantity,
                        PaymentMethod paymentMethod) {

        product.checkOutOfStock();

        Order order = Order.draft(customer.id());
        order.changeBillingInfo(billing);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, quantity);

        if(haveFreeShipping(customer)){
            Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }

        order.place();



        return order;
    }

    boolean haveFreeShipping(Customer customer){
        return customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }

}