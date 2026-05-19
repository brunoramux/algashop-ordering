package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
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
class OrderPersistenceEntityDisassemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository customerRepository;

    @InjectMocks
    OrderPersistenceEntityAssembler assembler;

    @BeforeEach
    public void setup(){
        Mockito.when(customerRepository.getReferenceById(Mockito.any(UUID.class)))
                .then(a -> {
                    UUID customerId = a.getArgument(0, UUID.class);
                    return CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build();
                });
    }

    @Test
    void shouldDisassembleOrderPersistenceEntityToOrder() {

        Order order = OrderTestDataBuilder.anOrder()
                .withItems(true)
                .build();

        OrderPersistenceEntity persistenceEntity = assembler.fromDomain(order);

        OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

        order = disassembler.toDomainEntity(persistenceEntity);

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.items()).hasSize(2);
        Assertions.assertThat(order.id().value().toLong()).isEqualTo(persistenceEntity.getId());
    }
}
