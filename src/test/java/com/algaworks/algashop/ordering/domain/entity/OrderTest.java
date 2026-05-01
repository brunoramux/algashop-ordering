package com.algaworks.algashop.ordering.domain.entity;


import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class OrderTest {

    @Test
    void shouldGenerate(){
        Order order = Order.draft(new CustomerId());
        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.isDraft()).isTrue();
    }

    @Test
    void shouldAddItem(){
        Order order = OrderTestDataBuilder.anOrder()
                .orderStatus(OrderStatus.PLACED)
                .build();

        Assertions.assertThat(order.items()).isNotEmpty();
        Assertions.assertThat(order.items()).hasSize(2);
    }


    @Test
    void shouldCalculateTotals(){
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).build();

        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(3));
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("16010.00"));
    }

    @Test
    void shouldBeAbleToChangeOrderStatusFromDraftToPlaced(){
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).build();

        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void shoulBeAbleToMaskAnOrderAsPaid(){
        Order order = OrderTestDataBuilder.anOrder()
                .orderStatus(OrderStatus.PLACED)
                .build();
        order.markAsPaid();
        Assertions.assertThat(order.isPaid()).isTrue();
    }

    @Test
    void givenAnOrderAlreadyPlaced_shouldNotBeAbleToPlaceOrder(){
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).build();
        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::place);
    }


    @Test
    void givenDraftOrder_whenChangePaymentMethod_shouldAllowChange(){
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).build();
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);

        Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }


    @Test
    void givenDraftOrder_whenChangeBillingInfo_shouldAllowChange(){
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).build();

        Assertions.assertThat(order.billing().address().city()).isEqualTo("Brasilia");
        Assertions.assertThat(order.billing().document()).isEqualTo(new Document("225-09-1992"));
    }

    @Test
    void givenDraftOrder_whenChangeShippingInfo_shouldAllowChange() {


        Order order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(1);

        Shipping shipping = OrderTestDataBuilder.aShipping()
                .cost(shippingCost)
                .expectedDate(expectedDeliveryDate)
                .build();

        order.changeShipping(shipping);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping),
                o -> Assertions.assertThat(o.shipping().cost()).isEqualTo(shippingCost),
                o -> Assertions.assertThat(o.shipping().expectedDate()).isEqualTo(expectedDeliveryDate)
        );

    }

    @Test
    void givenDraftOrderAndDeliveryDateInThePast_whenChangeShippingInfo_shouldNotAllowChange() {

        Order order = Order.draft(new CustomerId());

        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(2);

        Shipping shipping = OrderTestDataBuilder.aShipping()
                .expectedDate(expectedDeliveryDate)
                .build();

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(()-> order.changeShipping(shipping));
    }

    @Test
    void whenChangeOrderItemQuantity_shouldRecalculateTotal(){
        Order order = Order.draft(new CustomerId());

        order.addItem(
                ProductTestDataBuilder.aProduct().build(),
                new Quantity(3)
        );

        Assertions.assertThat(order.totalItems().value()).isEqualTo(3);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("9000.00"));

        OrderItem orderItem = order.items().iterator().next();

        order.changeItemQuantity(orderItem.id(), new Quantity(5));

        Assertions.assertThat(order.totalItems().value()).isEqualTo(5);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("15000.00"));
    }

    @Test
    void givenOutOfStock_whenTryToAddProduct_shouldThrowException() {
        Order order = Order.draft(new CustomerId());

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(
                        () -> order.addItem(ProductTestDataBuilder.aProductUnavailable().build(), new Quantity(2))
                );
    }

}