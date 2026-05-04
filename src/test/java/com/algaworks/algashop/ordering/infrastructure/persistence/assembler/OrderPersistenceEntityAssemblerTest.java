package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderPersistenceEntityAssemblerTest {

    private final OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();

    @Test
    void shouldConvertToDomain(){
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.DRAFT).build();
        OrderPersistenceEntity orderPersistenceEntity = assembler.fromDomain(order);

        Assertions.assertThat(orderPersistenceEntity).satisfies(
                p-> Assertions.assertThat(p.getId()).isEqualTo(order.id().value().toLong()),
                p-> Assertions.assertThat(p.getCustomerId()).isEqualTo(order.customerId().value()),
                p -> Assertions.assertThat(p.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                p -> Assertions.assertThat(p.getTotalItems()).isEqualTo(order.totalItems().value()),
                p -> Assertions.assertThat(p.getStatus()).isEqualTo(order.status().name()),
                p -> Assertions.assertThat(p.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
                p -> Assertions.assertThat(p.getPlacedAt()).isEqualTo(order.placedAt()),
                p -> Assertions.assertThat(p.getPaidAt()).isEqualTo(order.paidAt()),
                p -> Assertions.assertThat(p.getCanceledAt()).isEqualTo(order.canceledAt()),
                p -> Assertions.assertThat(p.getReadyAt()).isEqualTo(order.readyAt())
        );
    }

}