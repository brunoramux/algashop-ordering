package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainItemException;
import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainProductException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class ShoppingCartTest {

    @Test
    void shouldStartShopping() {
        CustomerId customerId = new CustomerId();
        ShoppingCart cart = ShoppingCart.startShopping(customerId);

        Assertions.assertThat(cart).isNotNull();
        Assertions.assertThat(cart.id()).isNotNull();
        Assertions.assertThat(cart.customerId()).isEqualTo(customerId);
        Assertions.assertThat(cart.totalAmount()).isEqualTo(Money.ZERO);
        Assertions.assertThat(cart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(cart.createdAt()).isNotNull();
        Assertions.assertThat(cart.items()).isEmpty();
        Assertions.assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void shouldAddNewItem() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct().build();
        Quantity quantity = new Quantity(2);

        cart.addItem(product, quantity);

        Assertions.assertThat(cart.items()).hasSize(1);
        ShoppingCartItem item = cart.items().iterator().next();
        Assertions.assertThat(item.productId()).isEqualTo(product.id());
        Assertions.assertThat(item.quantity()).isEqualTo(quantity);
        Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("6000.00")); // 3000 * 2
        Assertions.assertThat(cart.totalAmount()).isEqualTo(new Money("6000.00"));
        Assertions.assertThat(cart.totalItems()).isEqualTo(new Quantity(2));
    }

    @Test
    void shouldUpdateExistingItemWhenAddingSameProduct() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct().build();

        cart.addItem(product, new Quantity(1));
        cart.addItem(product, new Quantity(2));

        Assertions.assertThat(cart.items()).hasSize(1);
        ShoppingCartItem item = cart.items().iterator().next();
        Assertions.assertThat(item.quantity()).isEqualTo(new Quantity(3));
        Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("9000.00")); // 3000 * 3
        Assertions.assertThat(cart.totalAmount()).isEqualTo(new Money("9000.00"));
        Assertions.assertThat(cart.totalItems()).isEqualTo(new Quantity(3));
    }

    @Test
    void shouldRefreshItem() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct().build();
        cart.addItem(product, new Quantity(1));

        Product updatedProduct = Product.builder()
                .id(product.id())
                .name(new ProductName("IPhone Updated"))
                .price(new Money("3500.00"))
                .inStock(false)
                .build();

        cart.refreshItem(updatedProduct);

        ShoppingCartItem item = cart.items().iterator().next();
        Assertions.assertThat(item.name()).isEqualTo(new ProductName("IPhone Updated"));
        Assertions.assertThat(item.price()).isEqualTo(new Money("3500.00"));
        Assertions.assertThat(item.isAvailable()).isFalse();
        Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("3500.00"));
        Assertions.assertThat(cart.totalAmount()).isEqualTo(new Money("3500.00"));
    }

    @Test
    void shouldChangeItemQuantity() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product product = ProductTestDataBuilder.aProduct().build();
        cart.addItem(product, new Quantity(1));

        ShoppingCartItem item = cart.items().iterator().next();
        cart.changeItemQuantity(item.id(), new Quantity(5));

        Assertions.assertThat(item.quantity()).isEqualTo(new Quantity(5));
        Assertions.assertThat(item.totalAmount()).isEqualTo(new Money("15000.00"));
        Assertions.assertThat(cart.totalAmount()).isEqualTo(new Money("15000.00"));
        Assertions.assertThat(cart.totalItems()).isEqualTo(new Quantity(5));
    }

    @Test
    void shouldFindItemById() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartItem item = cart.items().iterator().next();

        ShoppingCartItem found = cart.findItem(item.id());

        Assertions.assertThat(found).isEqualTo(item);
    }

    @Test
    void shouldThrowExceptionWhenItemNotFoundById() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> cart.findItem(new ShoppingCartItemId()));
    }

    @Test
    void shouldFindItemByProductId() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartItem item = cart.items().iterator().next();

        ShoppingCartItem found = cart.findItem(item.productId());

        Assertions.assertThat(found).isEqualTo(item);
    }

    @Test
    void shouldThrowExceptionWhenItemNotFoundByProductId() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainProductException.class)
                .isThrownBy(() -> cart.findItem(new ProductId()));
    }

    @Test
    void shouldCheckContainsUnavailableItems() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());
        Product availableProduct = ProductTestDataBuilder.aProduct().build();
        Product unavailableProduct = ProductTestDataBuilder.aProductUnavailable().build();

        cart.addItem(availableProduct, new Quantity(1));

        Assertions.assertThat(cart.containsUnavailableItems()).isFalse();

        cart.addItem(unavailableProduct, new Quantity(1));

        Assertions.assertThat(cart.containsUnavailableItems()).isTrue();
    }

    @Test
    void shouldBeEmptyWhenNoItems() {
        ShoppingCart cart = ShoppingCart.startShopping(new CustomerId());

        Assertions.assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void shouldNotBeEmptyWhenHasItems() {
        ShoppingCart cart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Assertions.assertThat(cart.isEmpty()).isFalse();
    }
}
