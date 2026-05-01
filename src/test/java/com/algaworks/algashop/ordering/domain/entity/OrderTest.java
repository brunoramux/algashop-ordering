package com.algaworks.algashop.ordering.domain.entity;


import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
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
        Assertions.assertThat(order.items().iterator().next().productName()).isEqualTo(new ProductName("IPhone"));


        OrderItem orderItem = order.items().iterator().next();
        Assertions.assertWith(orderItem,
                (i) -> Assertions.assertThat(i.id()).isNotNull(),
                (i) -> Assertions.assertThat(i.productName()).isEqualTo(new ProductName("IPhone")),
                (i) -> Assertions.assertThat(i.price()).isEqualTo(new Money("3000.00")),
                (i) -> Assertions.assertThat(i.quantity()).isEqualTo(new Quantity(2))
        );
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
    void givenDraftOrder_whenChangeShippingInfo_shouldAllowChangeInfo() {
        Address address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt. 11")
                .city("Montfort")
                .state("South Carolina")
                .zipCode(new ZipCode("70763-540")).build();

        ShippingInfo shippingInfo = ShippingInfo.builder()
                .address(address)
                .fullName(new FullName("John", "Doe"))
                .document(new Document("112-33-2321"))
                .phone(new Phone("111-441-1244"))
                .build();

        Order order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(1);

        order.changeShippingInfo(shippingInfo, shippingCost, expectedDeliveryDate);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shippingInfo),
                o -> Assertions.assertThat(o.shippingCost()).isEqualTo(shippingCost),
                o -> Assertions.assertThat(o.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate)
        );

    }

    @Test
    void givenDraftOrderAndDeliveryDateInThePast_whenChangeShippingInfo_shouldNotAllowChangeInfo() {
        Address address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt. 11")
                .city("Montfort")
                .state("South Carolina")
                .zipCode(new ZipCode("70763-540")).build();

        ShippingInfo shippingInfo = ShippingInfo.builder()
                .address(address)
                .fullName(new FullName("John", "Doe"))
                .document(new Document("112-33-2321"))
                .phone(new Phone("111-441-1244"))
                .build();

        Order order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;

        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(2);

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(()-> order.changeShippingInfo(shippingInfo, shippingCost, expectedDeliveryDate));
    }

}