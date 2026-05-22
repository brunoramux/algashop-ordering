package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartUpdateProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({
        ShoppingCartUpdateProvider.class,
        ShoppingCartPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
class ShoppingCartUpdateProviderIT {
    private ShoppingCartUpdateProvider updateProvider;
    private ShoppingCartPersistenceProvider persistenceProvider;
    private CustomersPersistenceProvider customersPersistenceProvider;
    private ShoppingCartPersistenceEntityRepository entityRepository;

    @Autowired
    public ShoppingCartUpdateProviderIT(ShoppingCartUpdateProvider updateProvider,
                                        ShoppingCartPersistenceProvider persistenceProvider,
                                             CustomersPersistenceProvider customersPersistenceProvider,
                                             ShoppingCartPersistenceEntityRepository entityRepository) {
        this.persistenceProvider = persistenceProvider;
        this.customersPersistenceProvider = customersPersistenceProvider;
        this.entityRepository = entityRepository;
        this.updateProvider = updateProvider;
    }

    @BeforeEach
    public void setup() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(
                    CustomerTestDataBuilder.existingCustomer().build()
            );
        }
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void shouldUpdateItemPriceAndTotalAmount() {
        // cria ShoppingCart, inicialmente sem itens
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();

        // cria 2 produtos
        Product product1 = ProductTestDataBuilder.aProduct().price(new Money("2000")).build();
        Product product2 = ProductTestDataBuilder.aProductMacBook()
                .id(new ProductId(UUIDGenerator.generateTimeBasedUUID()))
                .price(new Money("200")).build();

        // adiciona os itens
        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));

        // persiste Shopping Cart
        persistenceProvider.add(shoppingCart);

        ProductId productIdToUpdate = product1.id();
        Money newProduct1Price = new Money("1500");
        Money expectedNewItemTotalPrice = newProduct1Price.multiply(new Quantity(2));
        Money expectedNewCartTotalAmount = expectedNewItemTotalPrice.add(new Money("200"));

        updateProvider.adjustPrice(productIdToUpdate, newProduct1Price);

        ShoppingCart updatedShoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        Assertions.assertThat(updatedShoppingCart.totalAmount()).isEqualTo(expectedNewCartTotalAmount);
        Assertions.assertThat(updatedShoppingCart.totalItems()).isEqualTo(new Quantity(3));

        ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);

        Assertions.assertThat(item.totalAmount()).isEqualTo(expectedNewItemTotalPrice);
        Assertions.assertThat(item.price()).isEqualTo(newProduct1Price);

    }
}