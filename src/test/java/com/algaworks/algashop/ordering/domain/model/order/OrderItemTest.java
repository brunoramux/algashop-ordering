package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
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
