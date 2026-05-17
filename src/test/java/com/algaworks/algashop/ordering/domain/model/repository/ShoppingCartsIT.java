package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.ShoppingCartPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

@DataJpaTest
@Import({
        ShoppingCartPersistenceEntityDisassembler.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceProvider.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
})
class ShoppingCartsIT {

    private final ShoppingCarts shoppingCarts;
    private final Customers customers;

    @Autowired
    public ShoppingCartsIT(ShoppingCarts shoppingCarts, Customers customers) {
        this.shoppingCarts = shoppingCarts;
        this.customers = customers;
    }

    @Test
    void shouldPersistAndFind() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customer.id())
                .build();

        shoppingCarts.add(shoppingCart);

        Optional<ShoppingCart> possibleShoppingCart = shoppingCarts.ofId(shoppingCart.id());

        Assertions.assertThat(possibleShoppingCart).isPresent();
        Assertions.assertThat(possibleShoppingCart.get()).satisfies(
                s -> Assertions.assertThat(s.id()).isEqualTo(shoppingCart.id()),
                s -> Assertions.assertThat(s.customerId()).isEqualTo(shoppingCart.customerId()),
                s -> Assertions.assertThat(s.totalAmount()).isEqualTo(shoppingCart.totalAmount()),
                s -> Assertions.assertThat(s.items()).hasSameSizeAs(shoppingCart.items())
        );
    }

    @Test
    void shouldUpdateExistingShoppingCart() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customer.id())
                .build();

        shoppingCarts.add(shoppingCart);

        ShoppingCart savedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        ShoppingCartItem item = savedShoppingCart.items().iterator().next();
        savedShoppingCart.changeItemQuantity(item.id(), new Quantity(2));

        shoppingCarts.add(savedShoppingCart);

        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        Assertions.assertThat(updatedShoppingCart.findItem(item.id()).quantity()).isEqualTo(new Quantity(2));
    }

    @Test
    void shouldRemovePersistedItems() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customer.id())
                .build();

        shoppingCarts.add(shoppingCart);

        ShoppingCart savedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        ShoppingCartItem item = savedShoppingCart.items().iterator().next();
        savedShoppingCart.removeItem(item.id());

        shoppingCarts.add(savedShoppingCart);

        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        Assertions.assertThat(updatedShoppingCart.items()).hasSize(1);
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customer.id())
                .build();
        shoppingCarts.add(shoppingCart);

        ShoppingCart shoppingCart1 = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        ShoppingCart shoppingCart2 = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        ShoppingCartItem item1 = shoppingCart1.items().iterator().next();
        shoppingCart1.changeItemQuantity(item1.id(), new Quantity(2));
        shoppingCarts.add(shoppingCart1);

        ShoppingCartItem item2 = shoppingCart2.items().iterator().next();
        shoppingCart2.changeItemQuantity(item2.id(), new Quantity(3));

        Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> shoppingCarts.add(shoppingCart2));
    }

    @Test
    void shouldCountExistingShoppingCarts() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        Assertions.assertThat(shoppingCarts.count()).isZero();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customer.id())
                .build();
        ShoppingCart shoppingCart2 = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customer.id())
                .build();

        shoppingCarts.add(shoppingCart);
        shoppingCarts.add(shoppingCart2);

        Assertions.assertThat(shoppingCarts.count()).isEqualTo(2L);
    }

    @Test
    void shouldReturnIfShoppingCartExists() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customer.id())
                .build();
        shoppingCarts.add(shoppingCart);

        Assertions.assertThat(shoppingCarts.exists(shoppingCart.id())).isTrue();
        Assertions.assertThat(shoppingCarts.exists(new ShoppingCartId())).isFalse();
    }
}
