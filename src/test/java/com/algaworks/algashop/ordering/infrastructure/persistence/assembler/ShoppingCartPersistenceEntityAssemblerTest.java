package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ShoppingCartPersistenceEntityAssemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository customerRepository;

    @InjectMocks
    private ShoppingCartPersistenceEntityAssembler assembler;

    @BeforeEach
    public void setup() {
        Mockito.when(customerRepository.getReferenceById(Mockito.any(UUID.class)))
                .then(a -> {
                    UUID customerId = a.getArgument(0, UUID.class);
                    return CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build();
                });
    }

    @Test
    void shouldConvertToPersistenceEntity() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        ShoppingCartPersistenceEntity persistenceEntity = assembler.fromDomain(shoppingCart);

        Assertions.assertThat(persistenceEntity).satisfies(
                p -> Assertions.assertThat(p.getId()).isEqualTo(shoppingCart.id().value()),
                p -> Assertions.assertThat(p.getCustomerId()).isEqualTo(shoppingCart.customerId().value()),
                p -> Assertions.assertThat(p.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value()),
                p -> Assertions.assertThat(p.getTotalItems()).isEqualTo(shoppingCart.totalItems().value()),
                p -> Assertions.assertThat(p.getCreatedAt()).isEqualTo(shoppingCart.createdAt()),
                p -> Assertions.assertThat(p.getItems()).hasSameSizeAs(shoppingCart.items())
        );
    }

    @Test
    void givenShoppingCartWithNoItems_shouldRemovePersistenceEntityItems() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withItems(false)
                .build();
        ShoppingCartPersistenceEntity persistenceEntity = ShoppingCartPersistenceEntityTestDataBuilder
                .existingShoppingCart()
                .build();

        Assertions.assertThat(shoppingCart.items()).isEmpty();
        Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();

        assembler.merge(persistenceEntity, shoppingCart);

        Assertions.assertThat(persistenceEntity.getItems()).isEmpty();
    }

    @Test
    void givenShoppingCartWithItems_shouldAddToPersistenceEntity() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withItems(true)
                .build();
        ShoppingCartPersistenceEntity persistenceEntity = ShoppingCartPersistenceEntityTestDataBuilder
                .existingShoppingCart()
                .items(new HashSet<>())
                .build();

        Assertions.assertThat(shoppingCart.items()).isNotEmpty();
        Assertions.assertThat(persistenceEntity.getItems()).isEmpty();

        assembler.merge(persistenceEntity, shoppingCart);

        Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();
    }

    @Test
    void givenShoppingCartWithItems_whenMerge_shouldMergeCorrectly() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withItems(true)
                .build();

        Set<ShoppingCartItemPersistenceEntity> shoppingCartItems = shoppingCart.items().stream()
                .map(assembler::fromDomain)
                .collect(Collectors.toSet());

        ShoppingCartPersistenceEntity persistenceEntity = ShoppingCartPersistenceEntityTestDataBuilder
                .existingShoppingCart()
                .items(shoppingCartItems)
                .build();

        ShoppingCartItem shoppingCartItem = shoppingCart.items().iterator().next();
        shoppingCart.removeItem(shoppingCartItem.id());

        assembler.merge(persistenceEntity, shoppingCart);

        Assertions.assertThat(shoppingCart.items()).hasSize(1);
        Assertions.assertThat(persistenceEntity.getItems()).hasSize(1);
    }
}
