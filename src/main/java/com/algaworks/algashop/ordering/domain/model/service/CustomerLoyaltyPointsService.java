package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.exception.CantAddLoyaltyPointsOrderIsNotReady;
import com.algaworks.algashop.ordering.domain.model.exception.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;

import java.util.Objects;

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
