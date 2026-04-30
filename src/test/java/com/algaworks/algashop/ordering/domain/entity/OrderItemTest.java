package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import org.junit.jupiter.api.Test;


class OrderItemTest {

    @Test
    public void shouldGenerate() {
        OrderItem item = OrderItem.brandNew()
                .orderId(new OrderId())
                .productId(new ProductId())
                .quantity(new Quantity(1))
                .price(new Money("100"))
                .productName(new ProductName("Product 1"))
                .build();
    }
}
