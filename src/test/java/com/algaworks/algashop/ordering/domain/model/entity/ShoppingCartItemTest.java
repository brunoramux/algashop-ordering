package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartItemIncompatibleProductException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartItemTest {

    @Test
    void shouldCreateBrandNewShoppingCartItem() {
        ShoppingCartId shoppingCartId = new ShoppingCartId();
        ProductId productId = new ProductId();
        ProductName name = new ProductName("IPhone");
        Money price = new Money("3000.00");
        Quantity quantity = new Quantity(2);
        Boolean available = true;

        ShoppingCartItem item = ShoppingCartItem.brandNew()
                .shoppingCartId(shoppingCartId)
                .productId(productId)
                .name(name)
                .price(price)
                .quantity(quantity)
                .available(available)
                .build();

        Assertions.assertThat(item).isNotNull();
        Assertions.assertThat(item.id()).isNotNull();
        Assertions.assertThat(item.shoppingCartId()).isEqualTo(shoppingCartId);
        Assertions.assertThat(item.productId()).isEqualTo(productId);
        Assertions.assertThat(item.name()).isEqualTo(name);
        Assertions.assertThat(item.price()).isEqualTo(price);
        Assertions.assertThat(item.quantity()).isEqualTo(quantity);
        Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("6000.00")); // 3000 * 2
        Assertions.assertThat(item.isAvailable()).isEqualTo(available);
    }

    @Test
    void shouldRefreshShoppingCartItem() {
        ShoppingCartItem item = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Product updatedProduct = Product.builder()
                .id(item.productId())
                .name(new ProductName("IPhone Updated"))
                .price(new Money("3500.00"))
                .inStock(false)
                .build();

        item.refresh(updatedProduct);

        Assertions.assertThat(item.name()).isEqualTo(new ProductName("IPhone Updated"));
        Assertions.assertThat(item.price()).isEqualTo(new Money("3500.00"));
        Assertions.assertThat(item.isAvailable()).isFalse();
        Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("3500.00")); // recalculated
    }

    @Test
    void shouldThrowExceptionWhenRefreshingWithIncompatibleProduct() {
        ShoppingCartItem item = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Product incompatibleProduct = Product.builder()
                .id(new ProductId()) // different id
                .name(new ProductName("Different Product"))
                .price(new Money("1000.00"))
                .inStock(true)
                .build();

        Assertions.assertThatExceptionOfType(ShoppingCartItemIncompatibleProductException.class)
                .isThrownBy(() -> item.refresh(incompatibleProduct));
    }

    @Test
    void shouldChangeQuantityAndRecalculateTotal() {
        ShoppingCartItem item = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Quantity newQuantity = new Quantity(3);
        item.changeQuantity(newQuantity);

        Assertions.assertThat(item.quantity()).isEqualTo(newQuantity);
        Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("9000.00")); // 3000 * 3
    }

    @Test
    void shouldHandleZeroQuantity() {
        ShoppingCartItem item = ShoppingCartItem.brandNew()
                .shoppingCartId(new ShoppingCartId())
                .productId(new ProductId())
                .name(new ProductName("Test"))
                .price(new Money("100.00"))
                .quantity(new Quantity(0))
                .available(true)
                .build();

        Assertions.assertThat(item.totalAmount()).isEqualTo(Money.ZERO);
    }
}
