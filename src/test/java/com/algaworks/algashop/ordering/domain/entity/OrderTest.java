package com.algaworks.algashop.ordering.domain.entity;


import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void shouldGenerate(){
        Order.draft(new CustomerId());
    }

    @Test
    void shouldAddItem(){
        Order order = Order.draft(new CustomerId());
        ProductId productId = new ProductId();

        order.addItem(
                productId,
                new ProductName("Iphone"),
                new Money("10000.00"),
                new Quantity(1)
        );

        Assertions.assertThat(order.items()).isNotEmpty();
        Assertions.assertThat(order.items()).hasSize(1);
        Assertions.assertThat(order.items().iterator().next().productName()).isEqualTo(new ProductName("Iphone"));


        OrderItem orderItem = order.items().iterator().next();

        Assertions.assertWith(orderItem,
                (i) -> Assertions.assertThat(i.id()).isNotNull(),
                (i) -> Assertions.assertThat(i.productName()).isEqualTo(new ProductName("Iphone")),
                (i) -> Assertions.assertThat(i.productId()).isEqualTo(productId),
                (i) -> Assertions.assertThat(i.price()).isEqualTo(new Money("10000.00")),
                (i) -> Assertions.assertThat(i.quantity()).isEqualTo(new Quantity(1))
        );
    }


    @Test
    void shouldCalculateTotals(){
        Order order = Order.draft(new CustomerId());
        ProductId productId = new ProductId();

        order.addItem(
                productId,
                new ProductName("Iphone"),
                new Money("10000.00"),
                new Quantity(2)
        );

        order.addItem(
                productId,
                new ProductName("Iphone"),
                new Money("10000.00"),
                new Quantity(3)
        );

        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(5));
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("50000.00"));
    }

    @Test
    void shouldBeAbleToChangeOrderStatusFromDraftToPlaced(){
        Order order = Order.draft(new CustomerId());
        order.place();

        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenAnOrderAlreadyPlaced_shouldNotBeAbleToPlaceOrder(){
        Order order = Order.draft(new CustomerId());
        order.place();
        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::place);
    }
}