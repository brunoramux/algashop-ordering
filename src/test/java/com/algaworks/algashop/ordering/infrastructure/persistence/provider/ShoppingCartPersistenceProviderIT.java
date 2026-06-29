package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({
        ShoppingCartPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ShoppingCartPersistenceProviderIT {

    private ShoppingCartPersistenceProvider persistenceProvider;
    private CustomersPersistenceProvider customersPersistenceProvider;
    private ShoppingCartPersistenceEntityRepository entityRepository;

    @Autowired
    public ShoppingCartPersistenceProviderIT(ShoppingCartPersistenceProvider persistenceProvider,
                                              CustomersPersistenceProvider customersPersistenceProvider,
                                              ShoppingCartPersistenceEntityRepository entityRepository) {
        this.persistenceProvider = persistenceProvider;
        this.customersPersistenceProvider = customersPersistenceProvider;
        this.entityRepository = entityRepository;
    }

    // CRIA CUSTOMER ANTES DE CADA TESTE
    @BeforeEach
    public void setup() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(
                    CustomerTestDataBuilder.existingCustomer().build()
            );
        }
    }

    @Test
    public void shouldAddAndFindShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Assertions.assertThat(shoppingCart.version()).isNull();

        persistenceProvider.add(shoppingCart);

        Assertions.assertThat(shoppingCart.version()).isNotNull().isEqualTo(0L);

        ShoppingCart foundCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();
        Assertions.assertThat(foundCart).isNotNull();
        Assertions.assertThat(foundCart.id()).isEqualTo(shoppingCart.id());
        Assertions.assertThat(foundCart.totalItems().value()).isEqualTo(3);
    }

    @Test
    public void shouldRemoveShoppingCartById() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        persistenceProvider.add(shoppingCart);
        Assertions.assertThat(persistenceProvider.exists(shoppingCart.id())).isTrue();

        persistenceProvider.remove(shoppingCart.id());

        Assertions.assertThat(persistenceProvider.exists(shoppingCart.id())).isFalse();
        Assertions.assertThat(entityRepository.findById(shoppingCart.id().value())).isEmpty();
    }

    @Test
    public void shouldRemoveShoppingCartByEntity() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        persistenceProvider.add(shoppingCart);
        Assertions.assertThat(persistenceProvider.exists(shoppingCart.id())).isTrue();

        persistenceProvider.remove(shoppingCart);

        Assertions.assertThat(persistenceProvider.exists(shoppingCart.id())).isFalse();
    }

    @Test
    public void shouldFindShoppingCartByCustomerId() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .build();
        persistenceProvider.add(shoppingCart);

        ShoppingCart foundCart = persistenceProvider.ofCustomer(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).orElseThrow();

        Assertions.assertThat(foundCart).isNotNull();
        Assertions.assertThat(foundCart.customerId()).isEqualTo(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);
        Assertions.assertThat(foundCart.id()).isEqualTo(shoppingCart.id());
    }

    @Test
    public void shouldCorrectlyCountShoppingCarts() {
        long initialCount = persistenceProvider.count();

        ShoppingCart cart1 = ShoppingCartTestDataBuilder.aShoppingCart().build();
        persistenceProvider.add(cart1);

        Customer otherCustomer = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .email(new Email("other.customer@email.com"))
                .build();
        customersPersistenceProvider.add(otherCustomer);

        ShoppingCart cart2 = ShoppingCartTestDataBuilder.aShoppingCart().customerId(otherCustomer.id()).build();
        persistenceProvider.add(cart2);

        long finalCount = persistenceProvider.count();

        Assertions.assertThat(finalCount).isEqualTo(initialCount + 2);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldAddAndFindWhenNoTransaction() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        persistenceProvider.add(shoppingCart);

        Assertions.assertThatNoException().isThrownBy(() -> {
            ShoppingCart foundCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();
            Assertions.assertThat(foundCart).isNotNull();
        });
    }

}