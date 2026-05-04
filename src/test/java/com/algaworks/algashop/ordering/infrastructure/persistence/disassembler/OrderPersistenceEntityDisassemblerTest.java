package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderPersistenceEntityDisassemblerTest {

    @Test
    void shouldDisassembleOrderPersistenceEntityToOrder() {
        UUID customerId = UUID.randomUUID();
        OffsetDateTime placedAt = OffsetDateTime.now().minusDays(1);
        OffsetDateTime paidAt = OffsetDateTime.now();
        OffsetDateTime readyAt = OffsetDateTime.now().plusDays(1);

        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntity.builder()
                .id(123L)
                .customerId(customerId)
                .totalAmount(BigDecimal.valueOf(150.00))
                .totalItems(3)
                .status("PLACED")
                .paymentMethod("CREDIT_CARD")
                .placedAt(placedAt)
                .paidAt(paidAt)
                .canceledAt(null)
                .readyAt(readyAt)
                .build();

        OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

        Order order = disassembler.toDomainEntity(persistenceEntity);

        assertEquals(new OrderId(123L), order.id());
        assertEquals(new CustomerId(customerId), order.customerId());
        assertEquals(new Money(BigDecimal.valueOf(150.00)), order.totalAmount());
        assertEquals(new Quantity(3), order.totalItems());
        assertEquals(OrderStatus.PLACED, order.status());
        assertEquals(PaymentMethod.CREDIT_CARD, order.paymentMethod());
        assertEquals(placedAt, order.placedAt());
        assertEquals(paidAt, order.paidAt());
        assertNull(order.canceledAt());
        assertEquals(readyAt, order.readyAt());
    }
}
