package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.domain.model.utility.TSIDGenerator;
import com.algaworks.algashop.ordering.domain.model.utility.UUIDGenerator;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;


public class OrderPersistenceEntityTestDataBuilder {

    public OrderPersistenceEntityTestDataBuilder() {
    }

    public static OrderPersistenceEntity.OrderPersistenceEntityBuilder  existingOrder() {

        long orderId = TSIDGenerator.generateTSID().toLong();
        OrderItemPersistenceEntity item1 = OrderItemPersistenceEntity.builder()
                .id(new OrderItemId(TSIDGenerator.generateTSID()).value().toLong())
                .productId(UUIDGenerator.generateTimeBasedUUID())
                .productName("IPhone 17 MAX")
                .price(BigDecimal.valueOf(10000.00))
                .quantity(2)
                .totalAmount(BigDecimal.valueOf(20000.00))
                .build();
        OrderItemPersistenceEntity item2 = OrderItemPersistenceEntity.builder()
                .id(new OrderItemId(TSIDGenerator.generateTSID()).value().toLong())
                .productId(UUIDGenerator.generateTimeBasedUUID())
                .productName("IPhone 17 MAX")
                .price(BigDecimal.valueOf(10000.00))
                .quantity(2)
                .totalAmount(BigDecimal.valueOf(20000.00))
                .build();

        return OrderPersistenceEntity.builder()
                .id(orderId)
                .customer(CustomerPersistenceEntityTestDataBuilder.aCustomer().build())
                .totalItems(4)
                .totalAmount(new BigDecimal(40000))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now())
                .items(Set.of(item1, item2));

    }

}
