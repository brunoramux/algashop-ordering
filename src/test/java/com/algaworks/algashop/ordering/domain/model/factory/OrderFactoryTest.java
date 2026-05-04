package com.algaworks.algashop.ordering.domain.model.factory;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.entity.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class OrderFactoryTest {

    @Test
    void shouldCreateFilledOrder()
    {
        Shipping shipping = OrderTestDataBuilder.aShipping().build();
        Billing billing = OrderTestDataBuilder.aBilling();

        Product product = ProductTestDataBuilder.aProduct().build();

        PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

        CustomerId customerId = new CustomerId();

        Order order = OrderFactory.filled(customerId, shipping, billing, paymentMethod, product, new Quantity(2));

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping),
                o -> Assertions.assertThat(o.billing()).isEqualTo(billing),
                o -> Assertions.assertThat(o.items()).hasSize(1),
                o -> Assertions.assertThat(o.totalItems()).isEqualTo(new Quantity(2)),
                o -> Assertions.assertThat(o.paymentMethod()).isEqualTo(paymentMethod),
                o -> Assertions.assertThat(o.customerId()).isEqualTo(customerId),
                o -> Assertions.assertThat(o.isDraft()).isTrue(),
                o -> Assertions.assertThat(o.items().iterator().next().productName()).isEqualTo(new ProductName("IPhone")),
                o -> Assertions.assertThat(o.items().iterator().next().price()).isEqualTo(new Money("3000.00")),
                o -> Assertions.assertThat(o.items().iterator().next().totalAmount()).isEqualTo(new Money("6000.00"))
        );

        order.place();

        Assertions.assertThat(order.isPlaced()).isTrue();

        order.markAsPaid();

        Assertions.assertThat(order.isPaid()).isTrue();
;    }

}