package com.algaworks.algashop.ordering.domain.model.order.specification;

import com.algaworks.algashop.ordering.domain.model.Specification;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.LoyaltyPoints;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerHaveFreeShippingSpecification implements Specification<Customer> {
    private final int pointsForFreeShippingRule;

    @Override
    public boolean isSatisfiedBy(Customer customer) {
        return customer.loyaltyPoints().compareTo(new LoyaltyPoints(pointsForFreeShippingRule)) >= 0;
    }
}
