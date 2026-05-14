package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.domain.model.utility.TSIDGenerator;
import com.algaworks.algashop.ordering.domain.model.utility.UUIDGenerator;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public class ShoppingCartPersistenceEntityTestDataBuilder {

    public ShoppingCartPersistenceEntityTestDataBuilder() {
    }

    public static ShoppingCartPersistenceEntity.ShoppingCartPersistenceEntityBuilder existingShoppingCart() {
        long shoppingCartId = TSIDGenerator.generateTSID().toLong();
        ShoppingCartItemPersistenceEntity item1 = ShoppingCartItemPersistenceEntity.builder()
                .id(new ShoppingCartItemId(TSIDGenerator.generateTSID()).value().toLong())
                .productId(UUIDGenerator.generateTimeBasedUUID())
                .productName("IPhone")
                .price(new BigDecimal("3000.00"))
                .quantity(1)
                .totalAmount(new BigDecimal("3000.00"))
                .available(true)
                .build();
        ShoppingCartItemPersistenceEntity item2 = ShoppingCartItemPersistenceEntity.builder()
                .id(new ShoppingCartItemId(TSIDGenerator.generateTSID()).value().toLong())
                .productId(UUIDGenerator.generateTimeBasedUUID())
                .productName("MacBook")
                .price(new BigDecimal("10000.00"))
                .quantity(1)
                .totalAmount(new BigDecimal("10000.00"))
                .available(true)
                .build();

        return ShoppingCartPersistenceEntity.builder()
                .id(shoppingCartId)
                .customer(CustomerPersistenceEntityTestDataBuilder.aCustomer().build())
                .totalItems(2)
                .totalAmount(new BigDecimal("13000.00"))
                .createdAt(OffsetDateTime.now())
                .items(Set.of(item1, item2));
    }
}
