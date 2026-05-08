package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class OrderPersistenceEntityDisassemblerTest {

    @Test
    void shouldDisassembleOrderPersistenceEntityToOrder() {

        Order order = OrderTestDataBuilder.anOrder()
                .withItems(true)
                .build();

        OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();

        OrderPersistenceEntity persistenceEntity = assembler.fromDomain(order);

        OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

        order = disassembler.toDomainEntity(persistenceEntity);

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.items()).hasSize(2);
        Assertions.assertThat(order.id().value().toLong()).isEqualTo(persistenceEntity.getId());
    }
}
