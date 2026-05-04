package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import org.junit.jupiter.api.Test;


class OrderItemTest {

    @Test
    void shouldGenerate() {
        OrderItem.brandNew()
                .orderId(new OrderId())
                .product(ProductTestDataBuilder.aProduct().build())
                .quantity(new Quantity(1))
                .build();
    }
}
