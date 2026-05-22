package com.algaworks.algashop.ordering.domain.model.order.service;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.specification.CustomerHaveFreeShippingSpecification;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@DomainService
@RequiredArgsConstructor
public class CheckoutService {
    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;


    public Order checkout(ShoppingCart shoppingCart, Customer customer, Billing billing, Shipping shipping,
                          PaymentMethod paymentMethod) {

        if(shoppingCart.isEmpty()){
            throw new ShoppingCartCantProceedToCheckoutException();
        }

        if(shoppingCart.containsUnavailableItems()){
            throw new ShoppingCartCantProceedToCheckoutException();
        }

        Set<ShoppingCartItem> items = shoppingCart.items();

        Order order = Order.draft(shoppingCart.customerId());
        order.changeBillingInfo(billing);
        order.changePaymentMethod(paymentMethod);

        for(ShoppingCartItem item : items){
            order.addItem(new Product(item.productId(), item.name(), item.price(), item.isAvailable()), item.quantity());
        }

        if(haveFreeShipping(customer)){
            Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }

        order.place();
        shoppingCart.empty();
        return order;
    }

    boolean haveFreeShipping(Customer customer){
        return customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }

}
