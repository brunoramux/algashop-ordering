package com.algaworks.algashop.ordering.domain.entity;


import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.factory.OrderFactory;
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
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.DRAFT).build();
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

    @Test
    void shouldCreateDraftOrderWithCorrectDefaults() {
        CustomerId customerId = new CustomerId();
        Order order = Order.draft(customerId);

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.id()).isNotNull();
        Assertions.assertThat(order.customerId()).isEqualTo(customerId);
        Assertions.assertThat(order.totalAmount()).isEqualTo(Money.ZERO);
        Assertions.assertThat(order.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(order.placedAt()).isNull();
        Assertions.assertThat(order.paidAt()).isNull();
        Assertions.assertThat(order.cancelledAt()).isNull();
        Assertions.assertThat(order.readyAt()).isNull();
        Assertions.assertThat(order.billing()).isNull();
        Assertions.assertThat(order.shipping()).isNull();
        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.DRAFT);
        Assertions.assertThat(order.paymentMethod()).isNull();
        Assertions.assertThat(order.items()).isEmpty();
        Assertions.assertThat(order.isDraft()).isTrue();
    }

    @Test
    void givenAnDraftOrder_shouldBeAbleToChangeOrderInformation() {
        Order order = Order.draft(new CustomerId());

        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(3));
        order.changeShipping(OrderTestDataBuilder.aShipping().build());
        order.changeBillingInfo(OrderTestDataBuilder.aBilling());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        Assertions.assertThat(order.totalItems().value()).isEqualTo(3);
        Assertions.assertThat(order.shipping()).isNotNull();
        Assertions.assertThat(order.billing()).isNotNull();
        Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);

    }

    @Test
    void givenAnOrderWithStatusDifferentFromDraft_whenTryToChangeOrderInformation_shouldThrowException() {
        Order order = Order.draft(new CustomerId());
        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(3));
        order.changeShipping(OrderTestDataBuilder.aShipping().build());
        order.changeBillingInfo(OrderTestDataBuilder.aBilling());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
        order.place();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(
                        () -> order.addItem(ProductTestDataBuilder.aProductUnavailable().build(), new Quantity(2))
                );

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(
                        () -> order.changeShipping(OrderTestDataBuilder.aShipping().build())
                );

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(
                        () -> order.changeBillingInfo(OrderTestDataBuilder.aBilling())
                );

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(
                        () -> order.changePaymentMethod(PaymentMethod.CREDIT_CARD)
                );
    }

    @Test
    void givenAnNewOrder_shouldBeAbleToRemoveOrderItem(){
        Order order = OrderFactory.filled(
                new CustomerId(),
                OrderTestDataBuilder.aShipping().build(),
                OrderTestDataBuilder.aBilling(),
                PaymentMethod.CREDIT_CARD,
                ProductTestDataBuilder.aProduct().build(),
                new Quantity(1)
        );

        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        Assertions.assertThat(order.totalItems().value()).isEqualTo(2);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("6010.00"));

        OrderItem orderItem = order.items().iterator().next();

        order.removeItem(orderItem.id());

        Assertions.assertThat(order.totalItems().value()).isEqualTo(1);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("3010.00"));

    }

    @Test
    void givenAnPlacedOrder_shouldNotBeAbleToRemoveOrderItem(){
        Order order = OrderFactory.filled(
                new CustomerId(),
                OrderTestDataBuilder.aShipping().build(),
                OrderTestDataBuilder.aBilling(),
                PaymentMethod.CREDIT_CARD,
                ProductTestDataBuilder.aProduct().build(),
                new Quantity(1)
        );

        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        Assertions.assertThat(order.totalItems().value()).isEqualTo(2);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("6010.00"));

        order.place();

        OrderItem orderItem = order.items().iterator().next();
        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(
                        () -> order.removeItem(orderItem.id())
                );

    }

    @Test
    void givenAnPaidOrder_shouldBeAbleToChangeStatusToReady(){
        Order order = OrderFactory.filled(
                new CustomerId(),
                OrderTestDataBuilder.aShipping().build(),
                OrderTestDataBuilder.aBilling(),
                PaymentMethod.CREDIT_CARD,
                ProductTestDataBuilder.aProduct().build(),
                new Quantity(1)
        );

        order.place();
        order.markAsPaid();

        Assertions.assertThat(order.isPaid()).isTrue();

        order.markAsReady();

        Assertions.assertThat(order.isReady()).isTrue();
    }

    @Test
    void givenANotPaidOrder_shouldNotBeAbleToChangeStatusToReady(){
        Order order = OrderFactory.filled(
                new CustomerId(),
                OrderTestDataBuilder.aShipping().build(),
                OrderTestDataBuilder.aBilling(),
                PaymentMethod.CREDIT_CARD,
                ProductTestDataBuilder.aProduct().build(),
                new Quantity(1)
        );

        order.place();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(
                        order::markAsReady
                );


    }

    @Test
    void givenAnOrder_shouldBeAbleToCancelOrder(){
        Order order = OrderFactory.filled(
                new CustomerId(),
                OrderTestDataBuilder.aShipping().build(),
                OrderTestDataBuilder.aBilling(),
                PaymentMethod.CREDIT_CARD,
                ProductTestDataBuilder.aProduct().build(),
                new Quantity(1)
        );

        order.place();
        order.cancel();
        Assertions.assertThat(order.isCancelled()).isTrue();
    }

    @Test
    void givenAnCanceledOrder_shouldNotBeAbleToCancelOrderAgain(){
        Order order = OrderFactory.filled(
                new CustomerId(),
                OrderTestDataBuilder.aShipping().build(),
                OrderTestDataBuilder.aBilling(),
                PaymentMethod.CREDIT_CARD,
                ProductTestDataBuilder.aProduct().build(),
                new Quantity(1)
        );

        order.place();
        order.cancel();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(
                        order::cancel
                );
    }
}