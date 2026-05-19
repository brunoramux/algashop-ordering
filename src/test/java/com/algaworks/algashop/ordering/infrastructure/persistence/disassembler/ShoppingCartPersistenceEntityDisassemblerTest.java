package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ShoppingCartPersistenceEntityDisassemblerTest {

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
    void shouldDisassembleShoppingCartPersistenceEntityToShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withItems(true)
                .build();
        ShoppingCartPersistenceEntity persistenceEntity = assembler.fromDomain(shoppingCart);

        ShoppingCartPersistenceEntityDisassembler disassembler = new ShoppingCartPersistenceEntityDisassembler();

        ShoppingCart domainEntity = disassembler.toDomainEntity(persistenceEntity);

        Assertions.assertThat(domainEntity).isNotNull();
        Assertions.assertThat(domainEntity.items()).hasSize(2);
        Assertions.assertThat(domainEntity.id().value()).isEqualTo(persistenceEntity.getId());
    }
}
