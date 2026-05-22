package com.algaworks.algashop.ordering.domain.model.customer.service;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.customer.exception.CantAddLoyaltyPointsOrderIsNotReady;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.exception.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;

import java.util.Objects;

@DomainService
public class CustomerLoyaltyPointsService {

    private static final LoyaltyPoints basePoints = new LoyaltyPoints(5);
    private static final Money expectedAmountToGivePoints = new Money("1000");

    public void addPoints(Customer customer, Order order){
        Objects.requireNonNull(order);
        Objects.requireNonNull(customer);

        if(!customer.id().equals(order.customerId())) {
            throw new OrderNotBelongsToCustomerException();
        }

        if(!order.isReady()){
            throw new CantAddLoyaltyPointsOrderIsNotReady();
        }

        Integer pointsToAdd = order.totalAmount().divide(expectedAmountToGivePoints)
                .multiply(new Quantity(basePoints.value()))
                .value()
                .intValue();

        customer.addLoyaltyPoint(pointsToAdd);
    }

}
