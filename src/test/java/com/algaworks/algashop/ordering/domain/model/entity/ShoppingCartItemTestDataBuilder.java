package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

public class ShoppingCartItemTestDataBuilder {

    private ShoppingCartItemTestDataBuilder() {
    }

    public static ShoppingCartItem.ExistingShoppingCartItemBuilder aShoppingCartItem() {
        return ShoppingCartItem.existing()
                .id(new ShoppingCartItemId())
                .shoppingCartId(new ShoppingCartId())
                .productId(new ProductId())
                .name(new ProductName("IPhone"))
                .price(new Money("3000.00"))
                .quantity(new Quantity(1))
                .totalAmount(new Money("3000.00"))
                .available(true);
    }

    public static ShoppingCartItem.ExistingShoppingCartItemBuilder aShoppingCartItemMacBook() {
        return ShoppingCartItem.existing()
                .id(new ShoppingCartItemId())
                .shoppingCartId(new ShoppingCartId())
                .productId(new ProductId())
                .name(new ProductName("MacBook"))
                .price(new Money("10000.00"))
                .quantity(new Quantity(1))
                .totalAmount(new Money("10000.00"))
                .available(true);
    }

    public static ShoppingCartItem.ExistingShoppingCartItemBuilder aShoppingCartItemUnavailable() {
        return ShoppingCartItem.existing()
                .id(new ShoppingCartItemId())
                .shoppingCartId(new ShoppingCartId())
                .productId(new ProductId())
                .name(new ProductName("Desktop I3"))
                .price(new Money("2000.00"))
                .quantity(new Quantity(1))
                .totalAmount(new Money("2000.00"))
                .available(false);
    }
}
