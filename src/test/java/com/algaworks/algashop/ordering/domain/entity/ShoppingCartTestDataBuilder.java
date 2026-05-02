package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class ShoppingCartTestDataBuilder {

    private CustomerId customerId = new CustomerId();
    private ShoppingCartId shoppingCartId = new ShoppingCartId();
    private Money totalAmount = new Money("16000.00");
    private Quantity totalItems = new Quantity(3);
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private Set<ShoppingCartItem> items = new HashSet<>();

    private boolean withItems = true;

    private ShoppingCartTestDataBuilder() {}

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCart build() {
        if (withItems) {
            items.add(ShoppingCartItemTestDataBuilder.aShoppingCartItem().build());
            items.add(ShoppingCartItemTestDataBuilder.aShoppingCartItemMacBook().build());
        }

        return ShoppingCart.existing()
                .id(shoppingCartId)
                .customerId(customerId)
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .createdAt(createdAt)
                .items(items)
                .build();
    }

    public ShoppingCartTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public ShoppingCartTestDataBuilder shoppingCartId(ShoppingCartId shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
        return this;
    }

    public ShoppingCartTestDataBuilder totalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public ShoppingCartTestDataBuilder totalItems(Quantity totalItems) {
        this.totalItems = totalItems;
        return this;
    }

    public ShoppingCartTestDataBuilder createdAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ShoppingCartTestDataBuilder items(Set<ShoppingCartItem> items) {
        this.items = items;
        return this;
    }

    public ShoppingCartTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }
}
